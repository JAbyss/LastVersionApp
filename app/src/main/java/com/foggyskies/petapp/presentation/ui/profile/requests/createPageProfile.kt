package com.foggyskies.petapp.presentation.ui.profile.requests

import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.profile.PageProfileDC
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.CREATE_PAGE_PROFILE
import com.foggyskies.petapp.workers.BodyFile
import com.foggyskies.petapp.workers.TypeLoadFile
import com.foggyskies.petapp.workers.uploadFile
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

fun ProfileViewModel.newPageProfile(pageProfile: PageProfileDC, path: String) {
    backgroundScope.launch {
        checkInternet(
            request = {
                cRequest<String>(
                    response = createPageProfile(pageProfile),
                    onOk = { idPage ->
                        val bodyFile = BodyFile.generate(path, TypeLoadFile.PROFILE, idPage)
                        val readyPath = uploadFile(
                            File(path),
                            bodyFile,
                            isCompressed = true
                        )
                        readyPath?.let {
                            changeAvatarProfile(readyPath, idPage)
                            withContext(Main){
                                isAddingNewCard = false
                            }
                        }
                    }
                )
            }
        )
    }
}

suspend fun createPageProfile(data: PageProfileDC): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + CREATE_PAGE_PROFILE) {
            contentType(ContentType.Application.Json)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(data)
        }
    }
}