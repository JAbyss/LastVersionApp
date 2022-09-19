package com.foggyskies.petapp.presentation.ui.home.widgets.post.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Content.LIKED_USERS
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun PostScreenHandler.likedUsers(){
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<List<UserIUSI>>(
                    response = getLikedUsers(idPageAndPost),
                    onOk = {
                        likedUsersList = it
                    },
                    onError = {

                    }
                )
            },
            networkError = {

            }
        )
    }
}

suspend fun getLikedUsers(data: IdPageAndPost): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + LIKED_USERS){
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(data)
        }
    }
}