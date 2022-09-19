package com.foggyskies.petapp

import android.util.Log
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.network.ConnectivityObserver
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1

suspend inline fun <T, reified O> request(
    data: T,
    request: KSuspendFunction1<T, HttpResponse>,
    onOk: (O) -> Unit,
    onError: (HttpResponse) -> Unit
) {
    val response = request(data)
    if (response.status.isSuccess())
        when (0::class) {
            String::class -> onOk(response.bodyAsText() as O)
            Unit::class -> onOk(Unit as O)
            else -> onOk(response.body())
        }
    else
        onError(response)
}

suspend inline fun <reified O> cRequest(
    response: HttpResponse,
    onOk: (O) -> Unit,
    onError: (HttpResponse) -> Unit = { Log.e("cRequest", "Ошибка: ${it.status}, $it")}
) {
    if (response.status.isSuccess()) {
        when (0::class) {
            String::class -> onOk(response.bodyAsText() as O)
            Unit::class -> onOk(Unit as O)
            HttpResponse::class -> onOk(response as O)
            else -> onOk(response.body())
        }
    }else
        onError(response)
}

inline fun checkInternet(request: () -> Unit, networkError: () -> Unit = { Log.e("NETWORK", "NETWORK ERROR") }){
    if (isNetworkAvailable.value == ConnectivityObserver.Status.Available)
        request()
    else
        networkError()
}

suspend inline fun <reified O> requestGet(
    response: HttpResponse,
    onOk: (O) -> Unit,
    onError: (HttpResponse) -> Unit
) {
    if (response.status.isSuccess())
        if (O::class == String::class) {
            onOk(response.bodyAsText() as O)
        } else {
            onOk(response.body())
        }
    else
        onError(response)
}