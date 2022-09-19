package com.foggyskies.petapp.presentation.ui.profile.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
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
        checkInternet(
            request = {
                cRequest<String>(
                    response = changeAvatarProfileRequest(idPage, pathToFile),
                    onOk = {
                        withContext(Main) {
                            initImagePageProfile = it

                            listPagesProfile.forEach { page ->
                                if (page.id == selectedPage.id) {
                                    page.image = initImagePageProfile
                                    return@forEach
                                }
                            }
                        }
                        //todo Надо переделать
                    }
                )
            }
        )
    }
}

suspend fun changeAvatarProfileRequest(idPage: String, pathToFile: String): HttpResponse {
    clientJson.use {
        return it.post(BASE_URL + CHANGE_AVATAR_PROFILE) {
            headers[BuildConfig.Authorization] = MainPreference.Token
            headers["idPage"] = idPage
            setBody(pathToFile)
        }
    }
}