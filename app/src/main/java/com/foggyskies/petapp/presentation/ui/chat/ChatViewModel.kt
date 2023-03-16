package com.foggyskies.petapp.presentation.ui.chat

import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils.indexOf
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.BuildConfig
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.network.ConnectivityObserver
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.presentation.ui.chat.entity.FileDC
import com.foggyskies.petapp.presentation.ui.chat.requests.*
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.profile.encodeToBase64
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.temppackage.GalleryHandler
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

enum class StateTextField {
    EMPTY, WRITING, EDIT
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

    val backgroundScope = CoroutineScope(IO)

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

    //    val chatState = MutableStateFlow(mutableListOf<ChatMessageDC>())
//    private val _state = mutableStateOf(ChatState())
//    val state: State<ChatState> = _state
    val _state = mutableStateListOf<ChatMessageDC>()
//    val state: State<ChatState> = _state

    var visibleButtonDown by mutableStateOf(false)

    var countUnreadMessage by mutableStateOf(0)
    val repositoryUserDB: RepositoryUserDB by inject(RepositoryUserDB::class.java)

    private fun isElementExist(message: ChatMessageDC): Boolean {
        for (element in _state)
            if (element.id == message.id)
                return true
        return false
    }

    fun loadNextMessages(idLastMessage: String, isActive: MutableState<Boolean>) {
//        CoroutineScope(Dispatchers.IO).launch {
//            if (isNetworkAvailable.value == ConnectivityObserver.Status.Available)
//                socket?.send(Frame.Text("nextMessages|${state.value.messages.last().id}"))
//            else
//                repositoryUserDB.dbUser.loadNextMessages(chatEntity?.id!!, idLastMessage) {
//                    if (!_state.contains(it))
//                        _state.add(it)
//                }
//            Log.e("LOADING Messages", "FINISH")
//            delay(2000)
//            isActive.value = false
//        }
    }

    fun clearMessages() {
        _state.apply { removeAll { it.id < _state[118].id } }
    }

    var isServerResponded by mutableStateOf(false)

    fun connectToChat(idChat: String, context: Context) {
        backgroundScope.launch {

            repositoryUserDB.dbUser.apply {
                createTableMessages(idChat)
                if (isNetworkAvailable.value != ConnectivityObserver.Status.Available)
                    async {
                        val list = loadFiftyMessages(idChat, chatEntity?.nameChat!!)
                        if (!_state.containsAll(list))
                            _state.addAll(list)
//                        Log.e("TEST", _state.value.toString())
                    }
            }
            hundredMessages(idChat)
            newMessages(idChat)
//            newMessages(idChat)
//            createChatSession(idChat)
            if (isNetworkAvailable.value == ConnectivityObserver.Status.Available) {
//                HttpClient(Android) {
////                    install(JsonFeature) {
////                        serializer = KotlinxSerializer()
////                    }
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 30000
//                    }
//                }.use {
//                    it.get("${Routes.SERVER.REQUESTS.BASE_URL}/subscribes/createChatSession?idChat=$idChat")
//                }
                val client = HttpClient(CIO) {
                    install(WebSockets)
                }
                socket =
                    client.webSocketSession() {
                        url("${Routes.SERVER.WEBSOCKETCOMMANDS.BASE_URL}/subscribes/chatSessions")
                        header(BuildConfig.Authorization, MainPreference.Token)
                        header("idChat", idChat)
                    }
                observeMessages()
                    .onEach { message ->
                        if (message != null)
                            if (!isElementExist(message)) {
//                                chatState.e
//                                val newList =
//                                    state.value.messages.toMutableList().apply {
//                                        add(0, message)
//                                    }
                                _state.add(0, message)
                                repositoryUserDB.insertMessage(idChat, message)
//
//                                _state.value = state.value.copy(
//                                    messages = newList
//                                )
                            }
                    }.launchIn(this@launch)
            }
        }
    }

    fun observeMessages(): Flow<ChatMessageDC?> {

        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    it as Frame.Text
                    isServerResponded = true
                    val json = it.readText()

                    val chatMessageDC = Json.decodeFromString<ChatMessageDC>(json)
                    chatMessageDC
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }
//    observeMessages(
//    callBack = { flowMessage ->
//        flowMessage.onEach { message ->
//
//            if (!_state.value.messages.containsAll(message)) {
////
//                val newList =
//                    state.value.messages.toMutableList().apply {
//                        addAll(message)
//                    }
//                //FIXME НАДО РАЗОБРАТЬСЯ
////
//                _state.value = state.value.copy(
//                    messages = newList
//                )
//                async {
//                    message.forEach {
//
//                        repositoryUserDB.insertMessage(idChat, it)
//                    }
//                }
//
//            }
//        }.launchIn(this@launch)
//    }
//    )
//    .onEach { message ->
//        if (message != null)
//            if (!isElementExist(message)) {
//
//                val newList =
//                    state.value.messages.toMutableList().apply {
//                        add(0, message)
////                                    while (this.size > 30) {
////                                        removeLast()
////                                    }
//                    }
//                repositoryUserDB.insertMessage(idChat, message)
//
//                _state.value = state.value.copy(
//                    messages = newList
//                )
//            }
//    }.launchIn(this@launch)
//}
//    fun observeMessages(callBack: (Flow<List<ChatMessageDC>>) -> Unit): Flow<ChatMessageDC?> {
//
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    it as Frame.Text
//                    isServerResponded = true
//                    val json = it.readText()
//                    if ("^nextMessages\\|".toRegex().find(json) != null) {
//                        val chatMessageDC = Json.decodeFromString<List<ChatMessageDC>>(
//                            json.replace(
//                                "nextMessages|",
//                                ""
//                            )
//                        )
//                        callBack(flowOf(chatMessageDC))
//                        null
//                    } else {
//                        val chatMessageDC = Json.decodeFromString<ChatMessageDC>(json)
//                        chatMessageDC
//                    }
//                } ?: flow {}
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow { }
//        }
//    }

    fun sendMessage(message: String) {
        backgroundScope.launch {
//            val json = Json.encodeToString(message)
            Log.e("SOCKET IS", socket.toString())
            socket?.send(message)
        }
    }

    fun disconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            socket?.close()
            socket = null
            _state.clear()
        }
    }

    fun addImageToMessage(message: String, callBack: () -> Unit = {}) {
        CoroutineScope(Dispatchers.Default).launch {

            val taskImages = galleryHandler!!.selectedItems.windowed(10, 10, true)
            taskImages.forEach {
                val asyncList = mutableListOf<Deferred<Unit>>()
                val listImageAddress = mutableListOf<String>()

                it.forEach {
                    asyncList.add(async {
                        val bm = BitmapFactory.decodeFile(it)
                        val string64 = encodeToBase64(bm)

                        HttpClient(Android) {
//                expectSuccess = false
//                            install(JsonFeature) {
//                                serializer = KotlinxSerializer()
//                            }
//                            install(ContentNegotiation) {
//                                json(Json {
//                                    prettyPrint = true
//                                    isLenient = true
//                                })
//                            }
                            install(HttpTimeout) {
                                requestTimeoutMillis = 30000
                            }
                        }.use {

                            val response: HttpResponse =
                                it.post("${Routes.SERVER.REQUESTS.BASE_URL}/subscribes/addImageToMessage") {
                                    headers["Auth"] = MainActivity.TOKEN
                                    parameter("idChat", chatEntity?.id)
                                    setBody(string64)
                                }
                            if (response.status.isSuccess()) {
                                listImageAddress.add(response.bodyAsText())
                            }
                        }
                    })
                }
                asyncList.awaitAll()
//                sendMessage(
//                    MessageDC(
//                        listImages = listImageAddress.toList(),
//                        message = message
//                    )
//                )
            }

            galleryHandler!!.selectedItems = emptyList()
//            callBack()
        }
    }

    var selectedImage by mutableStateOf<SelectedImageMessage?>(null)
    var messageSelected by mutableStateOf<ChatMessageDC?>(null)

//    fun deleteMessage() {
//        CoroutineScope(Dispatchers.IO).launch {
//
//            HttpClient(Android) {
////                expectSuccess = false
////                install(JsonFeature) {
////                    serializer = KotlinxSerializer()
////                }
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 30000
//                }
//            }.use {
//
//                val response: HttpResponse =
//                    it.post("${Routes.SERVER.REQUESTS.BASE_URL}/subscribes/deleteMessage") {
//                        headers["Auth"] = MainActivity.TOKEN
//                        headers["Content-Type"] = "Application/Json"
////                        parameter("idChat", chatEntity?.id)
//                        setBody(
//                            DeleteMessageEntity(
//                                idUser = if (messageSelected?.idUser!! == IDUSER)
//                                    chatEntity?.idCompanion!!
//                                else
//                                    IDUSER,
//                                idChat = chatEntity?.id!!,
//                                idMessage = messageSelected?.id!!
//                            )
//                        )
//                    }
//                if (response.status.isSuccess()) {
//                    repositoryUserDB.deleteMessage(chatEntity?.id!!, messageSelected?.id!!)
////                    val newList = state.value.messages.toMutableList().apply {
////                        remove(messageSelected)
////                    }
//                    _state.remove(messageSelected)
//                    messageSelected = null
////                    listImageAddress.add(response.readText())
//                }
//            }
//        }
//    }

    var lastBottomBatValue = ""

    var bottomBarValue by mutableStateOf("")
//    var isEdit by mutableStateOf(false)
//    var editValue = ""

    fun editMessageMode() {
        lastBottomBatValue = bottomBarValue
        bottomBarValue = messageSelected?.message!!
        stateTextField = StateTextField.EDIT
    }

//    fun editMessage() {
//        CoroutineScope(Dispatchers.IO).launch {
//
//            HttpClient(Android) {
////                expectSuccess = false
////                install(JsonFeature) {
////                    serializer = KotlinxSerializer()
////                }
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 30000
//                }
//            }.use {
//
//                val response: HttpResponse =
//                    it.post("${Routes.SERVER.REQUESTS.BASE_URL}/subscribes/editMessage") {
//                        headers["Auth"] = MainActivity.TOKEN
//                        headers["Content-Type"] = "Application/Json"
////                        parameter("idChat", chatEntity?.id)
//                        setBody(
//                            EditMessageEntity(
//                                idUser = if (messageSelected?.idUser!! == IDUSER)
//                                    chatEntity?.idCompanion!!
//                                else
//                                    IDUSER,
//                                idChat = chatEntity?.id!!,
//                                idMessage = messageSelected?.id!!,
//                                newMessage = bottomBarValue
//                            )
//                        )
//                    }
//                if (response.status.isSuccess()) {
//                    repositoryUserDB.editMessage(
//                        chatEntity?.id!!,
//                        messageSelected?.id!!,
//                        bottomBarValue
//                    )
////                    val newList = state.value.messages.toMutableList().apply {
//////                        remove(messageSelected)
////                        get(indexOf(messageSelected)).message = bottomBarValue
////                    }
//                    _state[_state.indexOf(messageSelected)].message = bottomBarValue
//
//                    messageSelected = null
//                    bottomBarValue = lastBottomBatValue
//                    stateTextField =
//                        if (lastBottomBatValue.isEmpty()) StateTextField.EMPTY else StateTextField.WRITING
////                    listImageAddress.add(response.readText())
//                }
//            }
//        }
//    }

    val menuHelper = MenuVisibilityHelper(action = { })

    var selectedPath by mutableStateOf("${Routes.FILE.ANDROID_DIR + Routes.FILE.DOWNLOAD_DIR}")

    var listFiles by mutableStateOf(emptyList<String>())
    var bottomSheetState by mutableStateOf(false)
//    val isAttachMenu

}

@Serializable
data class DeleteMessageEntity(
    val idMessage: String,
    val idUser: String,
    val idChat: String
)

data class SelectedImageMessage(
    var message: ChatMessageDC,
    var imageRequest: String
)

@Serializable
data class MessageDC(
    var listImages: List<String> = emptyList(),
    var listFiles: List<FileDC> = emptyList(),
    var message: String = ""
)

@kotlinx.serialization.Serializable
data class EditMessageEntity(
    val idMessage: String,
    val idUser: String,
    val idChat: String,
    val newMessage: String
)
