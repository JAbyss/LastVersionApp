package com.foggyskies.petapp.presentation.ui.home.requests

import androidx.compose.runtime.toMutableStateList
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.home.HomeMVIModel
import com.foggyskies.petapp.requestGet
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun HomeMVIModel.friends() {
    requestGet<List<UserIUSI>>(
        response = getFriends(),
        onOk = {
//            val needAddItems: List<UserIUSI> = it - listFriends.toSet()
//            val deletedItems = listFriends - it.toSet()
            listFriends = it.toMutableStateList()
        },
        onError = {

        }
    )
}

suspend fun getFriends(): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + "/getFriends") {
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
        }
    }
}