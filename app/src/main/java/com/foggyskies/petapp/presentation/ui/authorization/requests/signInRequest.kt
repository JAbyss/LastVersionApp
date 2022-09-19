package com.foggyskies.petapp.presentation.ui.authorization.requests

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.authorization.models.LoginUserDC
import com.foggyskies.petapp.presentation.ui.authorization.models.SaveAuthData
import com.foggyskies.petapp.presentation.ui.authorization.models.Token
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

fun AuthorizationViewModel.auth(
    data: LoginUserDC,
    onOkNavigate: () -> Unit
) {
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<Token>(
                    response = signInRequest(data),
                    onOk = { token ->
                        saveData(
                            SaveAuthData(
                                idToken = token.id,
                                idUser = token.idUser,
                                username = login.value,
                                password = password.value
                            )
                        )
                        viewModelScope.launch {
                            onOkNavigate()
                        }
                    }
                )
            }
        )
    }
}

suspend fun signInRequest(
    body: LoginUserDC
): HttpResponse {
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
    }.use {
        return it.post("${Routes.SERVER.REQUESTS.BASE_URL}/auth") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}