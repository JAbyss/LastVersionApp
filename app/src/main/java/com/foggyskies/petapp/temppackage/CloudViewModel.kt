package com.foggyskies.petapp.temppackage

import android.util.Base64
import androidx.lifecycle.ViewModel
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class CloudViewModel: ViewModel() {

    var cloudSocket: WebSocketSession? = null

    fun connectCloudSocket(){
        CoroutineScope(Dispatchers.IO).launch {

            if (cloudSocket == null){
                val client = HttpClient(CIO) {
                    install(WebSockets)
                }
                cloudSocket = client.webSocketSession {
                    url("${Routes.SERVER.WEBSOCKETCOMMANDS.BASE_URL}/cloud/downloadCloud")
//                    header("Auth", MainActivity.TOKEN)
                }
                observeMessages().onEach { file_ ->

                    val file = File("${Routes.FILE.ANDROID_DIR}/Download/${file_.nameFile}")
                    file.createNewFile()
                    val bytes = Base64.decode(file_.data, Base64.DEFAULT)
                    file.appendBytes(bytes)
                }.launchIn(this)
            }
        }

    }

    fun sendAction(path: String){
        CoroutineScope(Dispatchers.IO).launch {
            cloudSocket?.send("path")
        }
    }

    suspend fun observeMessages(): Flow<CloudFileDownload> {

        return try {
            cloudSocket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val data = it.toString().split("|")
                    val formData = CloudFileDownload(
                        nameFile = data[0],
                        data = data[1]
                    )
                    formData
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

}

data class CloudFileDownload(
    val nameFile: String,
    val data: String
)