package com.foggyskies.petapp.presentation.ui.authorization.requests

import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.authorization.models.RegistrationUserWithCodeDC
import com.foggyskies.petapp.presentation.ui.authorization.models.SaveAuthData
import com.foggyskies.petapp.presentation.ui.authorization.models.Token
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

fun AuthorizationViewModel.registration(
    data: RegistrationUserWithCodeDC,
    onOk: suspend () -> Unit
) {
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<Token>(
                    response = signUpRequest(data),
                    onOk = { token ->
                        saveData(
                            SaveAuthData(
                                idToken = token.id,
                                idUser = token.idUser,
                                username = login.value,
                                password = password.value
                            )
                        )
                        CoroutineScope(Main).launch {
                            onOk()
                        }
                    }
                )
            }
        )
    }
}

suspend fun signUpRequest(
    body: RegistrationUserWithCodeDC
): HttpResponse {
    clientJson.use {
        val response: HttpResponse =
            it.post("${Routes.SERVER.REQUESTS.BASE_URL}/registration") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        return response
    }
}