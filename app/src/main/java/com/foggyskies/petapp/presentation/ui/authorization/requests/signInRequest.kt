package com.foggyskies.petapp.presentation.ui.authorization.requests

import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.authorization.models.LoginUserDC
import com.foggyskies.petapp.presentation.ui.authorization.models.SaveAuthData
import com.foggyskies.petapp.presentation.ui.authorization.models.Token
import com.foggyskies.petapp.routs.Routes.AuthServer.requestAuthSession
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

fun AuthorizationViewModel.auth(
    data: LoginUserDC,
    onOkNavigate: () -> Unit
) {
    backgroundScope.launch {
        val response = httpRequest<Token>(signInRaw(data))
        response.onSuccess { token ->
            saveData(
                SaveAuthData(
                    idToken = token.token,
//                                idUser = token.idUser,
                    username = login.value,
                    password = password.value
                )
            )
            viewModelScope.launch {
                onOkNavigate()
            }
        }
//        checkInternet(
//            request = {
//                cRequest<Token>(
//                    response = signInRequest(data),
//                    onOk = { token ->
//                        saveData(
//                            SaveAuthData(
//                                idToken = token.token,
////                                idUser = token.idUser,
//                                username = login.value,
//                                password = password.value
//                            )
//                        )
//                        viewModelScope.launch {
//                            onOkNavigate()
//                        }
//                    }
//                )
//            }
//        )
    }
}

suspend fun signInRaw(
    body: LoginUserDC
): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.post(requestAuthSession) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}