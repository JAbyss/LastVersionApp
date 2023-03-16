package com.foggyskies.petapp.presentation.ui.profile.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.profile.ContentPreviewDC
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Content.CONTENT_PAGE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ProfileViewModel.contentPreview(idPageProfile: String){
    backgroundScope.launch {
        val response = httpRequest<List<ContentPreviewDC>>(getContentPreviewRaw(idPageProfile))
        response.onSuccess {
            withContext(Main){
                listPostImages = it
            }
        }
//        checkInternet(
//            request = {
//                cRequest<List<ContentPreviewDC>>(
//                    response = getContentPreview(idPageProfile),
//                    onOk = {
//                        withContext(Main){
//                            listPostImages = it
//                        }
//                    }
//                )
//            }
//        )
    }
}

private suspend fun getContentPreviewRaw(data: String): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.get(Routes.SERVER.REQUESTS.BASE_URL + CONTENT_PAGE){
            contentType(ContentType.Application.Json)
            AuthHeader
            parameter("idPageProfile", data)
        }
    }
}