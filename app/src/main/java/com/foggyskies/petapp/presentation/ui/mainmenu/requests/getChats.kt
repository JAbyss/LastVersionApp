package com.foggyskies.petapp.presentation.ui.mainmenu.requests

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.GET_CHATS
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun MenuViewModel.chats() {
    backgroundScope.launch {
        listChats.addAll(db.chatDao().getAllChats().map { it.toFormattedChat() })
        checkInternet(
            request = {
                cRequest<List<FormattedChatDC>>(
                    response = getChats(),
                    onOk = { resp ->
                        withContext(Main) {
                            resp.forEach {
                                if (!mapChats.containsKey(it.id)) {
                                    mapChats[it.id] = mutableStateOf(it)
                                } else
                                    mapChats[it.id]?.value = it
                            }
                        }
                    }
                )
            }
        )
    }
}

suspend fun getChats(): HttpResponse {
    clientJson.use {
        return it.get(Routes.SERVER.REQUESTS.BASE_URL + GET_CHATS) {
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
        }
    }
}