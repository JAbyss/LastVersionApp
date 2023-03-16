package com.foggyskies.petapp.presentation.ui.profile.requests

import androidx.navigation.NavHostController
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.cRequest
import com.foggyskies.petapp.checkInternet
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.httpRequest
import com.foggyskies.petapp.presentation.ui.authorization.client.AuthHeader
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.LOG_OUT
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun ProfileViewModel.logOut(nav_controller: NavHostController){
    backgroundScope.launch {
        val response = httpRequest<Unit>(logOutRaw())
        response.onSuccess {
            withContext(Main){
                nav_controller.navigate(NavTree.Authorization.name)
                MainPreference.clearPreference()
            }
        }
//        checkInternet(
//            request = {
//                cRequest<Unit>(
//                    response = logOutRequest(),
//                    onOk = {
//                        withContext(Main){
//                            nav_controller.navigate(NavTree.Authorization.name)
//                            MainPreference.clearPreference()
//                        }
//                    }
//                )
//            }
//        )
    }
}

private suspend fun logOutRaw(): Result<HttpResponse> = runCatching {
    clientJson.use {
        return@use it.get(BASE_URL + LOG_OUT){
           AuthHeader
        }
    }
}