package com.foggyskies.petapp.presentation.ui.profile.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.home.widgets.post.SelectedPostWithIdPageProfile
import com.foggyskies.petapp.presentation.ui.home.widgets.post.requests.IdPageAndPost
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Content.ONE_POST_INFO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun ProfileViewModel.infoAboutOnePost(idPost: String) {
    backgroundScope.launch {
        checkInternet(
            request = {
                val idPageAndPost = IdPageAndPost(
                    selectedPage.id,
                    idPost
                )
                cRequest<SelectedPostWithIdPageProfile>(
                    response = getInfoAboutOnePost(idPageAndPost),
                    onOk = {
                        postScreenHandler.selectPost(
                            it,
                            action = {
                                swipableMenu.isReadyMenu = false
                                isVisiblePostWindow = true
                            }
                        )
                    }
                )
            }
        )
    }
}

suspend fun getInfoAboutOnePost(idPageAndPost: IdPageAndPost): HttpResponse {
    clientJson.use {
        return it.post(BASE_URL + ONE_POST_INFO) {
            contentType(ContentType.Application.Json)
            header(BuildConfig.Authorization, MainPreference.Token)
            setBody(idPageAndPost)
        }
    }
}