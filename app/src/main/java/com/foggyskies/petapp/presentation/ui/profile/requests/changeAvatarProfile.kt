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
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.CHANGE_AVATAR_PROFILE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ProfileViewModel.changeAvatarProfile(pathToFile: String, idPage: String) {
    backgroundScope.launch {
        val response = httpRequest<String>(changeAvatarProfileRaw(idPage, pathToFile))
        response.onSuccess {
            withContext(Main) {
                initImagePageProfile = it

                listPagesProfile.forEach { page ->
                    if (page.id == selectedPage.id) {
                        page.image = initImagePageProfile
                        return@forEach
                    }
                }
            }
        }
//        checkInternet(
//            request = {
//                cRequest<String>(
//                    response = changeAvatarProfileRequest(idPage, pathToFile),
//                    onOk = {
//                        withContext(Main) {
//                            initImagePageProfile = it
//
//                            listPagesProfile.forEach { page ->
//                                if (page.id == selectedPage.id) {
//                                    page.image = initImagePageProfile
//                                    return@forEach
//                                }
//                            }
//                        }
//                        //todo Надо переделать
//                    }
//                )
//            }
//        )
    }
}

private suspend fun changeAvatarProfileRaw(idPage: String, pathToFile: String): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.post(BASE_URL + CHANGE_AVATAR_PROFILE) {
            AuthHeader
            headers["idPage"] = idPage
            setBody(pathToFile)
        }
    }
}