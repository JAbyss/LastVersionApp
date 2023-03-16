package com.foggyskies.petapp.presentation.ui.chat.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.ChatRoute.GET_MESSAGES
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

fun ChatViewModel.hundredMessages(idChat: String){

    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<List<ChatMessageDC>>(
                    response = getHundredMessages(idChat),
                    onOk = {
                        _state.addAll(it)
                        it.forEach { repositoryUserDB.dbUser.insertMessages(idChat, it) }
                    }
                )
            }
        )
    }
}

suspend fun getHundredMessages(idChat: String): HttpResponse {
    clientJson.use {
        return it.get(BASE_URL + GET_MESSAGES){
            header(BuildConfig.Authorization, MainPreference.Token)
            header("idChat", idChat)
        }
    }
}