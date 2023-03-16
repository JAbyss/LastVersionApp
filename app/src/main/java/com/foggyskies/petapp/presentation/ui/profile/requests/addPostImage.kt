package com.foggyskies.petapp.presentation.ui.profile.requests

import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.profile.*
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.Content.ADD_POST_IMAGE
import com.foggyskies.petapp.workers.BodyFile
import com.foggyskies.petapp.workers.TypeLoadFile
import com.foggyskies.petapp.workers.UploadFileViewModel
import com.foggyskies.petapp.workers.uploadFile
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

fun ProfileViewModel.newPost(
    path: String,
    idPage: String,
    description: String,
    uploadViewModel: UploadFileViewModel
) {
    backgroundScope.launch {
        //TODO надо пофиксить
        val bodyFile = BodyFile.generate(path, TypeLoadFile.CONTENT_PROFILE, idPage)

        val readyPath = uploadViewModel.uploadFile(
            File(path),
            bodyFile,
            isCompressed = true
        )

        readyPath.onSuccess {
            val content = ContentRequestDC(
                idPageProfile = idPage,
                item = NewContentDC(
                    type = "Image",
                    value = it,
                    description = description
                )
            )
            val response = httpRequest<ContentPreviewDC>(addPostImageRaw(content))
            response.onSuccess {
                withContext(Main) {
                    menuHelper.changeVisibilityMenu(MENUS.NEWCONTENT)
                    selectedPage.apply {
                        countContents = "${((countContents.toInt()) + 1)}"
                    }
                    listPostImages = listPostImages + it
                    descriptionMenuNewContent = ""
                }
            }
        }
//        readyPath?.let {
//            val content = ContentRequestDC(
//                idPageProfile = idPage,
//                item = NewContentDC(
//                    type = "Image",
//                    value = it,
//                    description = description
//                )
//            )
//            checkInternet(
//                request = {
//                    cRequest<ContentPreviewDC>(
//                        response = addPostImage(content),
//                        onOk = {
//                            withContext(Main) {
//                                menuHelper.changeVisibilityMenu(MENUS.NEWCONTENT)
//                                selectedPage.apply {
//                                    countContents = "${((countContents.toInt()) + 1)}"
//                                }
//                                listPostImages = listPostImages + it
//                                descriptionMenuNewContent = ""
//                            }
//                        }
//                    )
//                }
//            )
//        }
    }
}

// TODO Надо разобраться с созданием номого поста
suspend fun addPostImageRaw(body: ContentRequestDC): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.post(Routes.SERVER.REQUESTS.BASE_URL + ADD_POST_IMAGE) {
            contentType(ContentType.Application.Json)
            AuthHeader
            setBody(body)
        }
    }
}