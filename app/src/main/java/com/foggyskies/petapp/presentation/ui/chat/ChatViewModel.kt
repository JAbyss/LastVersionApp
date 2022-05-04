package com.foggyskies.petapp.presentation.ui.chat

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.GalleryHandler
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.domain.db.ChatDB
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.home.RepositoryChatDB
import com.foggyskies.petapp.presentation.ui.profile.human.encodeToBase64
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    var messages: List<ChatMessage> = emptyList(),
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

    var db: ChatDB? = null

    var chatEntity: FormattedChatDC? = null

    val galleryHandler = GalleryHandler()

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
    val repositoryChatDB: RepositoryChatDB by inject(RepositoryChatDB::class.java)


    fun connectToChat(idChat: String, context: Context) {
        db?.apply {
            createTable(idChat)
            _state.value = state.value.copy(
                messages = loadFiftyMessages(idChat).asReversed()
            )
        }

        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 30000
                }
            }.use {
                var a =
                    it.get<HttpResponse>("http://${MainActivity.MAINENDPOINT}/subscribes/createChatSession?idChat=$idChat")
//                _state.value = state.value.copy(
//                    messages = messages
//                )
            }
            val client = HttpClient(CIO) {
                install(WebSockets)
            }
            socket = client.webSocketSession {
                url("ws://${MainActivity.MAINENDPOINT}/subscribes/$idChat?username=$USERNAME")
            }
            observeMessages()
                .onEach { message ->
//                    ChatMessage
                    if (!_state.value.messages.contains(message)) {

                        val newList = state.value.messages.toMutableList().apply {
                            add(0, message)
                        }
                        viewModelScope.launch {

//                    val newImages = message.listImages.map {
//                            val image = ImageRequest.Builder(context)
//                                .data("http://${MainActivity.MAINENDPOINT}/${it}")
//                                .crossfade(true)
//                                .diskCachePolicy(CachePolicy.ENABLED)// it's the same even removing comments
//                                .build()
//                            val result = image.context.imageLoader.execute(image)
//                            val _imageDrawable = result.drawable
//                            // Converting it to bitmap and using it to calculate the palette
//                            val bitmap = _imageDrawable?.toBitmap()
//                            val string64 = encodeToBase64(bitmap!!)
//                            val formattedString = "{\"$it\": \"$string64\"}"
//                            formattedString
//                        }
                            repositoryChatDB.insertMessage(idChat, message)
                        }


                        _state.value = state.value.copy(
                            messages = newList
                        )
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun observeMessages(): Flow<ChatMessage> {

        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val chatMessage = Json.decodeFromString<ChatMessage>(json)
//                    db?.insertMessages(
//                        chatEntity?.id!!,
//                        message = chatMessage.copy(listImages = emptyList())
//                    )
                    chatMessage
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    fun sendMessage(message: MessageDC) {
        viewModelScope.launch {
            val json = Json.encodeToString(message)
            socket?.send(json)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            socket?.close()
            socket = null
            _state.value.clear()
        }
    }

    val listImageAddress = mutableListOf<String>()

    fun addImageToMessage(message: String, callBack: () -> Unit) {
        viewModelScope.launch {

            galleryHandler.selectedItems.forEach {

                val bm = BitmapFactory.decodeFile(it)
                val string64 = encodeToBase64(bm)

                HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                expectSuccess = false
                    install(HttpTimeout) {
                        requestTimeoutMillis = 300000
                    }
                }.use {

                    val response =
                        it.post<HttpResponse>("http://${MainActivity.MAINENDPOINT}/subscribes/addImageToMessage") {
                            headers["Auth"] = MainActivity.TOKEN
//                        headers["Content-Type"] = "text/plain"
                            parameter("idChat", chatEntity?.id)
                            body = string64
                        }
                    if (response.status.isSuccess()) {
                        listImageAddress.add(response.readText())
//                        imageProfile = humanPhoto
                    }
                }
            }
            sendMessage(
                MessageDC(
                    listImages = listImageAddress.toList(),
                    message = message
                )
            )
            listImageAddress.clear()
            galleryHandler.selectedItems = emptyList()
            callBack()
        }
    }
}

@Serializable
data class MessageDC(
    var listImages: List<String> = emptyList(),
    var message: String
)
