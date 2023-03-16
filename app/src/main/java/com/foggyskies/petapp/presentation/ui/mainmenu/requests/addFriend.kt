package com.foggyskies.petapp.presentation.ui.mainmenu.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun MenuViewModel.addFriend(idUser: String){
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<Unit>(
                    response = addFriendRequest(idUser),
                    onOk = {

                    }
                )
            }
        )
//        com.foggyskies.petapp.request<String, Unit>(
//            data = idUser,
//            request = ::addFriend,
//            onOk = {
//
//            },
//            onError = {
//
//            }
//        )
    }
}

suspend fun addFriendRequest(idUser: String): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + "/addFriend"){
            contentType(ContentType.Text.Plain)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(idUser)
        }
    }
}