package com.foggyskies.petapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import com.foggyskies.petapp.network.TAG
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory


@kotlinx.serialization.Serializable
data class NotificationDocument(
    var id: String,
    var notifications: List<Notification>
)

@kotlinx.serialization.Serializable
data class Notification(
    var id: String,
    var idUser: String,
    var title: String,
    var description: String,
    var image: String,
    var status: String
){
    fun toNWV(): NotificationWithVisilble {
        return NotificationWithVisilble(
            id = id,
             idUser =  idUser,
            title = title,
            description = description,
            image = image,
            status = status,
            isVisible = mutableStateOf(true)
        )
    }
}

@kotlinx.serialization.Serializable
data class NotificationWithVisilble(
    var id: String,
    var idUser: String,
    var title: String,
    var description: String,
    var image: String,
    var status: String,
    var isVisible: MutableState<Boolean> = mutableStateOf(true)
){
    fun toFormattedChat(): FormattedChatDC {
        return FormattedChatDC(
            id = id,
            idCompanion = idUser,
            image = image,
            nameChat = title
        )
    }
}

class PushNotificationService() : LifecycleService() {


    var mainSocket: DefaultClientWebSocketSession? = null

    var count = 0

    companion object {
        var notificationsList = mutableListOf<Notification>()
        var isServiceStarted = true
        var LIFESERVICE = false
        var ISAPPLIFE = false
    }

    var Author = ""
    var SinglUserNotification = true

    var Token = ""

    init {
        var isInternet = mutableStateOf(false)


        LIFESERVICE = true
        val a = SocketFactory.getDefault()
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                suspend fun execute(socketFactory: SocketFactory): Boolean {
                    return try{
                        Log.e(TAG, "PINGING google.")
                        val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
                        socket.connect(InetSocketAddress("8.8.8.8", 53), 5000)
                        socket.close()
                        Log.e(TAG, "PING success.")
                        if (mainSocket != null){

                        } else {
                            notifySystem()
                        }
                        isInternet.value = true
                        true
                    }catch (e: IOException){
                        Log.e(TAG, "No internet connection. ${e}")
                        mainSocket?.close()
                        mainSocket = null
                        isInternet.value = false
                        false
                    }
                }
                execute(a)
                delay(1000)
            }
        }
    }

    suspend fun notifySystem(){
        try {
            delay(3000)

            Token = applicationContext.getSharedPreferences(
                "Token",
                Context.MODE_PRIVATE
            ).getString("Token", "").toString()

            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                install(ContentNegotiation){
//                    json(Json {
//                        prettyPrint = true
//                        isLenient = true
//                    })
//                }
                expectSuccess = false
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                it.get("${Routes.SERVER.REQUESTS.BASE_URL}/createNotificationSession") {
                    parameter("token", Token)
                }
            }

            val client = HttpClient(CIO) {
                install(WebSockets)
            }
            mainSocket = client.webSocketSession {
                expectSuccess = false
                url("${Routes.SERVER.WEBSOCKETCOMMANDS.BASE_URL}/notify/$Token")
            }

            observeNotifications().onEach { notification ->
                Log.e("OBSERVERMESSAGES", "ПРИШЛО $notification")

                if (notificationsList.size == 0) {
                    Author = notification.title
                    SinglUserNotification = true
                    notificationsList.add(notification)
                } else if (notification.title != Author) {
                    SinglUserNotification = false
                    notificationsList.add(notification)
                } else {
                    notificationsList.add(notification)
                }

                if (SinglUserNotification && notificationsList.size == 1) {
                    showNotification(
                        notificationsList[0].title,
                        notificationsList[0].description,
                        notification
                    )
                } else if (SinglUserNotification) {
                    showNotification(
                        notificationsList[0].title,
                        "У вас имеется ${notificationsList.size} не прочитанных сообщений.",
                        notification
                    )
                } else if (!SinglUserNotification) {
                    showNotification(
                        "Сообщения",
                        "У вас имеется ${notificationsList.size} не прочитанных сообщений.",
                        notification
                    )
                }
            }.launchIn(CoroutineScope(Dispatchers.IO))
        } catch (e: java.lang.Exception) {

        }
    }

    fun observeNotifications(): Flow<Notification> {
        return try {
            mainSocket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val string = (it as? Frame.Text)?.readText() ?: ""
                    val json = Json.decodeFromString<Notification>(string)
                    json
                } ?: flow {}
        } catch (e: Exception) {
            flow { }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(task: String, desc: String, notification: Notification) {
        Log.e("SERVICE", "3")

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "2"
        val channelName = "chat_notification"

        val item =
            if (SinglUserNotification)
                Json.encodeToString(
                    FormattedChatDC(
                        id = notification.id,
                        idCompanion = notification.idUser,
                        nameChat = notification.title,
                        image = notification.image,
                        lastMessage = ""
                    )
                )
            else
                ""

        val pendingIntent = if (item.isNotEmpty()) {
            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                "https://www.example.com/itemChat=${item}token=$Token".toUri(),
                applicationContext,
                MainActivity::class.java
            )
            TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        } else {
            val notificationIntent = Intent(applicationContext, MainActivity::class.java)

            PendingIntent.getActivity(
                applicationContext,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }


        var builder: NotificationCompat.Builder? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("SERVICE", "4")

            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)

            builder = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_add)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        } else {
            Log.e("SERVICE", "5")

            builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(task)
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }
        Log.e("SERVICE", "6")

        manager.notify(2, builder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.e("SERVISEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE", "Я Запущен")
        return START_STICKY
    }
}