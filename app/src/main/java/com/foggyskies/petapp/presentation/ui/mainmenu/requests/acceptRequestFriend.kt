package com.foggyskies.petapp.presentation.ui.mainmenu.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.request
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun MenuViewModel.acceptFriend(idFriend: String){
    backgroundScope.launch {
        request<String, String>(
            data = idFriend,
            request = ::acceptRequestFriend,
            onOk = {

            },
            onError = {

            }
        )
    }
}

suspend fun acceptRequestFriend(idFriend: String): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + "/acceptRequestFriend"){
            contentType(ContentType.Text.Plain)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(idFriend)
        }
    }
}