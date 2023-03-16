package com.foggyskies.petapp.workers

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.foggyskies.petapp.presentation.ui.chat.MessageDC
import com.foggyskies.petapp.presentation.ui.chat.customui.getSizeFile
import com.foggyskies.petapp.presentation.ui.chat.entity.FileDC
import com.foggyskies.petapp.presentation.ui.chat.requests.messageWithContent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File
import kotlin.reflect.KSuspendFunction0

sealed class StateDownload()

data class FirstState(
    val action: KSuspendFunction0<Unit>
) : StateDownload()

data class SecondState(
    val action: () -> Unit
) : StateDownload()

@OptIn(ExperimentalCoroutinesApi::class)
class UploadFileViewModel : ViewModel() {

    val uploadScope = CoroutineScope(IO + SupervisorJob())

    val sharedFiles = MutableSharedFlow<BodyFileQueue>(
        replay = 0,
        extraBufferCapacity = 20,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    var nowPercentage = mutableStateOf(100)

    val files = mutableStateListOf<FileDC>()

    val nowUploadingFile = mutableStateOf<FileDC?>(null)

    val alphaForMini =
        androidx.compose.animation.core.Animatable(0.2f)

    val stateDownload = mutableStateOf<StateDownload>(FirstState(action = ::isClicked))

    val fullDownloadVisible = mutableStateOf(false)
    val miniDownloadVisible = derivedStateOf {
        !fullDownloadVisible.value && nowUploadingFile.value != null
    }

    suspend fun isClicked() {
        alphaForMini.animateTo(1f)
        stateDownload.value = SecondState(action = ::secondClick)
        delay(1500)
        if (!fullDownloadVisible.value)
            stateDownload.value = FirstState(action = ::isClicked)
        alphaForMini.animateTo(0.2f)
    }

    private fun secondClick() {
        fullDownloadVisible.value = true
    }

    fun closeFullDownload() {
        fullDownloadVisible.value = false
    }

    //    private val queue = ConcurrentHashMap<String, BodyFileQueue>()
    private val queue = mutableStateMapOf<String, BodyFileQueue>()

    private var nowLoadingDeferred: Deferred<FileDC>? = null

    val listQueueFile = derivedStateOf {
        queue.values.toMutableList().apply {
            if (size > 0)
                removeAt(0)
            else
                closeFullDownload()
        }
    }

    init {
        uploadScope.launch {
            sharedFiles.collect {
                try {
                    nowUploadingFile.value = it.toFileDC()
                    val uploading = async {
                        suspendUploadFilesToChat(it) ?: throw error("Uploading fail, return null")
                    }
                    nowLoadingDeferred = uploading
                    nowLoadingDeferred!!.join()
                    if (!uploading.isCancelled) {
                        val result = uploading.getCompleted()
                        messageWithContent(
                            message = MessageDC(
                                listFiles = listOf(result),
                                message = ""
                            ),
                            it.infoData
                        )
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("UPLOADING", e.toString())
                } finally {
                    queue.remove(it.idUpload)
                    if (queue.values.isNotEmpty()) {
                        sharedFiles.emit(queue.values.first())
                    } else {
                        nowUploadingFile.value = null
                    }
                    nowPercentage.value = 0
                }
            }
        }
    }

    fun addFileToQueue(body: BodyFileQueue) {
        if (queue.size == 0) {
            queue[body.idUpload] = body
            sharedFiles.tryEmit(body)
        } else {
            queue[body.idUpload] = body
        }
    }

    fun cancelNowLoadAndStartNext() {
        nowLoadingDeferred?.cancel()
    }

    fun removeFileInQueue(idUpload: String) {
        queue.remove(idUpload)
    }


    private suspend fun suspendUploadFilesToChat(
        bodyFileQueue: BodyFileQueue,
        isCompressed: Boolean = false,
    ): FileDC? {
        val bodyFile = bodyFileQueue.toBodyFile()
        val readyPath = uploadFile(bodyFileQueue.file, bodyFile, nowPercentage)
        readyPath?.let {
            return bodyFileQueue.file.run {
                FileDC(
                    name = nameWithoutExtension,
                    size = getSizeFile(length()),
                    type = extension,
                    path = "readyPath"
                )
            }
        }
        return null
    }

    fun uploadImagesToChat(
        message: String = "",
        idChat: String,
        vararg dirFile: String,
        typeLoad: TypeLoadFile,
        infoData: String,
        isCompressed: Boolean = true,
    ) {
        uploadScope.launch {
            try {
                val readyPaths = dirFile.map { pathToFile ->
                    val body = BodyFile.generate(pathToFile, typeLoad, infoData)
                    uploadFile(
                        File(pathToFile),
                        body,
                        isCompressed = isCompressed,
                    ) ?: throw error("Error, result null")
                }

                messageWithContent(
                    message = MessageDC(
                        listImages = emptyList(),
                        message = message
                    ),
//                    "readyPaths"
                    idChat
                )
            } catch (e: java.lang.Exception) {
                Log.e("UPLOAD_IMAGES_TO_CHAT", e.toString())
            }
        }
    }
}