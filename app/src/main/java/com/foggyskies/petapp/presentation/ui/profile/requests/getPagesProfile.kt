package com.foggyskies.petapp.presentation.ui.profile.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.autoAddAndRemove
import com.foggyskies.petapp.presentation.ui.profile.PageProfileFormattedDC
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.PAGES_MY_PROFILE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun ProfileViewModel.myPagesProfile(){
    backgroundScope.launch {
        val response = httpRequest<List<PageProfileFormattedDC>>(getPagesProfileRaw())
        response.onSuccess {
            listPagesProfile.autoAddAndRemove(it)

        }
//        checkInternet(
//            {
//                cRequest<List<PageProfileFormattedDC>>(
//                    response = getPagesProfile(),
//                    onOk = {
//                        listPagesProfile.autoAddAndRemove(it)
//                    }
//                )
//            }
//        )
    }
}

private suspend fun getPagesProfileRaw(): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.get(BASE_URL + PAGES_MY_PROFILE){
            contentType(ContentType.Application.Json)
            AuthHeader
        }
    }
}