package com.foggyskies.petapp

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.home.UsersSearchState
import com.foggyskies.petapp.presentation.ui.profile.human.PageProfileFormattedDC
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.routs.Routes.SERVER.REQUESTS.BASE_URL
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import java.io.File

data class FormattedItem<T>(
    var item: T,
    var isVisible: Boolean
)

data class OldListInfo<T>(
    var equalItemsList: MutableList<T>,
    var newItemsList: MutableList<T>,
    var depricatedItemsList: MutableList<T>
)

//data class NewMessagesCollectionWA(
//    val id: String,
//    val new_messages: List<ChatMessage>,
//    var isVisible: MutableState<Boolean> = mutableStateOf(true)
//)

//@kotlinx.serialization.Serializable
//data class NewMessagesCollection(
//    val id: String,
//    val new_messages: List<ChatMessage>
//){
//    fun toNMWA(): NewMessagesCollectionWA {
//        return NewMessagesCollectionWA(
//            id = id,
//            new_messages = new_messages,
//            isVisible = mutableStateOf(true)
//        )
//    }
//}

data class NewMessagesCollectionWA(
    val id: String,
    val image: String,
    val username: String,
    val new_message: ChatMessageDC,
    var isVisible: MutableState<Boolean> = mutableStateOf(true)
)

@kotlinx.serialization.Serializable
data class WatchNewMessage(
    val idChat: String,
    val image: String,
    val username: String,
    val new_message: ChatMessageDC
){
    fun toNMWA(): NewMessagesCollectionWA {
        return NewMessagesCollectionWA(
            id = idChat,
            image = image,
            username = username,
            new_message = new_message,
            isVisible = mutableStateOf(true)
        )
    }
}

class MainSocketViewModel : ViewModel() {

    val repositoryUserDB: RepositoryUserDB by inject(RepositoryUserDB::class.java)

//    var chatDao: ChatDao? = null

    var mainSocket: WebSocketSession? = null

    var socket: WebSocketSession? = null

    private val _users = mutableStateOf(UsersSearchState())
    val users: State<UsersSearchState> = _users

    var listChats by mutableStateOf(mutableListOf<FormattedChatDC>())

    var listRequests = mutableStateListOf<UserIUSI>()

    var listFriends = mutableStateListOf<UserIUSI>()


    fun connectToSearchUsers() {
        if (MainActivity.isNetworkAvailable.value)
            CoroutineScope(Dispatchers.IO).launch {
                val client = HttpClient(CIO) {
                    install(WebSockets)
                }
                socket = client.webSocketSession {
                    url("${Routes.SERVER.WEBSOCKETCOMMANDS.BASE_URL}/user")
                    header("Auth", MainActivity.TOKEN)
                }
                observeMessages().onEach { user ->

                    _users.value = users.value.copy(
                        users = user
                    )
                }.launchIn(this)
            }
    }

    suspend fun observeMessages(): Flow<List<UsersSearch>> {

        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val string = (it as? Frame.Text)?.readText() ?: ""
                    val json = Json.decodeFromString<List<UsersSearch>>(string)
                    json
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    fun sendMessage(username: String) {
        if (MainActivity.isNetworkAvailable.value)
            CoroutineScope(Dispatchers.IO).launch {
                socket?.send(username)
            }
    }

    fun disconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            socket?.close()
            socket = null
            _users.value.clear()
        }
    }

    fun getFriends() {
        CoroutineScope(Dispatchers.IO).launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listFriends = it.get("$BASE_URL/friends") {
                    this.headers["Auth"] = MainActivity.TOKEN
                }
            }
        }
    }

    fun getRequestsFriend() {
        CoroutineScope(Dispatchers.IO).launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listFriends = it.get("$BASE_URL/friends") {
                    this.headers["Auth"] = MainActivity.TOKEN
                }
            }
        }
    }

    fun sendAction(action: String) {
        if (MainActivity.isNetworkAvailable.value)
//            if (action.contains("loadFile|"))
            CoroutineScope(Dispatchers.IO).launch {
                if (mainSocket == null) {
                    createMainSocket()
                    do {
                        delay(500)
                        mainSocket?.send(action)
                    } while (mainSocket == null)
                } else {
                    mainSocket?.send(action)
                }
            }
    }

    private fun observeActions(): Flow<String> {

        return try {
            mainSocket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val string = (it as? Frame.Text)?.readText() ?: ""
                    string
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    var listPagesProfile by mutableStateOf(emptyList<PageProfileFormattedDC>())

//    var listNewMessages by mutableStateOf(emptyList<NewMessagesCollection>())
    var listNewMessages = mutableStateListOf<NewMessagesCollectionWA>()

    fun createMainSocket() {
        CoroutineScope(Dispatchers.IO).launch {

            var idUser = ""

            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                idUser = it.post<String>("$BASE_URL/createMainSocket") {
                    this.headers["Auth"] = MainActivity.TOKEN
                }
            }

            val client = HttpClient(CIO) {
                install(WebSockets)
            }
            mainSocket = client.webSocketSession {
                url("${Routes.SERVER.WEBSOCKETCOMMANDS.BASE_URL}/mainSocket/$idUser")
                header("Auth", MainActivity.TOKEN)
            }
            observeActions().onEach { user ->
                val regex = "^\\w+(?=\\|)".toRegex()
                val action = regex.find(user)?.value
                Log.e("ACTION", action.toString())
                if (action != null) {
                    val formatted = user.replace("$action|", "")
                    val map_actions = mapOf(
                        "getFriends" to {
                            val json = Json.decodeFromString<List<UserIUSI>>(formatted)
                            val needAddItems: List<UserIUSI> = json - listFriends.toSet()
                            val deletedItems = listFriends - json.toSet()
                            CoroutineScope(Dispatchers.IO).launch {
                                repositoryUserDB.updateFriends(needAddItems, deletedItems)
                            }
                            listFriends = json.toMutableStateList()
                        },
                        "getChats" to {
                            val json = Json.decodeFromString<List<FormattedChatDC>>(formatted)
                            val needAddItems: List<FormattedChatDC> = json - listChats.toSet()
                            val deletedItems = listChats - json.toSet()
                            CoroutineScope(Dispatchers.IO).launch {
                                repositoryUserDB.updateChats(needAddItems, deletedItems)
                            }
                            listChats = json.toMutableList()
                        },
                        "getRequestsFriends" to {
                            val json = Json.decodeFromString<List<UserIUSI>>(formatted)
                            if (listRequests.size != 0 && json.isEmpty()) {
                                listRequests.clear()
                            }
                            json.forEach { request ->
                                if (listRequests.size > json.size) {
                                    val newList = mutableListOf<UserIUSI>()
                                    val removeList = mutableListOf<UserIUSI>()
                                    listRequests.forEach { request ->
                                        if (!json.contains(request)) {
                                            removeList.add(request)
                                        }
                                    }
                                    listRequests.removeAll(removeList)
                                } else {
                                    if (!listRequests.contains(request)) {
                                        listRequests.add(request)
                                    }
                                }
                            }
                        },
                        "getInternalNotification" to {
                            val json = Json.decodeFromString<Notification>(formatted)
                            listNotifications.add(json.toNWV())
                        },
                        "getPagesProfile" to {
                            val json =
                                Json.decodeFromString<List<PageProfileFormattedDC>>(formatted)
                            listPagesProfile = json.toMutableList()
                        },
                        "getNewMessages" to {
                            val json =
                                Json.decodeFromString<WatchNewMessage>(formatted)
                            listNewMessages.add(json.toNMWA())
//                            listPagesProfile = json.toMutableList()
                        },
                        "loadFile" to {

                            val nameOperation = ".+(?=\\|)".toRegex().find(formatted)?.value!!
                            val data = formatted.replaceFirst("$nameOperation|", "")
                            val file = File("${Routes.FILE.ANDROID_DIR}/Download/$nameOperation")
                            file.createNewFile()
                            val bytes = Base64.decode(data, Base64.DEFAULT)
                            file.appendBytes(bytes)
                        }
                    )
                    map_actions[action]?.invoke()
                }
            }.launchIn(this)
        }
    }

//    val listInternalNotification = derivedStateOf {
//        if (listNewMessages.isEmpty()){
//
//        }
//    }

    var newNotificationList = mutableListOf<Char>()
    var oldNotificationList = mutableListOf<Char>()
    var transformedNotificationList = mutableListOf<FormattedItem<Char>>()

    val oldNotificationList_2 = derivedStateOf {
        checkOldListByNewList(oldNotificationList, newNotificationList)
    }

    val transformedNotificationList_2 = derivedStateOf {

    }

    fun firstInit() {
        newNotificationList = mutableListOf('A', 'B', 'C', 'D')
        oldNotificationList = newNotificationList
        transformedNotificationList = newNotificationList.transformToFormattedItem()
        println("newList - $newNotificationList\n oldLsit - $oldNotificationList \n transformedList - $transformedNotificationList")
    }

    /**
     *  Internal Notifications
     */

    val listNotifications = mutableStateListOf<NotificationWithVisilble>(
//        NotificationWithVisilble(
//            id = "пцупупуыуп",
//            idUser = "rtjtyrjty",
//            title = "Kalteesfxx crfad",
//            description = "Приветbcg",
//            image = "",
//            status = "sbhfdnb"
//        ),
//        NotificationWithVisilble(
//            id = "jyk454",
//            idUser = "dwfwagesghdbff",
//            title = "Kalbcdfterfad",
//            description = "Приветnmvg",
//            image = "",
//            status = "sgesgsg"
//        ),
//        NotificationWithVisilble(
//            id = "hsdhrh4554h",
//            idUser = "dwgsegsfwaf",
//            title = "Kalgsghdfhdterfad",
//            description = "Приветbnvc",
//            image = "",
//            status = "ewewwe435"
//        ), NotificationWithVisilble(
//            id = "hreherh55e",
//            idUser = "dwfwsegsegsegaf",
//            title = "esgsegsegse",
//            description = "Приветjkj",
//            image = "",
//            status = "dsvcbxfbr"
//        ), NotificationWithVisilble(
//            id = "wdhehe545yhe5waf",
//            idUser = "dsgssegwfwaf",
//            title = "gsdgdbcx bcv",
//            description = "Приветb j",
//            image = "",
//            status = "gshsgdsbdsb"
//        ), NotificationWithVisilble(
//            id = "hdeh54eh543",
//            idUser = "dwfwagreghrehf",
//            title = "egsgeegere",
//            description = "Приветkkkkv",
//            image = "",
//            status = "gedhsgvsds"
//        ), NotificationWithVisilble(
//            id = "hdeh54eh543",
//            idUser = "dwfwagreghrehf",
//            title = "egsgeegere",
//            description = "Приветkkkkv",
//            image = "",
//            status = "gedhsgvsds"
//        )
    )

    var selectedMuteBatItem = mutableStateOf(0)

    val isNotifyVisible by derivedStateOf {
//        listNotifications.isNotEmpty()
        listNewMessages.isNotEmpty() || isMuteBarVisible
    }

    var listValuesMute = listOf(
        "1ч",
        "4ч",
        "8ч",
        "24ч",
    )

    var isMuteBarVisible by mutableStateOf(false)

    fun notificationReceived(notification: Notification) {
        CoroutineScope(Dispatchers.IO).launch {
            mainSocket?.send("INR|N")
        }
    }

//    fun muteChat(){
//        viewModelScope.launch {
//            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 30000
//                }
//            }.use {
//                listFriends = it.get("$BASE_URL$MUTE_CHAT") {
//                    this.headers["Auth"] = MainActivity.TOKEN
//                    body = MuteChatDC()
//                }
//            }
//        }
//    }

}

data class MuteChatDC(
    val idChat: String,
    val timeMute: String
)

fun <T> checkOldListByNewList(oldList: MutableList<T>, newList: MutableList<T>): OldListInfo<T> {
    val equalItemsList = mutableListOf<T>()
    val newItemsList = mutableListOf<T>()
    val depricatedItemsList = mutableListOf<T>()

    newList.forEach { item ->
        if (oldList.contains(item)) {
            equalItemsList.add(item)
        } else {
            newItemsList.add(item)
        }
    }
    if (oldList != equalItemsList) {
        depricatedItemsList.addAll(oldList - newList)
    }
    val result = OldListInfo(
        newItemsList = newItemsList,
        equalItemsList = equalItemsList,
        depricatedItemsList = depricatedItemsList
    )

    return result
}

fun <T> List<T>.transformToFormattedItem(): MutableList<FormattedItem<T>> {
    val newList = mutableListOf<FormattedItem<T>>()
    this.forEach { item ->
        val newItem = FormattedItem(
            item = item,
            isVisible = false
        )
        newList.add(newItem)
    }
    return newList
}

fun <T> MutableList<T>.processingList(work: OldListInfo<T>) {
    work.newItemsList.forEach {
        this.add(it)
    }
}