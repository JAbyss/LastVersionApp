package com.foggyskies.petapp.workers.request

import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.routs.Routes.UploadSever.requestRegistration
import com.foggyskies.petapp.workers.UploadFileViewModel
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationUpload(
    var idUpload: Int?,
    val nameFile: String,
    val extension: String,
    val pathTo: String,
    var idInTaskManager: String?
)

suspend fun UploadFileViewModel.registrationUpload(body: RegistrationUpload): Result<String> {
    return httpRequest(_registrationUpload(body))
}

suspend fun _registrationUpload(
    body: RegistrationUpload
): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.post(requestRegistration) {
            contentType(ContentType.Application.Json)
            AuthHeader
            setBody(body)
        }
    }
}