package com.foggyskies.petapp.presentation.ui.profile.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.AVATAR
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

fun ProfileViewModel.myAvatar(){
    backgroundScope.launch {
        val response = httpRequest<String>(getAvatarRaw())
        response.onSuccess {
            initImageProfile = it
        }
//        checkInternet(
//            request = {
//                cRequest<String>(
//                    response = getAvatar(),
//                    onOk = {
////                        humanPhoto = it
//                        initImageProfile = it
//                    }
//                )
//            }
//        )
    }
}

private suspend fun getAvatarRaw(): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.get(BASE_URL + AVATAR){
            AuthHeader
        }
    }
}