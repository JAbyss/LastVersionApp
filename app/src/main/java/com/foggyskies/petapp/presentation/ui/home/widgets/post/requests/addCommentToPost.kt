package com.foggyskies.petapp.presentation.ui.home.widgets.post.requests

import androidx.compose.ui.text.input.TextFieldValue
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.chat.entity.CommentDC
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Content.ADD_COMMENT_TO_POST
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun PostScreenHandler.commentToPost() {
    backgroundScope.launch {
        val comment = CommentDC(
            id = "",
            idUser = MainPreference.IdUser,
            message = commentValue.value.text,
            date = ""
        )
        checkInternet(
            request = {
                cRequest<Unit>(
                    response = addCommentToPost(comment, idPageAndPost),
                    onOk = {
                        commentValue.value = TextFieldValue("")
                        listComments.comments = listComments.comments + comment
                    }
                )
            }
        )
    }
}

suspend fun addCommentToPost(
    data: CommentDC,
    idPageAndPost: IdPageAndPost
): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + ADD_COMMENT_TO_POST) {
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
            parameter("idPageProfile", idPageAndPost.idPageProfile)
            parameter("idPost", idPageAndPost.idPost)
            setBody(data)
        }
    }
}