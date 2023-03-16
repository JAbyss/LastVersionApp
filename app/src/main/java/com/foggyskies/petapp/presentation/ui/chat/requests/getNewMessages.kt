package com.foggyskies.petapp.presentation.ui.chat.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.ChatRoute.GET_NEW_MESSAGES
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

fun ChatViewModel.newMessages(idChat: String){

    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<List<ChatMessageDC>>(
                    response = getNewMessages(idChat),
                    onOk = { newMessagesResp ->
                        val reversed = newMessagesResp.reversed()
                        _state.addAll(0, reversed)
                        reversed.forEach { repositoryUserDB.insertMessage(idChat, it) }
                    }
                )
            }
        )
    }
}

suspend fun getNewMessages(idChat: String): HttpResponse {
    clientJson.use {
        return it.get(Routes.SERVER.REQUESTS.BASE_URL + GET_NEW_MESSAGES){
            header(BuildConfig.Authorization, MainPreference.Token)
            header("idChat", idChat)
        }
    }
}