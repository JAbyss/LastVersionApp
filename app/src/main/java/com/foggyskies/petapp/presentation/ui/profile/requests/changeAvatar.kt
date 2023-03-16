package com.foggyskies.petapp.presentation.ui.profile.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
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
        val response = httpRequest<String>(changeAvatarRaw(image))
        response.onSuccess {
            withContext(Main){
                initImageProfile = it
            }
        }
//        checkInternet(
//            request = {
//                cRequest<String>(
//                    response = changeAvatar(data = image),
//                    onOk = {
//                        withContext(Main){
//                            initImageProfile = it
//                        }
//                    }
//                )
//            }
//        )
    }
}

private suspend fun changeAvatarRaw(data: String): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.post(BASE_URL + CHANGE_AVATAR) {
            contentType(ContentType.Text.Plain)
            AuthHeader
            setBody(data)
        }
    }
}