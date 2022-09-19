package com.foggyskies.petapp.workers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.runtime.MutableState
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.FILE_LOAD
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

suspend fun uploadFile(
    file: File,
    bodyFile: BodyFile,
    nowPercentage: MutableState<Int>? = null,
    isCompressed: Boolean = false
): String? {

    return if (isCompressed) {
        compressedUpload(file, bodyFile)
    } else
        fullFileLoad(file, bodyFile) { percentage ->
            nowPercentage?.value = percentage
        }
}

suspend fun compressedUpload(file: File, bodyFile: BodyFile, partSend: (Int) -> Unit = {}): String? {
    val bitmap = BitmapFactory.decodeFile(file.path)
    val bao = ByteArrayOutputStream()

    val MAX_SIZE = 1920

    val isHeight = bitmap.height > bitmap.width
    val max = max(bitmap.height, bitmap.width)

    val (height, width) = if (max > MAX_SIZE) {
        val ratio =
            if (isHeight) bitmap.height / bitmap.width.toFloat() else bitmap.width / bitmap.height.toFloat()
        if (isHeight) MAX_SIZE to MAX_SIZE / ratio else MAX_SIZE / ratio to MAX_SIZE
    } else bitmap.height to bitmap.width

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bao)

    val bytes = bao.toByteArray()
    val string = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        java.util.Base64.getEncoder().encodeToString(bytes)
    } else {
        android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }
    checkInternet(
        request = {
            cRequest<HttpResponse>(
                response = uploadFile(
                    bodyFile.copy(
                        contentFile = string,
                        status = "finish"
                    )
                ),
                onOk = {
                    if (it.status.value == 201) {
                        return it.bodyAsText()
                    }
                }
            )
        }
    )
    return null
}

suspend fun fullFileLoad(file: File, bodyFile: BodyFile, partSend: (Int) -> Unit = {}): String? {
    file.inputStream()
        .use { input ->
            var arr =
                if (file.length() < 409600) ByteArray((file.length()).toInt()) else ByteArray(
                    409600
                )
            var allReaded = 0L
            val maxSize = file.length()
            do {
                val size =
                    if (maxSize - allReaded < arr.size) {
                        println("Check ${maxSize - allReaded}")
                        arr = ByteArray((maxSize - allReaded).toInt())
                        input.read(arr)
                    } else
                        input.read(arr)
                if (size <= 0) {
                    println(allReaded)
                    break
                } else {
                    allReaded += size
                    partSend(((allReaded.toDouble() / maxSize.toDouble()) * 100).toInt())
                    val string = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        java.util.Base64.getEncoder().encodeToString(arr)
                    } else {
                        android.util.Base64.encodeToString(arr, android.util.Base64.NO_WRAP)
                    }
                    checkInternet(
                        request = {
                            cRequest<HttpResponse>(
                                response = uploadFile(
                                    bodyFile.copy(
                                        contentFile = string,
                                        status = if (allReaded == maxSize) "finish" else ""
                                    )
                                ),
                                onOk = {
                                    if (it.status.value == 201) {
                                        return it.bodyAsText()
                                    }
                                }
                            )
                        }
                    )
                }
            } while (true)
        }
    return null
}

private suspend fun uploadFile(fileBody: BodyFile): HttpResponse {
    clientJson.use {
        return it.post("http://109.195.147.44:38142$FILE_LOAD") {
            contentType(ContentType.Application.Json)
            header(BuildConfig.Authorization, MainPreference.Token)
            setBody(fileBody)
        }
    }
}