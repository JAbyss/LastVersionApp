package com.foggyskies.petapp.presentation.ui.home

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.domain.repository.RepositoryChatDB
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.profile.human.MENUS
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
import org.koin.java.KoinJavaComponent.inject

enum class Menus {
    Friends, Chats, Post
}

interface UiState

interface UiEvent

abstract class BaseViewModel<T : UiState, in E : UiEvent> : ViewModel() {
    abstract val state: Flow<T>
}

sealed class HomeScreenEvent : UiEvent {
    data class ChangePosts(val posts: List<SelectedPostWithIdPageProfile>) : HomeScreenEvent()

//    companion object GetContent : HomeScreenEvent() {

//    }
}

data class HomeScreenUiState(
    val postsList: List<SelectedPostWithIdPageProfile>
) : UiState {
    companion object {
        fun initial() = HomeScreenUiState(
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

    open class InternetCheck{
        open fun startWithCheck(kFunction0: () -> Unit) {

        }
    }

//    interface checkInternet {
//        fun checkInternet(): Boolean {
//            return true
//        }
//    }
//
//    object a : checkInternet {
//
//        fun getA():  {
//            super.checkInternet()
//        }
//
//        override fun checkInternet(): Boolean {
//            return super.checkInternet()
//        }
//    }

//    class RightMenus : InternetCheck() {
//
//    }
//    object RightMenusActions: InternetAction {
//        fun getFriends(kFunction0: () -> Unit) {
//            InternetAction.startWithCheck(kFunction0)
//        }
//    }

    val swipableMenu = SwappableMenu()

    val listIconHome = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_profile,
            offset = Offset(x = 10f, y = -70f),
            onValueSelected = {
                it.navigate(NavTree.Profile.name)
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
            offset = Offset(x = -50f, y = -45f),
            onValueSelected = {
                it.navigate(NavTree.AdsHomeless.name)
            }
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_gamepad,
            offset = Offset(x = -70f, y = 10f),
            onValueSelected = {
                it.navigate(NavTree.Home.name)
            })
    )

    init {
        swipableMenu.listIcon = listIconHome
    }

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



    fun onSelectRightMenu(itemMenu: String, msViewModel: MainSocketViewModel){
        when (itemMenu) {
            "Пользователи" -> {
                menuHelper.changeVisibilityMenu(MENUS.SEARCHUSERS, secondAction = {
                    menuHelper.setVisibilityMenu(MENUS.RIGHT, false)
                })
//                            searchUsersSwitch()
                msViewModel.connectToSearchUsers()
            }
            "Беседы" -> {
                menuHelper.changeVisibilityMenu(MENUS.CHATS)
//                            chatsMenuSwitch()
//                            val a = FeedReaderDbHelper(context)
                getChats(msViewModel)
//                            msViewModel.sendAction("getChats|")
            }
            "Друзья" -> {
                menuHelper.changeVisibilityMenu(MENUS.FRIENDS)
//                            friendMenuSwitch()
                msViewModel.sendAction("getFriends|")
                msViewModel.sendAction("getRequestsFriends|")

            }
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
                is HomeScreenEvent.ChangePosts -> {
                    setState(oldState.copy(postsList = event.posts))
                }
            }
        }
    }
}