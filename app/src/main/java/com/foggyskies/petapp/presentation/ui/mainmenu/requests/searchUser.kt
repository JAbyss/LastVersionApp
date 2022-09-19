package com.foggyskies.petapp.presentation.ui.mainmenu.requests

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.client.clientJson
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.request
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.UserRoute.SEARCH_USER
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

fun <T> SnapshotStateList<T>.autoAddAndRemove(newData: List<T>){
    val needAddItems = newData - this.toSet()
    val deletedItems = this - newData.toSet()
    addAll(needAddItems)
    removeAll(deletedItems)
}

fun MenuViewModel.searchUser(username: String) {
    backgroundScope.launch {
        request<String, List<UsersSearch>>(
            data = username,
            request = ::searchUserRequest,
            onOk = {
                listFoundedUsers.autoAddAndRemove(it)
            },
            onError = {

            }
        )
    }
}

suspend fun searchUserRequest(username: String): HttpResponse {
    clientJson.use {
        return it.post(Routes.SERVER.REQUESTS.BASE_URL + SEARCH_USER) {
            contentType(ContentType.Text.Plain)
            headers[BuildConfig.Authorization] = MainPreference.Token
            setBody(username)
        }
    }
}