package com.foggyskies.petapp.presentation.ui.profile.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.CHANGE_AVATAR
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ProfileViewModel.changeAvatar(image: String){
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<String>(
                    response = changeAvatar(data = image),
                    onOk = {
                        withContext(Main){
                            initImageProfile = it
                        }
                    }
                )
            }
        )
    }
}

suspend fun changeAvatar(data: String): HttpResponse {
    clientJson.use {
        return it.post(BASE_URL + CHANGE_AVATAR) {
            contentType(ContentType.Text.Plain)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(data)
        }
    }
}