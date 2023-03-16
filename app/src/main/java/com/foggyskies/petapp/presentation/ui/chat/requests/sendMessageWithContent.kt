package com.foggyskies.petapp.presentation.ui.chat.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.chat.MessageDC
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.ChatRoute.MESSAGE_WITH_CONTENT
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun messageWithContent(message: MessageDC, idChat: String){
        checkInternet(
            request = {
                cRequest<Unit>(
                    response = sendMessageWithContent(message, idChat),
                    onOk = {

                    }
                )
            }
        )
}

suspend fun sendMessageWithContent(message: MessageDC, idChat: String): HttpResponse {
    clientJson.use {
        return it.post( BASE_URL + MESSAGE_WITH_CONTENT){
            contentType(ContentType.Application.Json)
            header(BuildConfig.Authorization, MainPreference.Token)
            header("idChat", idChat)
            setBody(message)
        }
    }
}