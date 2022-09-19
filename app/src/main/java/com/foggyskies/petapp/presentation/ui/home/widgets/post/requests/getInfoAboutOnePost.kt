package com.foggyskies.petapp.presentation.ui.home.widgets.post.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.presentation.ui.home.widgets.post.SelectedPostWithIdPageProfile
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Content.ONE_POST_INFO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

//TODO ПОка не понятно, нужно или нет.
fun PostScreenHandler.infoOnePost(data: IdPageAndPost) {
    backgroundScope.launch {
        checkInternet(
            {
                cRequest<SelectedPostWithIdPageProfile>(
                    response = getInfoAboutOnePost(data),
                    onOk = {

                    },
                    onError = {

                    }
                )
            },
            networkError = {

            }
        )
    }
}

suspend fun getInfoAboutOnePost(data: IdPageAndPost): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + ONE_POST_INFO) {
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(data)
        }
    }
}