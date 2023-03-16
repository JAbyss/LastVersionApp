package com.foggyskies.petapp

import android.util.Log
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.network.ConnectivityObserver
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.Error
import kotlin.reflect.KSuspendFunction1

//suspend inline fun <T, reified O> request(
//    data: T,
//    request: KSuspendFunction1<T, HttpResponse>,
//    onOk: (O) -> Unit,
//    onError: (HttpResponse) -> Unit
//) {
//    val response = request(data)
//    if (response.status.isSuccess())
//        when (0::class) {
//            String::class -> onOk(response.bodyAsText() as O)
//            Unit::class -> onOk(Unit as O)
//            else -> onOk(response.body())
//        }
//    else
//        onError(response)
//}

suspend inline fun <reified O> cRequest(
    response: HttpResponse,
    onOk: (O) -> Unit,
    onError: (HttpResponse) -> Unit = { Log.e("cRequest", "Ошибка: ${it.status}, $it") }
) {
    if (response.status.isSuccess()) {
        when (0::class) {
            String::class -> onOk(response.bodyAsText() as O)
            Unit::class -> onOk(Unit as O)
            HttpResponse::class -> onOk(response as O)
            else -> onOk(response.body())
        }
    } else
        onError(response)
}

//suspend fun a() {
//    request<String>()
//}

//suspend fun aaa(): String {
//    checkInternet(request = {
//        request<String>()
//    }).onSuccess {
//        it.onSuccess {
//            return ""
//        }
//        it.onFailure {
//            return ""
//        }
//    }.onFailure {
//        return ""
//    }
//}

suspend inline fun <reified T> httpRequest(httpResponse: Result<HttpResponse>): Result<T> =
    runCatching {

        return if (httpResponse.isSuccess) {
            val value = httpResponse.getOrNull() ?: return Result.failure(Error("Unknown"))
            if (value.status.isSuccess())
                when (T::class) {
                    String::class -> Result.success<T>(value.bodyAsText() as T)
                    Unit::class -> Result.success<T>(Unit as T)
                    else -> Result.success(value.body<T>())
                }
            else
                Result.failure(Exception(value.status.description))
        } else
            Result.failure(httpResponse.exceptionOrNull() ?: Error("Unknown"))
    }

inline fun <T> checkInternet(request: () -> T): Result<T> {
    return if (isNetworkAvailable.value == ConnectivityObserver.Status.Available)
        Result.success(request())
    else
        Result.failure(Exception())
}

//suspend inline fun <reified T> request(): Result<T> {
//    val response = _registrationUpload(
//        RegistrationUpload(null, "", "", "", null)
//    )
//
//    return if (response.status.isSuccess())
//        Result.success(response.body<T>())
//    else
//        Error(response.status.description)
//}

inline fun <T> checkInternet(request: () -> T, networkError: () -> T): T {
    return if (isNetworkAvailable.value == ConnectivityObserver.Status.Available)
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