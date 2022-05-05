package com.foggyskies.petapp

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.domain.dao.ChatDao
import com.foggyskies.petapp.domain.repository.RepositoryChatDB
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.home.UsersSearchState
import com.foggyskies.petapp.presentation.ui.profile.human.PageProfileFormattedDC
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

data class FormattedItem<T>(
    var item: T,
    var isVisible: Boolean
)

data class OldListInfo<T>(
    var equalItemsList: MutableList<T>,
    var newItemsList: MutableList<T>,
    var depricatedItemsList: MutableList<T>
)

class MainSocketViewModel : ViewModel() {

    val repositoryChatDB: RepositoryChatDB by inject(RepositoryChatDB::class.java)

    var chatDao: ChatDao? = null

    var mainSocket: WebSocketSession? = null

    var socket: WebSocketSession? = null

    private val _users = mutableStateOf(UsersSearchState())
    val users: State<UsersSearchState> = _users

    var listChats by mutableStateOf(mutableListOf<FormattedChatDC>())

    var listRequests = mutableStateListOf<UserIUSI>()

    var listFriends = mutableStateListOf<UserIUSI>()

    fun connectToSearchUsers() {
        viewModelScope.launch {
            val client = HttpClient(CIO) {
                install(WebSockets)
            }
            socket = client.webSocketSession {
                url("ws://${MainActivity.MAINENDPOINT}/user")
                header("Auth", MainActivity.TOKEN)
            }
            observeMessages().onEach { user ->

                _users.value = users.value.copy(
                    users = user
                )
            }.launchIn(viewModelScope)
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
        viewModelScope.launch {
            socket?.send(username)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            socket?.close()
            socket = null
            _users.value.clear()
        }
    }

    fun getFriends() {
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listFriends = it.get("http://${MainActivity.MAINENDPOINT}/friends") {
                    this.headers["Auth"] = MainActivity.TOKEN
                }
            }
        }
    }

    fun getRequestsFriend() {
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listFriends = it.get("http://${MainActivity.MAINENDPOINT}/friends") {
                    this.headers["Auth"] = MainActivity.TOKEN
                }
            }
        }
    }

    fun sendAction(action: String) {
        viewModelScope.launch {
            if (mainSocket == null && MainActivity.isNetworkAvailable.value) {
                createMainSocket()
                do {
                    delay(500)
                    mainSocket?.send(action)
                } while (mainSocket == null)
            } else if (MainActivity.isNetworkAvailable.value) {
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

    var listPagesProfile = mutableListOf<PageProfileFormattedDC>()

    fun createMainSocket() {
        viewModelScope.launch {

            var idUser = ""

            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                idUser = it.post<String>("http://${MainActivity.MAINENDPOINT}/createMainSocket") {
                    this.headers["Auth"] = MainActivity.TOKEN
                }
            }

            val client = HttpClient(CIO) {
                install(WebSockets)
            }
            mainSocket = client.webSocketSession {
                url("ws://${MainActivity.MAINENDPOINT}/mainSocket/$idUser")
                header("Auth", MainActivity.TOKEN)
            }

            observeActions().onEach { user ->
                val regex = ".+(?=\\|)".toRegex()
                val action = regex.find(user)?.value
                Log.e("ACTION", action.toString())
                if (action != null) {
                    val formatted = user.replace("$action|", "")
                    val map_actions = mapOf(
                        "getFriends" to {
                            val json = Json.decodeFromString<List<UserIUSI>>(formatted)
                            json.forEach { friend ->

                                if (listFriends.size > json.size) {
                                    val listRemove = mutableListOf<UserIUSI>()
                                    listFriends.forEach { friend ->
                                        if (json.contains(friend)) {

                                        } else {
                                            listRemove.add(friend)
                                        }
                                    }
                                    listFriends.removeAll(listRemove)
                                } else {
                                    if (!listFriends.contains(friend)) {
                                        listFriends.add(friend)
                                    }
                                }
                            }
                        },
                        "getChats" to {
                            val json = Json.decodeFromString<List<FormattedChatDC>>(formatted)
                            val needAddItems: List<FormattedChatDC> = json - listChats
                            val deletedItems = listChats - json
//                            val repositoryChatDB: RepositoryChatDB by inject(RepositoryChatDB::class.java)
                            viewModelScope.launch {
                                repositoryChatDB.updateChats(needAddItems, deletedItems)
//                                needAddItems.forEach {
//                                    chatDao?.insertChat(it.toChat())
//                                }
//                                deletedItems.forEach {
//                                    chatDao?.deleteChat(it.toChat())
//                                }
                            }
                            listChats = json.toMutableList()
                        },
                        "getRequestsFriends" to {
                            val json = Json.decodeFromString<List<UserIUSI>>(formatted)
                            if (listRequests.size != 0 && json.size == 0) {
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
                        }
                    )
                    map_actions[action]?.invoke()
                }
            }.launchIn(viewModelScope)
        }
    }

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
        listNotifications.isNotEmpty()
    }

    var listValuesMute = listOf(
        "1ч",
        "4ч",
        "8ч",
        "24ч",
    )

    var isMuteBarVisible by mutableStateOf(false)

    fun notificationReceived(notification: Notification) {
        viewModelScope.launch {
            mainSocket?.send("INR|N")
        }
    }
}

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