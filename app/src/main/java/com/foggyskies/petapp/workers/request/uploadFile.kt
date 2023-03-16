package com.foggyskies.petapp.workers.request

import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.routs.Routes.UploadSever.requestUploading
import com.foggyskies.petapp.workers.UploadFileViewModel
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class UploadingFile(
    var idUpload: Int,
    val data: String
)

suspend fun UploadFileViewModel.uploadFile(body: UploadingFile): Result<Unit> {
    return httpRequest<Unit>(_uploadFile(body))
}

suspend fun _uploadFile(
    body: UploadingFile
): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.post(requestUploading) {
            contentType(ContentType.Application.Json)
            AuthHeader
            setBody(body)
        }
    }
}

