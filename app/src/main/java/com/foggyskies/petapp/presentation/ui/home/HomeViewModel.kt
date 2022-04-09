package com.foggyskies.petapp.presentation.ui.home

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
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

    val swipableMenu = SwappableMenu()

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

    private val listOffsetsForCircle by derivedStateOf {
        listOf(
            Offset(x = 10 * density, y = -70 * density),
            Offset(x = -50 * density, y = -45 * density),
            Offset(x = -70 * density, y = 10 * density)
        )
    }

    var isTappedScreen by mutableStateOf(false)

    private var offsetStart by mutableStateOf(Offset.Zero)

    val offsetEdit by derivedStateOf {
        DpOffset(x = (offsetStart.x / density).dp, y = (offsetStart.y / density).dp)
    }

    val listOffsetGlobal by derivedStateOf {
        listOffsetsForCircle.map {
            it + offsetStart
        }
    }
}
