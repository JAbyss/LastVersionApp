package com.foggyskies.petapp.presentation.ui.mainmenu.requests

import android.os.Bundle
import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import androidx.core.os.bundleOf
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.ChatRoute.CREATE_CHAT
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun MenuViewModel.createChat(
    item: UserIUSI,
    crossinline navigate: (Bundle) -> Unit
    ) {
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<String>(
                    response = createChatRequest(item.id),
                    onOk = {
                        val formattedChat = FormattedChatDC(
                            id = it,
                            nameChat = item.username,
                            idCompanion = item.id,
                            image = item.image
                        )
                        val string = Json.encodeToString(formattedChat)
                        val b = bundleOf("itemChat" to string)
                        withContext(Main){
                            navigate(b)
                        }
                    }
                )
            }
        )
    }
}

suspend fun createChatRequest(idSecondUser: String): HttpResponse {
    clientJson.use {
        return it.post(BASE_URL + CREATE_CHAT) {
            contentType(ContentType.Text.Plain)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(idSecondUser)
        }
    }
}