package com.foggyskies.petapp.presentation.ui.chat

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.profile.human.encodeToBase64
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.temppackage.GalleryHandler
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

enum class StateTextField {
    EMPTY, WRITING
}

enum class StateKeyBoard {
    HIDDEN, VISIBLE
}

data class ChatState(
    var messages: List<ChatMessageDC> = emptyList(),
    var isLoading: Boolean = false
) {
    fun clear() {
        messages = emptyList()
        isLoading = false
    }
}

class ChatViewModel : ViewModel() {

    override fun onCleared() {
        viewModelScope.launch {
            super.onCleared()
            socket?.close()
        }
    }

    var chatEntity: FormattedChatDC? = null

    var galleryHandler: GalleryHandler? = null

    init {
        galleryHandler = GalleryHandler()
    }

    var heightHeaderAppBar by mutableStateOf(0)

    var heightBottomAppBar by mutableStateOf(0)

    var height_keyboard by mutableStateOf(0)

    var stateTextField by mutableStateOf(StateTextField.EMPTY)

    var height_config by mutableStateOf(0)
    var density by mutableStateOf(0f)

    var heightChat by mutableStateOf(height_config.dp - ((heightHeaderAppBar + heightBottomAppBar) / density).dp)

    fun changeStateChatToVisible() {
        heightChat =
            height_config.dp - ((heightHeaderAppBar + heightBottomAppBar + height_keyboard) / density).dp
    }

    fun changeStateChatToHidden() {
        heightChat = height_config.dp - ((heightHeaderAppBar + heightBottomAppBar) / density).dp
    }

    var socket: WebSocketSession? = null

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    var visibleButtonDown by mutableStateOf(false)

    var countUnreadMessage by mutableStateOf(0)
    val repositoryUserDB: RepositoryUserDB by inject(RepositoryUserDB::class.java)


    fun connectToChat(idChat: String, context: Context) {

        CoroutineScope(Dispatchers.IO).launch {
            repositoryUserDB.dbUser.apply {
                createTableMessages(idChat)
                _state.value = state.value.copy(
                    messages = loadFiftyMessages(idChat).asReversed()
                )
                Log.e("TEST", _state.value.toString())
            }
            if (isNetworkAvailable.value) {
                HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                    install(HttpTimeout) {
                        requestTimeoutMillis = 30000
                    }
                }.use {
                    it.get<HttpResponse>("${Routes.SERVER.REQUESTS.BASE_URL}/subscribes/createChatSession?idChat=$idChat")
                }
                val client = HttpClient(CIO) {
                    install(WebSockets)
                }
                socket = client.webSocketSession {
                    header("Auth", TOKEN)
                    url("${Routes.SERVER.WEBSOCKETCOMMANDS.BASE_URL}/subscribes/$idChat?username=$USERNAME")
                }
                observeMessages()
                    .onEach { message ->
                        if (!_state.value.messages.map{ it.id }.contains(message.id)) {

                            val newList = state.value.messages.toMutableList().apply {
                                add(0, message)
                            }
                            repositoryUserDB.insertMessage(idChat, message)

                            _state.value = state.value.copy(
                                messages = newList
                            )
                        }
                    }.launchIn(this)
            }
        }
    }

    fun observeMessages(): Flow<ChatMessageDC> {

        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val chatMessageDC = Json.decodeFromString<ChatMessageDC>(json)
                    chatMessageDC
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    fun sendMessage(message: MessageDC) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Json.encodeToString(message)
            socket?.send(json)
        }
    }

    fun disconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            socket?.close()
            socket = null
            _state.value.clear()
        }
    }

    val listImageAddress = mutableListOf<String>()

    fun addImageToMessage(message: String, callBack: () -> Unit = {}) {
        CoroutineScope(Dispatchers.Default).launch {

            val asyncList = mutableListOf<Deferred<Unit>>()

            galleryHandler!!.selectedItems.forEach {
                asyncList.add(async {
                    val bm = BitmapFactory.decodeFile(it)
                    val string64 = encodeToBase64(bm)

                    HttpClient(Android) {
//                expectSuccess = false
                        install(HttpTimeout) {
                            requestTimeoutMillis = 30000
                        }
                    }.use {

                        val response =
                            it.post<HttpResponse>("${Routes.SERVER.REQUESTS.BASE_URL}/subscribes/addImageToMessage") {
                                headers["Auth"] = MainActivity.TOKEN
                                parameter("idChat", chatEntity?.id)
                                body = string64
                            }
                        if (response.status.isSuccess()) {
                            listImageAddress.add(response.readText())
                        }
                    }
                })
            }
            asyncList.awaitAll()
            sendMessage(
                MessageDC(
                    listImages = listImageAddress.toList(),
                    message = message
                )
            )
            listImageAddress.clear()
            galleryHandler!!.selectedItems = emptyList()
//            callBack()
        }
    }

    var selectedImage by mutableStateOf<SelectedImageMessage?>(null)

}

data class SelectedImageMessage(
    var message: ChatMessageDC,
    var imageRequest: String
)

@Serializable
data class MessageDC(
    var listImages: List<String> = emptyList(),
    var message: String
)
