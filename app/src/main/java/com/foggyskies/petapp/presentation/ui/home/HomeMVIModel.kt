package com.foggyskies.petapp.presentation.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.presentation.ui.home.widgets.post.SelectedPostWithIdPageProfile
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class HomeMVIModel :
    ViewModel() {
    val backgroundScope = CoroutineScope(Dispatchers.IO)

    val listPosts = mutableStateListOf<SelectedPostWithIdPageProfile>()

    // ЛИСТЫ ДЛЯ ЗАПРОСОВ

    var listFriends = mutableStateListOf<UserIUSI>()
    var listChats = mutableStateListOf<FormattedChatDC>()
    var listRequestsFriends = mutableStateListOf<UserIUSI>()

    var swipableMenu = SwappableMenu()

    val repositoryUserDB: RepositoryUserDB by inject(RepositoryUserDB::class.java)

    val menuHelper = MenuVisibilityHelper(action = { swipableMenu.isReadyMenu = it })

//    var postScreenHandler = PostScreenHandler()

    fun photoScreenClosed() {
        viewModelScope.launch {
            delay(400)
            swipableMenu.isReadyMenu = true
        }
    }

    fun initCustomMenu(listIconHome: List<ItemSwappableMenu>){
        swipableMenu.listIcon = listIconHome
    }
}