package com.foggyskies.petapp.presentation.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.globalviews.Screens
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.call.*
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
import kotlinx.serialization.json.Json
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

    open class InternetCheck {
        open fun startWithCheck(kFunction0: () -> Unit) {

        }
    }


    suspend fun initService(msViewModel: MainSocketViewModel){

        msViewModel.sendAction(Routes.SERVER.WEBSOCKETCOMMANDS.CHATS)

    }


    var selectedPage by mutableStateOf(Screens.IDLE)

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

    val repositoryUserDB: RepositoryUserDB by inject(RepositoryUserDB::class.java)

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
//        install(ContentNegotiation){
//            json(Json {
//                prettyPrint = true
//                isLenient = true
//            })
//        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
    }

    fun checkInternet(func: () -> Unit) {
        if (MainActivity.isNetworkAvailable.value) {
            func()
        }
    }

//    fun onSelectRightMenu(itemMenu: String, msViewModel: MainSocketViewModel) {
//        viewModelScope.launch {
//
//            when (itemMenu) {
//                "Пользователи" -> {
//                    menuHelper.changeVisibilityMenu(MENUS.SEARCHUSERS, secondAction = {
//                        menuHelper.setVisibilityMenu(MENUS.RIGHT, false)
//                    })
//                    msViewModel.connectToSearchUsers()
//                }
//                "Беседы" -> {
//                    repositoryUserDB.getChats(msViewModel)
////                    if (msViewModel.listChats.isNotEmpty())
//                    menuHelper.changeVisibilityMenu(MENUS.CHATS)
////                getChats(msViewModel)
//                }
//                "Друзья" -> {
//                    repositoryUserDB.getFriends(msViewModel)
////                    if (msViewModel.listFriends.isNotEmpty())
//                    menuHelper.changeVisibilityMenu(MENUS.FRIENDS)
////                    msViewModel.sendAction("getFriends|")
//                    msViewModel.sendAction("getRequestsFriends|")
//
//                }
//            }
//        }
//    }

    fun getContent() {
        viewModelScope.launch {
            client.use {
                val a: List<SelectedPostWithIdPageProfile> =
                    it.get("${Routes.SERVER.REQUESTS.BASE_URL}/content/getPosts") {
                        this.headers["Auth"] = MainActivity.TOKEN
                    }
                reducer.sendEvent(HomeScreenEvent.ChangePosts(a))
            }
        }
    }

//    fun getChats(msViewModel: MainSocketViewModel) {
//        viewModelScope.launch {
//        }
//    }

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