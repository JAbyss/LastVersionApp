package com.foggyskies.petapp.presentation.ui.mainmenu.requests

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun MenuViewModel.requestFriends(){
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<List<UserIUSI>>(
                    response = getRequestsFriends(),
                    onOk = {
                        withContext(Main){
                            listRequestsFriends = it.toMutableStateList()
                        }
                    }
                )
            }
        )
    }
}

suspend fun getRequestsFriends(): HttpResponse {
    clientJson.use {
        return it.get(Routes.SERVER.REQUESTS.BASE_URL + "/getRequestsFriends"){
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
        }
    }
}