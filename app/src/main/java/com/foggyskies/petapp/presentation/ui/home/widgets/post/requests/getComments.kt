package com.foggyskies.petapp.presentation.ui.home.widgets.post.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.home.widgets.post.FormattedCommentDC
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Content.COMMENTS
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun PostScreenHandler.comments() {
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<FormattedCommentDC>(
                    response = getComments(idPageAndPost),
                    onOk = {
                        listComments = it
                    }
                )
            }
        )
    }
}

suspend fun getComments(data: IdPageAndPost): HttpResponse {
    clientJson.use {
        return it.get(Routes.SERVER.REQUESTS.BASE_URL + COMMENTS) {
            contentType(ContentType.Application.Json)
            header(BuildConfig.Authorization, MainPreference.Token)
            parameter("idPageProfile", data.idPageProfile)
            parameter("idPost", data.idPost)
        }
    }
}