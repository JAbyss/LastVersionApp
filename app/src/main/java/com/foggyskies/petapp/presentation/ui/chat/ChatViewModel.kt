package com.foggyskies.petapp.presentation.ui.chat

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMainEntity
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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
    fun clear(){
        messages = emptyList()
        isLoading = false
    }
}

class ChatViewModel : ViewModel() {

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

    fun connectToChat(idChat: String) {
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout){
                    requestTimeoutMillis = 3000
                }
            }.use {
                var a = it.get<HttpResponse>("http://${MainActivity.MAINENDPOINT}/subscribes/createChatSession?idChat=$idChat")
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
                    val newList = state.value.messages.toMutableList().apply {
                        add(0, message)
                    }
                    _state.value = state.value.copy(
                        messages = newList
                    )
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
                    chatMessage
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            socket?.send(message)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            socket?.close()
            socket = null
            _state.value.clear()
        }
    }
}