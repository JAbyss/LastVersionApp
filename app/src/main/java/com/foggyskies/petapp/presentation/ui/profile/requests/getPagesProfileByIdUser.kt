package com.foggyskies.petapp.presentation.ui.profile.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.autoAddAndRemove
import com.foggyskies.petapp.presentation.ui.profile.PageProfileFormattedDC
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.PAGES_OTHER_PROFILE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ProfileViewModel.pagesProfileByIdUser(idUser: String){
    backgroundScope.launch {
        val response = httpRequest<List<PageProfileFormattedDC>>(getPagesProfileByIdUserRaw(idUser))
        response.onSuccess {
            withContext(Main){
                listPagesProfile.autoAddAndRemove(it)
            }
        }
//        checkInternet(
//            request = {
//                cRequest<List<PageProfileFormattedDC>>(
//                    response = getPagesProfileByIdUser(idUser),
//                    onOk = {
//                        withContext(Main){
//                            listPagesProfile.autoAddAndRemove(it)
//                        }
//                    }
//                )
//            }
//        )
    }
}

private suspend fun getPagesProfileByIdUserRaw(idUser: String): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.get(BASE_URL + PAGES_OTHER_PROFILE){
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
            parameter("idUser", idUser)
        }
    }
}