package com.foggyskies.petapp.presentation.ui.authorization.requests

import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.authorization.models.RegistrationUserDC
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Auth.GENERATE_CODE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun AuthorizationViewModel.generateCode(
    data: RegistrationUserDC,
    onOk: () -> Unit
) {
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<Unit>(
                    response = generateCodeRequest(data),
                    onOk = {
                        onOk()
                    }
                )
            }
        )
    }
}

suspend fun generateCodeRequest(
    body: RegistrationUserDC
): HttpResponse {
    clientJson.use {
        val response: HttpResponse =
            it.post(Routes.SERVER.REQUESTS.BASE_URL + GENERATE_CODE) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        return response
    }
}