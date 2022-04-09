//package com.foggyskies.petapp
//
//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import io.ktor.client.*
//import io.ktor.client.engine.android.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.features.*
//import io.ktor.client.features.json.*
//import io.ktor.client.features.json.serializer.*
//import io.ktor.client.features.websocket.*
//import io.ktor.client.request.*
//import io.ktor.http.cio.websocket.*
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
//
//class NotificationViewModel: ViewModel() {
//
//    var Token = ""
//
//    var mainSocket: DefaultClientWebSocketSession? = null
//
//    fun observeNotifications(): Flow<Notification> {
//        return try {
//            mainSocket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val string = (it as? Frame.Text)?.readText() ?: ""
////                    var json  = Json.parseToJsonElement(string)
//
//                    val json = Json.decodeFromString<Notification>(string)
//                    json
//                } ?: flow {}
//        } catch (e: Exception) {
//            flow { }
//        }
//    }
//
//
//    init {
////        val token = applicationContext.getSharedPreferences(
////            "Token",
////            Context.MODE_PRIVATE
////        ).getString("Token", "").toString()
//        viewModelScope.launch {
//            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                expectSuccess = false
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 3000
//                }
//            }.use {
//                it.get<String>("http://${MainActivity.MAINENDPOINT}/createNotificationSession") {
//                    parameter("token", Token)
////                    this.headers["Auth"] = MainActivity.TOKEN
////                    parameter("idUser", "62333996647f7366746c563a")
//                }
//            }
//
//
//            val client = HttpClient(CIO) {
//                install(WebSockets)
//            }
//            mainSocket = client.webSocketSession {
//                expectSuccess = false
//                url("ws://${MainActivity.MAINENDPOINT}/notify/$Token")
////                header("Auth", token)
//            }
//
//            observeNotifications().onEach { notification ->
//                Log.e("OBSERVERMESSAGES", "ПРИШЛО $notification")
//                if (PushNotificationService.notificationsList.size > 0) {
//                    Log.e("SERVICE", "1")
//
//                    PushNotificationService.notificationsList.add(notification)
////                    showNotification(
////                        "Сообщения",
////                        "У вас имеется ${PushNotificationService.notificationsList.size} не прочитанных сообщений."
////                    )
//                } else {
//                    Log.e("SERVICE", "2")
//
////                showNotification(notification.title, notification.description)
//                    PushNotificationService.notificationsList.add(notification)
//                }
//            }.launchIn(this)
//        }
//    }
//}