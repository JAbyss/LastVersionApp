package com.foggyskies.petapp.workers

import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val nameFile = inputData.getString("nameFile")!!
        val dirFile = inputData.getString("dirFile")!!
        val idChat = inputData.getString("idChat")!!
        // Do the work here--in this case, upload the images.
//        uploadImages()
        CoroutineScope(Dispatchers.IO).launch {

            uploadFile(nameFile, dirFile, idChat)
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun generateUUID(countSimbols: Int): String {
        var string = ""
        repeat(countSimbols) {
            string += Random.nextInt(from = 32, until = 126).toChar()
        }
        return string
    }

    suspend fun uploadFile(
        nameFile: String,
        dirFile: String,
        idChat: String
    ) {
        val file = File(dirFile + nameFile)
//        val file = File("/storage/emulated/0/Download/Viena.zip")
        val typeFile = "\\w+\$".toRegex().find(nameFile)?.value!!
        val name = "^\\w+".toRegex().find(nameFile)?.value!!

        if (file.isFile) {
            val codeOperation = generateUUID(5)
            val nameOperation = "fileSending|$codeOperation|"

            val client = HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000000
                }
            }

            file.inputStream()
                .use { input ->
                    var arr =
                        if (file.length() / 8 < 4096) ByteArray((file.length() / 8).toInt()) else ByteArray(
                            4096000
                        )
                    var allReaded = 0L
                    val maxSize = file.length()
                    do {
                        val size =
                            if (maxSize - allReaded < arr.size) {
                                println("Check ${maxSize - allReaded}")
                                arr = ByteArray((maxSize - allReaded).toInt())
//                    println(a.size)
                                input.read(arr)
                            } else
                                input.read(arr)
                        if (size <= 0) {
                            println(allReaded)
                            break
                        } else {
                            allReaded += size
                            val string = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                java.util.Base64.getEncoder().encodeToString(arr)
                            } else {
                                android.util.Base64.encodeToString(arr, android.util.Base64.NO_WRAP)
                            }

                            client.post<HttpResponse>("${Routes.SERVER.REQUESTS.BASE_URL}/subscribes/fileUpload") {
//                                    headers["Auth"] = MainActivity.TOKEN
                                headers["Content-Type"] = "Application/Json"
//                        parameter("idChat", chatEntity?.id)
                                body = BodyFile(
                                    idChat = idChat,
                                    nameFile = name,
                                    contentFile = string,
                                    status = if (allReaded == maxSize) "finish" else "",
                                    idUser = IDUSER,
                                    typeFile = typeFile
                                )
//                                    parameter("idChat", "629275cb1372bb3eb625641b")
//                                    parameter("nameFile", nameFile)
//                                    parameter("contentFile", string)
//                                    parameter("status", if (allReaded == maxSize) "finish" else "")
//                                    parameter("idUser", "62913e7fcc47483b16951822")
//                                    parameter("typeFile", typeFile)
//                                    body = DeleteMessageEntity(
//                                        idUser = if (messageSelected?.idUser!! == MainActivity.IDUSER)
//                                            chatEntity?.idCompanion!!
//                                        else
//                                            MainActivity.IDUSER,
//                                        idChat = chatEntity?.id!!,
//                                        idMessage = messageSelected?.id!!
//                                    )
                            }
                        }
                    } while (true)
                }
        }
    }
}

@kotlinx.serialization.Serializable
data class BodyFile(
    val idChat: String,
    val nameFile: String,
    val contentFile: String,
    val status: String,
    val idUser: String,
    val typeFile: String
)