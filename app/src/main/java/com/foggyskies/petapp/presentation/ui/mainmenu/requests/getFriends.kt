package com.foggyskies.petapp.presentation.ui.mainmenu.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.GET_FRIENDS
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun MenuViewModel.friends() {
    backgroundScope.launch {
        listFriends.addAll(db.friendDao().getFriends().map { it.toIUSI() })
        checkInternet(
            request = {
                cRequest<List<UserIUSI>>(
                    response = getFriends(),
                    onOk = {
                        withContext(Main){
                            val needAddItems: List<UserIUSI> = it - listFriends.toSet()
                            val deletedItems = listFriends - it.toSet()
                            listFriends.addAll(needAddItems)
                            listFriends.removeAll(deletedItems.toSet())
                        }
                    }
                )
            }

        )
    }
}

suspend fun getFriends(): HttpResponse {
    clientJson.use {
        return it.get(Routes.SERVER.REQUESTS.BASE_URL + GET_FRIENDS) {
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
        }
    }
}