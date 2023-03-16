package com.foggyskies.petapp.presentation.ui.chat.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.ChatRoute.CREATE_CHAT_SESSION
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

fun ChatViewModel.createChatSession(
    idChat: String
){
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<Unit>(
                    response = createChatSessionRequest(idChat),
                    onOk = {}
                )
            }
        )
    }
}

suspend fun createChatSessionRequest(idChat: String): HttpResponse {
    clientJson.use {
        return it.get(BASE_URL + CREATE_CHAT_SESSION + idChat){
            headers[BuildConfig.Authorization] = MainPreference.Token
        }
    }
}