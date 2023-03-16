package com.foggyskies.petapp.presentation.ui.chat.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.chat.DeleteMessageEntity
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.ChatRoute.DELETE_MESSAGE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ChatViewModel.deleteMessage() {
    backgroundScope.launch {
        checkInternet(request = {
            val message = DeleteMessageEntity(
                idUser = if (messageSelected?.idUser!! == MainPreference.IdUser)
                    chatEntity?.idCompanion!!
                else
                    MainPreference.IdUser,
                idChat = chatEntity?.id!!,
                idMessage = messageSelected?.id!!
            )
            cRequest<Unit>(
                response = deleteMessageRequest(message),
                onOk = {
                    withContext(Main){
                        repositoryUserDB.deleteMessage(chatEntity?.id!!, messageSelected?.id!!)
                        _state.remove(messageSelected)
                        messageSelected = null
                    }
                }
            )
        })
    }
}

private suspend fun deleteMessageRequest(message: DeleteMessageEntity): HttpResponse {
    clientJson.use {
        return it.post(BASE_URL + DELETE_MESSAGE) {
            contentType(ContentType.Application.Json)
            header(BuildConfig.Authorization, MainPreference.Token)
            setBody(message)
        }
    }
}