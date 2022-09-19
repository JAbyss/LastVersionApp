package com.foggyskies.petapp.presentation.ui.splashscreen.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.PasswordCoder
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.authorization.models.LoginUserDC
import com.foggyskies.petapp.presentation.ui.authorization.models.SaveAuthData
import com.foggyskies.petapp.presentation.ui.authorization.models.Token
import com.foggyskies.petapp.presentation.ui.authorization.requests.saveData
import com.foggyskies.petapp.presentation.ui.authorization.requests.signInRequest
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Auth.CHECK_TOKEN
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend inline fun checkToken(
    onOk: (Unit) -> Unit,
    onError: (HttpResponse) -> Unit
) {
    checkInternet(
        request = {
            cRequest<Unit>(
                response = checkOnExistToken(),
                onOk = onOk,
                onError = onError
            )
        }
    )
}

suspend fun auth(
    data: LoginUserDC,
    onOk: () -> Unit,
    onError: (HttpResponse) -> Unit
) {
    checkInternet(
        request = {
            cRequest<Token>(
                response = signInRequest(data),
                onOk = { token ->
                    saveData(
                        SaveAuthData(
                            idToken = token.id,
                            idUser = token.idUser,
                            username = data.username,
                            password = PasswordCoder.decodeStringFS(data.password)
                        )
                    )
                    onOk()
                },
                onError = onError
            )
        }
    )
}

suspend fun checkOnExistToken(): HttpResponse {
    clientJson.use {
        return it.get(BASE_URL + CHECK_TOKEN) {
            headers[BuildConfig.Authorization] = MainPreference.Token
        }
    }
}