package com.foggyskies.petapp.presentation.ui.home.requests

import androidx.compose.runtime.toMutableStateList
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.home.HomeMVIModel
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.requestGet
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

//suspend fun HomeMVIModel.chats(){
//
//    requestGet<List<FormattedChatDC>>(
//        response = getChats(),
//        onOk = {
//            listChats = it.toMutableStateList()
//        },
//        onError = {
//
//        }
//    )
//}
//
//private suspend fun getChats(): Result<HttpResponse> = runCatching {
//    clientJson.use {
//        return@use it.get(Routes.SERVER.REQUESTS.BASE_URL + "/getChat"){
//            contentType(ContentType.Application.Json)
//            headers[BuildConfig.Authorization] = MainPreference.Token
//        }
//    }
//}