package com.foggyskies.petapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@kotlinx.serialization.Serializable
data class NotificationDocument(
    var id: String,
    var notifications: List<Notification>
)

@kotlinx.serialization.Serializable
data class Notification(
    var id: String,
    var title: String,
    var description: String,
    var image: String,
    var status: String
)

class PushNotificationService(): Service() {

    var mainSocket: DefaultClientWebSocketSession? = null

    var count = 0

    var notificationsList = mutableListOf<Notification>()

    init{
        Log.e("SERVICE", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        GlobalScope.launch {
            while (true){
                Log.e("ADD", "Hello world${++count}")
                delay(500)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(10000)

                val token = applicationContext.getSharedPreferences(
                    "Token",
                    Context.MODE_PRIVATE
                ).getString("Token", "").toString()

                HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                    expectSuccess = false
                    install(HttpTimeout) {
                        requestTimeoutMillis = 3000
                    }
                }.use {
                    it.get<String>("http://${MainActivity.MAINENDPOINT}/createNotificationSession") {
                        parameter("token", token)
//                    this.headers["Auth"] = MainActivity.TOKEN
//                    parameter("idUser", "62333996647f7366746c563a")
                    }
                }


                val client = HttpClient(CIO) {
                    install(WebSockets)
                }
                mainSocket = client.webSocketSession {
                    expectSuccess = false
                    url("ws://${MainActivity.MAINENDPOINT}/notify/$token")
//                header("Auth", token)
                }

                observeNotifications().onEach { notification ->
                    Log.e("OBSERVERMESSAGES", "ПРИШЛО $notification")
                    if (notificationsList.size > 0) {
                        Log.e("SERVICE", "1")

                        notificationsList.add(notification)
                        showNotification(
                            "Сообщения",
                            "У вас имеется ${notificationsList.size} не прочитанных сообщений."
                        )
                    } else {
                        Log.e("SERVICE", "2")

                        showNotification(notification.title, notification.description)
                        notificationsList.add(notification)
                    }
                }.launchIn(this)
//            var a = mutableStateOf(mainSocket?.incoming?.receive())
//            Log.e("HHHHH", (a .toString()))
            }catch (e: java.lang.Exception){

            }
        }
    }

    fun observeNotifications(): Flow<Notification> {
       return try {
           mainSocket?.incoming
               ?.receiveAsFlow()
               ?.filter { it is Frame.Text }
               ?.map {
                   val string = (it as? Frame.Text)?.readText() ?: ""
//                    var json  = Json.parseToJsonElement(string)

                    val json = Json.decodeFromString<Notification>(string)
                   json
               } ?: flow {}
       }catch (e: Exception){
           flow {  }
       }
    }

    private fun showNotification(task: String, desc: String) {
        Log.e("SERVICE", "3")

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "2"
        val channelName = "chat_notification"

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0, notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        var builder: NotificationCompat.Builder? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("SERVICE", "4")

            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//            channel.setSound(null, null)
            manager.createNotificationChannel(channel)

            builder = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_add)
                .setContentIntent(contentIntent)
        } else{
            Log.e("SERVICE", "5")

            builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(task)
                .setContentText(desc)
                .setContentIntent(contentIntent)
//                .setSound(null)
        }
        Log.e("SERVICE", "6")

        manager.notify(2, builder.build())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        GlobalScope.launch {
//            while (true){
//                Log.e("ADD", "Hello world${++count}")
//                delay(500)
//            }
//        }
        return START_STICKY
    }

//    }
}