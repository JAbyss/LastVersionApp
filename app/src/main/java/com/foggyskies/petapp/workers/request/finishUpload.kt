package com.foggyskies.petapp.workers.request

import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.routs.Routes.UploadSever.requestFinish
import com.foggyskies.petapp.workers.UploadFileViewModel
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

suspend fun UploadFileViewModel.finishUpload(body: Int): Result<String> {
    return httpRequest<String>(_finishUploadFile(body))
}

private suspend fun _finishUploadFile(
    body: Int
): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.post(requestFinish) {
            contentType(ContentType.Application.Json)
            AuthHeader
            setBody(body)
        }
    }
}