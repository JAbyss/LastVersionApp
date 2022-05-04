package com.foggyskies.petapp.presentation.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.domain.db.Messages
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.presentation.ui.profile.human.ContentPreviewDC
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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


    val swipableMenu = SwappableMenu()

    var isReadyMenu by mutableStateOf(true)

    var isUsersMenuOpen by mutableStateOf(false)

    var isChatsMenuOpen by mutableStateOf(false)

    var isFriendsMenuOpen by mutableStateOf(false)

    fun photoScreenClosed() {
        viewModelScope.launch {
            delay(400)
            swipableMenu.isReadyMenu = true
        }
    }

    var isVisiblePhotoWindow by mutableStateOf(false)

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

    var isRightMenuOpen by mutableStateOf(false)

    var density by mutableStateOf(0f)

    private var offsetStart by mutableStateOf(Offset.Zero)

    /**
     * POST SCREEN
     */

    var postScreenHandler = PostScreenHandler()

    var selectedPost by mutableStateOf<SelectedPostWithIdPageProfile?>(null)

    var listContents by mutableStateOf(emptyList<SelectedPostWithIdPageProfile>())

    var listComments by mutableStateOf(emptyList<CommentDC>())

    fun getPosts() {
        viewModelScope.launch {
            Log.e("GETPOST ЗАПРОС", "ЗАПРОС ПОСТЫ")

            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listContents =
                    it.get("http://${MainActivity.MAINENDPOINT}/content/getPosts") {
                        this.headers["Auth"] = MainActivity.TOKEN
                    }
            }
        }
    }

    fun sendNewComment() {
        viewModelScope.launch {
            if (postScreenHandler.commentValue.text.isNotBlank() || postScreenHandler.commentValue.text.isNotEmpty()) {
                val comment = CommentDC(
                    id = "",
                    idUser = IDUSER,
                    message = postScreenHandler.commentValue.text,
                    date = ""
                )
                listComments = listComments + comment
                HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                    install(HttpTimeout) {
                        requestTimeoutMillis = 3000
                    }
                }.use {
                    it.post<HttpResponse>("http://${MainActivity.MAINENDPOINT}/content/addCommentToPost") {
                        headers["Auth"] = MainActivity.TOKEN
                        headers["Content-Type"] = "Application/Json"
                        parameter("idPageProfile", selectedPost?.idPageProfile)
                        parameter("idPost", selectedPost?.item?.id!!)
                        body = comment
                    }
                }
                postScreenHandler.commentValue = TextFieldValue("")
            }
        }
    }

    fun selectPost(post: SelectedPostWithIdPageProfile) {
        viewModelScope.launch {
            selectedPost = post
            isLiked = post.isLiked

            swipableMenu.isReadyMenu = false
            isVisiblePhotoWindow = true

            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listComments = it.get("http://${MainActivity.MAINENDPOINT}/content/getComments") {
                    this.headers["Auth"] = MainActivity.TOKEN
                    parameter("idPageProfile", selectedPost?.idPageProfile)
                    parameter("idPost", selectedPost?.item?.id!!)
                }
            }
        }
    }

    var isLiked by mutableStateOf(false)

    fun likePost() {
        viewModelScope.launch {

            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                isLiked = it.get("http://${MainActivity.MAINENDPOINT}/content/addLikeToPost") {
                    this.headers["Auth"] = MainActivity.TOKEN
                    parameter("idPageProfile", selectedPost?.idPageProfile)
                    parameter("idPost", selectedPost?.item?.id!!)
                }
                selectedPost?.isLiked = isLiked
            }
        }
    }

    var likedUsersList by mutableStateOf(emptyList<UserIUSI>())

    fun getLikedUsers() {
        viewModelScope.launch {

            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                likedUsersList =
                    it.get("http://${MainActivity.MAINENDPOINT}/content/getLikedUsers") {
                        this.headers["Auth"] = MainActivity.TOKEN
                        parameter("idPageProfile", selectedPost?.idPageProfile)
                        parameter("idPost", selectedPost?.item?.id!!)
                    }
            }
        }
    }
//    @Serializable
//    @Parcelize
//    data class FormattedChatDC(
//        var id: String,
//        var nameChat: String,
//        var idCompanion: String,
//        var image: String,
//        var lastMessage: String = ""
//    ) : Parcelable

    fun getChats(msViewModel: MainSocketViewModel) {
        viewModelScope.launch {
//            msViewModel.listChats =
            val chats = msViewModel.chatDao?.getAllChats()?.toMutableList()!!
            val formattedChat = chats.map {
                FormattedChatDC(
                    id = it.idChat,
                    nameChat = it.companionName,
                    image = it.imageCompanion,
                    idCompanion = it.companionId
                )
            }
            val CREATE_TABLE =
                "CREATE TABLE ${Messages.TABLE_NAME + "fwafawf"} (" +
                        " ${Messages.COLUMN_ID} TEXT PRIMARY KEY," +
                        " ${Messages.COLUMN_AUTHOR} TEXT, ${Messages.COLUMN_DATE} TEXT," +
                        " ${Messages.COLUMN_MESSAGE} TEXT," +
                        " ${Messages.COLUMN_LIST_IMAGES} TEXT )"
//            a.writableDatabase.execSQL(CREATE_TABLE)
//            msViewModel.chatDao?.createNewMessageTable("623480a2132ed64da24c5e3b")
            msViewModel.listChats = formattedChat.toMutableList()
            msViewModel.sendAction("getChats|")
//            val isExist = dao.checkOnExistChat(idChat = "623480a2132ed64da24c5e3b")
//            if (isExist)
//            val chat = dao.getChatByIdChat(idChat = "623480a2132ed64da24c5e3b")
        }
//        dao.insertChat(Chat(
//            idChat = "623480a2132ed64da24c5e3b",
//            companionId = "62334df9373ec10e05f9e588",
//            companionName = "Kalterfad",
//            imageCompanion = "images/avatars/avatar_626819ddc8adde276e90709f.png"
//        ))

    }

}

@kotlinx.serialization.Serializable
data class SelectedPostWithIdPageProfile(
    var idPageProfile: String,
    var item: ContentPreviewDC,
    var author: String,
    var image: String,
    var countLikes: String = "",
    var countComets: String = "",
    var isLiked: Boolean = false
)

@Serializable
data class ContentUsersDC(
    var id: String,
    var type: String,
    var likes: List<String>,
    var comments: List<CommentDC>,
    var address: String,
    var description: String = ""
)

@Serializable
data class CommentDC(
    var id: String,
    var idUser: String,
    var message: String,
    var date: String
)

enum class StatePost {
    IMAGE, COMMENTS, LIKES
}