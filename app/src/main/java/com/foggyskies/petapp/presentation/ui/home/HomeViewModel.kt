package com.foggyskies.petapp.presentation.ui.home

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.NotificationWithVisilble
import com.foggyskies.petapp.presentation.ui.globalviews.AnimationClass
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.SwipableMenu
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class UsersSearchState(
    var isLoading: Boolean = false,
    var users: List<UsersSearch> = listOf()
) {
    fun clear() {
        users = emptyList()
        isLoading = false
    }
}

class HomeViewModel : ViewModel() {

//    public override fun onCleared() {
//        viewModelScope.launch {
//            super.onCleared()
//            mainSocket?.close()
//            socket?.close()
//        }
//    }

    val swipableMenu = SwipableMenu()

    val circularSelector = CircularSelector()

    var isReadyMenu by mutableStateOf(true)

    var isUsersMenuOpen by mutableStateOf(false)

    var isChatsMenuOpen by mutableStateOf(false)

    var isFriendsMenuOpen by mutableStateOf(false)

    fun photoScreenClosed() {
        viewModelScope.launch {
            delay(1000)
            swipableMenu.isReadyMenu = true
        }
    }

    var isMenuClosed by mutableStateOf(true)

    fun menuClosing() {
        viewModelScope.launch {
            isMenuClosed = false
            delay(300)
            isMenuClosed = true
        }
    }

    var isVisiblePhotoWindow by mutableStateOf(false)

    var isVisibleLikeAnimation by mutableStateOf(false)

    var isStartSecondStepAnimation by mutableStateOf(false)

//    var listFriends = mutableStateListOf<UserIUSI>()
//
//    var mainSocket: WebSocketSession? = null
//
//    var listChats by mutableStateOf(mutableListOf<FormattedChatDC>())
//
//    var listRequests = mutableStateListOf<UserIUSI>()

//    fun createMainSocket() {
//        viewModelScope.launch {
//
//            var idUser = ""
//
//            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 3000
//                }
//            }.use {
//                idUser = it.post<String>("http://$MAINENDPOINT/createMainSocket") {
//                    this.headers["Auth"] = TOKEN
////                    parameter("idUser", "62333996647f7366746c563a")
//                }
////
//
//            }
//
//            val client = HttpClient(CIO) {
//                install(WebSockets)
//            }
//            mainSocket = client.webSocketSession {
//                url("ws://$MAINENDPOINT/mainSocket/$idUser")
//                header("Auth", TOKEN)
//            }
//
//            observeActions().onEach { user ->
//                val regex = ".+(?=\\|)".toRegex()
//                val action = regex.find(user)?.value
//                Log.e("ACTION", action.toString())
//                if (action != null) {
//                    val formatted = user.replace("$action|", "")
//                    val map_actions = mapOf(
//                        "getFriends" to {
//                            val json = Json.decodeFromString<List<UserIUSI>>(formatted)
//                            json.forEach { friend ->
//
//                                if (listFriends.size > json.size) {
//                                    val listRemove = mutableListOf<UserIUSI>()
//                                    listFriends.forEach { friend ->
//                                        if (json.contains(friend)) {
//
//                                        } else {
//                                            listRemove.add(friend)
//                                        }
//                                    }
//                                    listFriends.removeAll(listRemove)
//                                } else {
//                                    if (!listFriends.contains(friend)) {
//                                        listFriends.add(friend)
//                                    }
//                                }
//                            }
//                        },
//                        "getChats" to {
//                            val json = Json.decodeFromString<List<FormattedChatDC>>(formatted)
//                            json.forEach { chat ->
////                                if (listChats)
////                                if (!listChats.contains(chat)) {
////                                    listChats.add(chat)
////                                }
//                            }
//                            listChats = json.toMutableList()
//                        },
//                        "getRequestsFriends" to {
//                            val json = Json.decodeFromString<List<UserIUSI>>(formatted)
//                            if (listRequests.size != 0 && json.size == 0) {
//                                listRequests.clear()
//                            }
//                            json.forEach { request ->
//                                if (listRequests.size > json.size) {
//                                    val newList = mutableListOf<UserIUSI>()
//                                    val removeList = mutableListOf<UserIUSI>()
//                                    listRequests.forEach { request ->
//                                        if (!json.contains(request)) {
//                                            removeList.add(request)
//                                        }
//                                    }
//                                    listRequests.removeAll(removeList)
//                                } else {
//                                    if (!listRequests.contains(request)) {
//                                        listRequests.add(request)
//                                    }
//                                }
//                            }
//                        }
//                    )
//                    map_actions[action]?.invoke()
//                }
//            }.launchIn(viewModelScope)
//        }
//    }
//
//    private fun observeActions(): Flow<String> {
//
//        return try {
//            mainSocket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val string = (it as? Frame.Text)?.readText() ?: ""
////                    var json  = Json.parseToJsonElement(string)
//
////                    val json = Json.decodeFromString<List<UsersSearch>>(string)
//                    string
//                } ?: flow {}
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow { }
//        }
//    }
//
//    fun sendAction(action: String) {
//        viewModelScope.launch {
//            if (mainSocket == null) {
//                createMainSocket()
//                do {
//                    delay(500)
//                    mainSocket?.send(action)
//                } while (mainSocket == null)
//            } else {
//                mainSocket?.send(action)
//            }
//        }
//    }

    fun chatsMenuSwitch() {
        isChatsMenuOpen = !isChatsMenuOpen

        isRightMenuOpen = false
        isReadyMenu = !isChatsMenuOpen
    }

    fun friendMenuSwitch() {
        isFriendsMenuOpen = !isFriendsMenuOpen

        isRightMenuOpen = false
        isReadyMenu = !isFriendsMenuOpen
    }

    fun searchUsersSwitch() {
        isUsersMenuOpen = !isUsersMenuOpen

        isRightMenuOpen = false
        isReadyMenu = !isUsersMenuOpen
    }

//    fun getRequestsFriend() {
//        viewModelScope.launch {
//            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 3000
//                }
//            }.use {
//                listFriends = it.get("http://$MAINENDPOINT/friends") {
//                    this.headers["Auth"] = TOKEN
//                }
//            }
//        }
//    }
//
//    fun getFriends() {
//        viewModelScope.launch {
//            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 3000
//                }
//            }.use {
//                listFriends = it.get("http://$MAINENDPOINT/friends") {
//                    this.headers["Auth"] = TOKEN
//                }
//            }
//        }
//    }

    fun doubleTapLike() {
        viewModelScope.launch {
            isVisibleLikeAnimation = true
            delay(200)
            isStartSecondStepAnimation = true
            delay(500)
            isVisibleLikeAnimation = false
            isStartSecondStepAnimation = false
        }
    }

    var isRightMenuOpen by mutableStateOf(false)

    var density by mutableStateOf(0f)

    val radiusCircle = 80f

    val listOffsetsForCircle by derivedStateOf {
        listOf(
            Offset(x = 10 * density, y = -70 * density),
            Offset(x = -50 * density, y = -45 * density),
            Offset(x = -70 * density, y = 10 * density)
        )
    }

    var isTappedScreen by mutableStateOf(false)

    var offsetStart by mutableStateOf(Offset.Zero)

    val offsetEdit by derivedStateOf {
        DpOffset(x = (offsetStart.x / density).dp, y = (offsetStart.y / density).dp)
    }

    val listOffsetGlobal by derivedStateOf {
        listOffsetsForCircle.map {
            it + offsetStart
        }
    }

//    var socket: WebSocketSession? = null
//
//    private val _users = mutableStateOf(UsersSearchState())
//    val users: State<UsersSearchState> = _users
//
//    fun connectToSearchUsers() {
//        viewModelScope.launch {
//            val client = HttpClient(CIO) {
//                install(WebSockets)
//            }
//            socket = client.webSocketSession {
//                url("ws://$MAINENDPOINT/user")
//                header("Auth", TOKEN)
//            }
//            observeMessages().onEach { user ->
//
//                _users.value = users.value.copy(
//                    users = user
//                )
//            }.launchIn(viewModelScope)
//        }
//    }

//    suspend fun observeMessages(): Flow<List<UsersSearch>> {
//
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val string = (it as? Frame.Text)?.readText() ?: ""
//                    val json = Json.decodeFromString<List<UsersSearch>>(string)
//                    json
//                } ?: flow {}
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow { }
//        }
//    }
//
//    fun sendMessage(username: String) {
//        viewModelScope.launch {
//            socket?.send(username)
//        }
//    }
//
//    fun disconnect() {
//        viewModelScope.launch {
//            socket?.close()
//            socket = null
//            _users.value.clear()
//        }
//    }

    /**
     * InternalNotifications
     */

    val oldListNotifications = mutableStateListOf<AnimationClass>()

    val listNotifications = mutableStateListOf<NotificationWithVisilble>(NotificationWithVisilble(
            id = "пцупупуыуп",
            idUser = "rtjtyrjty",
            title = "Kalteesfxx crfad",
            description = "Приветbcg",
            image = "",
            status = "sbhfdnb"
        ),
        NotificationWithVisilble(
            id = "jyk454",
            idUser = "dwfwagesghdbff",
            title = "Kalbcdfterfad",
            description = "Приветnmvg",
            image = "",
            status = "sgesgsg"
        ),
        NotificationWithVisilble(
            id = "hsdhrh4554h",
            idUser = "dwgsegsfwaf",
            title = "Kalgsghdfhdterfad",
            description = "Приветbnvc",
            image = "",
            status = "ewewwe435"
        ), NotificationWithVisilble(
            id = "hreherh55e",
            idUser = "dwfwsegsegsegaf",
            title = "esgsegsegse",
            description = "Приветjkj",
            image = "",
            status = "dsvcbxfbr"
        ), NotificationWithVisilble(
            id = "wdhehe545yhe5waf",
            idUser = "dsgssegwfwaf",
            title = "gsdgdbcx bcv",
            description = "Приветb j",
            image = "",
            status = "gshsgdsbdsb"
        ), NotificationWithVisilble(
            id = "hdeh54eh543",
            idUser = "dwfwagreghrehf",
            title = "egsgeegere",
            description = "Приветkkkkv",
            image = "",
            status = "gedhsgvsds"
        ), NotificationWithVisilble(
            id = "hdeh54eh543",
            idUser = "dwfwagreghrehf",
            title = "egsgeegere",
            description = "Приветkkkkv",
            image = "",
            status = "gedhsgvsds"
        ))



}
