package com.foggyskies.petapp.presentation.ui.profile.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.StateProfile
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.DELETE_PAGE_PROFILE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

fun ProfileViewModel.deletePageProfile() {
    backgroundScope.launch {
        val response = httpRequest<Unit>(deletePageProfileRaw(selectedPage.id))
        response.onSuccess {
            stateProfile = StateProfile.HUMAN
            listPagesProfile.remove(selectedPage)
        }
//        checkInternet(
//            request = {
//                cRequest<Unit>(
//                    response = deletePageProfileRequest(selectedPage.id),
//                    onOk = {
//                        stateProfile = StateProfile.HUMAN
//                        listPagesProfile.remove(selectedPage)
//                    }
//                )
//            }
//        )
    }
}

private suspend fun deletePageProfileRaw(idPageProfile: String): Result<HttpResponse> =
    runCatching {
        clientJson.use {
            return@use it.delete(BASE_URL + DELETE_PAGE_PROFILE) {
                AuthHeader
                header("IdPageProfile", idPageProfile)
            }
        }
    }