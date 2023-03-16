package com.foggyskies.petapp.presentation.ui.splashscreen.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.authorization.models.LoginUserDC
import com.foggyskies.petapp.presentation.ui.authorization.models.Token
import com.foggyskies.petapp.presentation.ui.authorization.requests.saveToken
import com.foggyskies.petapp.presentation.ui.authorization.requests.signInRaw
import com.foggyskies.petapp.routs.Routes.AuthServer.requestVerifyToken
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.withContext

suspend fun checkToken(
    data: LoginUserDC,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val response = httpRequest<Unit>(checkTokenRaw())
    response.onSuccess {
        withContext(Main) {
            onSuccess()
        }
    }.onFailure {
        val response = httpRequest<Token>(signInRaw(data))
        response.onSuccess {
            saveToken(it.token)
            withContext(Main) {
                onSuccess()
            }
        }.onFailure {
            withContext(Main) {
                onFailure()
            }
        }
    }
}

suspend fun checkTokenRaw(): Result<HttpResponse> = runCatching {
    clientJson.use {
        it.post(requestVerifyToken) {
            AuthHeader
        }
    }
}

//suspend inline fun checkToken(
//    onOk: (Unit) -> Unit,
//    onError: (HttpResponse) -> Unit
//) {
//    checkInternet(
//        request = {
//            cRequest<Unit>(
//                response = checkOnExistToken(),
//                onOk = onOk,
//                onError = onError
//            )
//        }
//    )
//}
//
//suspend fun auth(
//    data: LoginUserDC,
//    onOk: () -> Unit,
//    onError: (HttpResponse) -> Unit
//) {
//    checkInternet(
//        request = {
//            cRequest<Token>(
//                response = signInRequest(data),
//                onOk = { token ->
//                    saveData(
//                        SaveAuthData(
//                            idToken = token.token,
////                            idUser = token.idUser,
//                            username = data.username,
//                            password = PasswordCoder.decodeStringFS(data.password)
//                        )
//                    )
//                    onOk()
//                },
//                onError = onError
//            )
//        }
//    )
//}
//
//suspend fun checkOnExistToken(): HttpResponse {
//    clientJson.use {
//        return it.get(BASE_URL + CHECK_TOKEN) {
//            headers[BuildConfig.Authorization] = MainPreference.Token
//        }
//    }
//}