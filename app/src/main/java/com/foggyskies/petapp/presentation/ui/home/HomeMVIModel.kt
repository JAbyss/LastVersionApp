package com.foggyskies.petapp.presentation.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.R
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.presentation.ui.home.widgets.post.SelectedPostWithIdPageProfile
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class HomeMVIModel :
    ViewModel() {

    val listPosts = mutableStateListOf<SelectedPostWithIdPageProfile>()

    // ЛИСТЫ ДЛЯ ЗАПРОСОВ

    var listFriends = mutableStateListOf<UserIUSI>()
    var listChats = mutableStateListOf<FormattedChatDC>()
    var listRequestsFriends = mutableStateListOf<UserIUSI>()

    val swipableMenu = SwappableMenu()

    val listIconHome = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_profile,
            position = SwappableMenu.PositionsIcons.TOP,
            onValueSelected = {
                it.navigate(NavTree.Profile.name)
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
            position = SwappableMenu.PositionsIcons.TOP_LEFT,
            onValueSelected = {
                it.navigate(NavTree.AdsHomeless.name)
            }
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_gamepad,
            position = SwappableMenu.PositionsIcons.LEFT,
            onValueSelected = {
                it.navigate(NavTree.Home.name)
            })
    )

    init {
        swipableMenu.listIcon = listIconHome
    }

    val repositoryUserDB: RepositoryUserDB by inject(RepositoryUserDB::class.java)

    val menuHelper = MenuVisibilityHelper(action = { swipableMenu.isReadyMenu = it })

//    var postScreenHandler = PostScreenHandler()

    fun photoScreenClosed() {
        viewModelScope.launch {
            delay(400)
            swipableMenu.isReadyMenu = true
        }
    }
}