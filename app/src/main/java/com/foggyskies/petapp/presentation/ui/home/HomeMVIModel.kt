package com.foggyskies.petapp.presentation.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.domain.db.ChatDB
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

enum class Menus {
    Friends, Chats, Post
}

interface UiState

interface UiEvent

abstract class BaseViewModel<T : UiState, in E : UiEvent> : ViewModel() {
    abstract val state: Flow<T>
}

sealed class HomeScreenEvent : UiEvent {
    data class ShowDialog(val show: Boolean) : HomeScreenEvent()
    data class PlusCount(val count: Int) : HomeScreenEvent()
    data class ChangePosts(val posts: List<SelectedPostWithIdPageProfile>) : HomeScreenEvent()
    object FFF : HomeScreenEvent() {
        suspend fun getContent(): List<SelectedPostWithIdPageProfile> {
            val client = HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }
            return client.get("http://${MainActivity.MAINENDPOINT}/content/getPosts")
//                .use {
//                return it.get("http://${MainActivity.MAINENDPOINT}/content/getPosts") {
//                        this.headers["Auth"] = MainActivity.TOKEN
//                    }
////                reducer.setState(reducer.state.value.copy(postsList = a))
//            }
        }
    }

//    companion object GetContent : HomeScreenEvent() {

//    }
}

data class HomeScreenUiState(
    val isShowDialog: Boolean,
    val count: Int,
    val postsList: List<SelectedPostWithIdPageProfile>
) : UiState {
    companion object {
        fun initial() = HomeScreenUiState(
            isShowDialog = false,
            count = 0,
            postsList = emptyList()
        )
    }
}

abstract class Reducer<S : UiState, E : UiEvent>(initialVal: S) {
    private val _state: MutableStateFlow<S> = MutableStateFlow(initialVal)
    val state: StateFlow<S>
        get() = _state

    fun sendEvent(event: E) {
        reduce(_state.value, event)
    }

    fun setState(newState: S) {
        val success = _state.tryEmit(newState)
    }

    abstract fun reduce(oldState: S, event: E)
}

class HomeMVIModel :
    BaseViewModel<HomeScreenUiState, HomeScreenEvent>() {

    private val reducer = HomeReducer(HomeScreenUiState.initial())

    override val state: StateFlow<HomeScreenUiState>
        get() = reducer.state

    val swipableMenu = SwappableMenu()

    val repositoryChatDB: RepositoryChatDB by inject(RepositoryChatDB::class.java)

    val menuHelper = MenuVisibilityHelper(action = { swipableMenu.isReadyMenu = it })

    var postScreenHandler = PostScreenHandler()

    fun photoScreenClosed() {
        viewModelScope.launch {
            delay(400)
            swipableMenu.isReadyMenu = true
        }
    }

    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
        }
    }

    fun getContent() {
        viewModelScope.launch {
            client.use {
                val a: List<SelectedPostWithIdPageProfile> =
                    it.get("http://${MainActivity.MAINENDPOINT}/content/getPosts") {
                        this.headers["Auth"] = MainActivity.TOKEN
                    }
                reducer.sendEvent(HomeScreenEvent.ChangePosts(a))
            }
        }
    }

    fun getChats(msViewModel: MainSocketViewModel) {
        viewModelScope.launch {
            repositoryChatDB.getChats(msViewModel)
        }
//        viewModelScope.launch {
//
//            val chats = msViewModel.chatDao?.getAllChats()?.toMutableList()!!
//            val formattedChat = chats.map {
//                FormattedChatDC(
//                    id = it.idChat,
//                    nameChat = it.companionName,
//                    image = it.imageCompanion,
//                    idCompanion = it.companionId
//                )
//            }
//            msViewModel.listChats = formattedChat.toMutableList()
//            msViewModel.sendAction("getChats|")
//        }
    }

    private class HomeReducer(initial: HomeScreenUiState) :
        Reducer<HomeScreenUiState, HomeScreenEvent>(initial) {
        override fun reduce(oldState: HomeScreenUiState, event: HomeScreenEvent) {
            when (event) {
                is HomeScreenEvent.ShowDialog -> {
                    setState(oldState.copy(isShowDialog = event.show))
                }
                is HomeScreenEvent.PlusCount -> {
                    setState(oldState.copy(count = event.count))
                }
                is HomeScreenEvent.ChangePosts -> {
                    setState(oldState.copy(postsList = event.posts))
                }
            }
        }
    }
}

//@Composable
//fun Aaaa() {
//    val viewModel: HomeMVIModel = HomeMVIModel()
//    val state = viewModel.state.collectAsState()
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//    }
//}

class RepositoryChatDB(
    val dbChat: ChatDB
) {

    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
        }
    }

    suspend fun getChats(msViewModel: MainSocketViewModel) {
        val localChats = dbChat.chatDao().getAllChats()
        val formattedChat = localChats.map {
            FormattedChatDC(
                id = it.idChat,
                nameChat = it.companionName,
                image = it.imageCompanion,
                idCompanion = it.companionId
            )
        }
        msViewModel.listChats = formattedChat.toMutableList()
        msViewModel.sendAction("getChats|")
    }

    suspend fun updateChats(
        needAddItems: List<FormattedChatDC>,
        deletedItems: List<FormattedChatDC>
    ) {
        needAddItems.forEach {
            dbChat.chatDao().insertChat(it.toChat())
        }
        deletedItems.forEach {
            dbChat.chatDao().deleteChat(it.toChat())
        }
    }

//    suspend fun saveImage(
//        idChat: String,
//        idMessage: String,
//        imageLink: String,
//        image: Bitmap,
//        imagesList: List<String>
//    ) {
//        val message = dbChat.getOneMessage(idChat, idMessage)
//        val dbListImages = message?.listImages
//        dbListImages?.let { listLink ->
//            val newList: List<Map<String, String>> = if (listLink.isEmpty()) {
//                val nameFile = "image_${Date().time}.png"
//                val baos = ByteArrayOutputStream()
//                image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                val byte_array = baos.toByteArray()
//                File(Routes.FILE.MAIN_APP_DIR + Routes.FILE.IMAGES + nameFile).writeBytes(
//                    byte_array
//                )
//                listOf(mapOf(imageLink to nameFile))
//            } else {
//                listLink.map { mapImages ->
//                    val map = Json.decodeFromString<Map<String, String>>(mapImages)
//                    if (map.containsKey(imageLink)) {
//                        val isExistFile =
//                            File(Routes.FILE.MAIN_APP_DIR + Routes.FILE.IMAGES + map[imageLink]).exists()
//                        if (!isExistFile) {
//                            val nameFile = "/image_${Date().time}.png"
//                            val baos = ByteArrayOutputStream()
//                            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                            val byte_array = baos.toByteArray()
//                            File(Routes.FILE.MAIN_APP_DIR + Routes.FILE.IMAGES + nameFile).writeBytes(
//                                byte_array
//                            )
//                            mapOf(imageLink to nameFile)
//                        } else {
//                            map
//                        }
//                    } else {
//                        val nameFile = "/image_${Date().time}.png"
//                        val baos = ByteArrayOutputStream()
//                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                        val byte_array = baos.toByteArray()
//                        File(Routes.FILE.MAIN_APP_DIR + Routes.FILE.IMAGES + nameFile).writeBytes(
//                            byte_array
//                        )
//                        mapOf(imageLink to nameFile)
//                    }
//                }
//
//            }
////            val newList: List<String> = listLink.map { link ->
////                val map = Json.decodeFromString<Map<String, String>>(link)
////                if (map.containsKey(imageLink)) {
////                    val isExistFile =
////                        File(Routes.FILE.MAIN_APP_DIR + Routes.FILE.IMAGES + map[imageLink]).exists()
////                    if (!isExistFile) {
////                        val nameFile = "image_${Date().time}.png"
////                        val baos = ByteArrayOutputStream()
////                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////                        val byte_array = baos.toByteArray()
////                        File(Routes.FILE.MAIN_APP_DIR + Routes.FILE.IMAGES + nameFile).writeBytes(
////                            byte_array
////                        )
////                        Json.encodeToString(mapOf(imageLink to nameFile))
////                    } else {
////                        Json.encodeToString(map[imageLink])
////                    }
////                } else {
////                    val nameFile = "image_${Date().time}.png"
////                    val baos = ByteArrayOutputStream()
////                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
////                    val byte_array = baos.toByteArray()
////                    File(Routes.FILE.MAIN_APP_DIR + Routes.FILE.IMAGES + nameFile).writeBytes(
////                        byte_array
////                    )
////                    Json.encodeToString(mapOf(imageLink to nameFile))
////                }
////            }
//            val listString = Json.encodeToString(newList)
//            dbChat.reWriteImages(idChat, idMessage, listString)
//        }
//    }

//    suspend fun checkImageLink(idChat: String, idMessage: String, remoteImageLink: String): File? {
//        var isExist = false
//        var image: File? = null
//
//        val listImages = dbChat.getImageList(idChat, idMessage)
//        listImages.forEach forC@{
//            if (it.containsKey(remoteImageLink)) {
//                isExist = true
//                image = File("${Routes.FILE.MAIN_APP_DIR}/Images${it[remoteImageLink]}")
//                return image
//            }
//        }
//        return image
//    }
//
//    suspend fun checkImageLink(idChat: String, idMessage: String, dbImageLink: String, a: String = ""): File? {
//        var isExist = false
//        var image: File? = null
//        val key = Json.decodeFromString<Map<String, String>>(dbImageLink).keys.map {
//            it
//        }
//        val listImages = dbChat.getImageList(idChat, idMessage)
//        listImages.forEach forC@{
//            if (it.containsKey(key[0])) {
//                isExist = true
//                image = File("${Routes.FILE.MAIN_APP_DIR}/Images${it[key[0]]}")
//                return image
//            }
//        }
//        return image
//    }

    suspend fun insertMessage(idChat: String, message: ChatMessage) {
        dbChat.insertMessages(idChat, message = message)
    }
}