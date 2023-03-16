package com.foggyskies.petapp.presentation.ui.home.widgets.post.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

@kotlinx.serialization.Serializable
data class IdPageAndPost(
    val idPageProfile: String,
    val idPost: String
)

fun PostScreenHandler.likePost(){
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<Boolean>(
                    response = addLikeToPost(idPageAndPost),
                    onOk = {
                        isLiked = it
                        selectedPost?.isLiked = isLiked
                    }
                )
            }
        )
    }
}
suspend fun addLikeToPost(data: IdPageAndPost): HttpResponse {
    clientJson.use {
        return it.post("${Routes.SERVER.REQUESTS.BASE_URL}/content/addLikeToPost"){
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(data)
        }
    }
}