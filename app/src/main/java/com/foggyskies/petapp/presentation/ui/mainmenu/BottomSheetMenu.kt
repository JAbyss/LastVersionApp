package com.foggyskies.petapp.presentation.ui.mainmenu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.SearchUsersScreen
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.chats
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.friends
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.requestFriends
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.ChatsScreen
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FriendsScreen
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import kotlinx.coroutines.launch

enum class Screens {
    FRIENDS, CHATS, SEARCH_USERS, IDLE
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColumnScope.BottomSheetMenu(
    viewModel: MenuViewModel = viewModel(modelClass = MenuViewModel::class.java),
    nav_controller: NavHostController
) {
    
    val selectedPage = remember {
        mutableStateOf(viewModel.selectedPage)
    }

    Column(
//        targetState = selectedPage.value,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .align(CenterHorizontally)
    ) {

        when (selectedPage.value) {
            Screens.FRIENDS -> FriendsScreen(
                viewModel = viewModel,
                nav_controller = nav_controller
            )
            Screens.CHATS -> ChatsScreen(
                viewModel = viewModel,
                nav_controller = nav_controller
            )
            Screens.SEARCH_USERS -> SearchUsersScreen(
                nav_controller = nav_controller,
                viewModel = viewModel
            )
            Screens.IDLE -> DashBoardMenu(
                viewModel,
                selectedPage
            )
        }

    }

    Spacer(modifier = Modifier.height(15.dp))
    AnimatedContent(
        targetState = selectedPage.value,
        modifier = Modifier.align(CenterHorizontally)
    ) { targetState ->
        if (targetState == Screens.IDLE)
            BottomNavigationMenu(nav_controller)
        else
            BottomMenu(viewModel, selectedPage)
    }
//    BottomNavigationMenu()
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun ColumnScope.DashBoardMenu(
    viewModel: MenuViewModel,
//    msViewModel: MainSocketViewModel,
    selectedScreens: MutableState<Screens>
) {

    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomEnd = 20.dp,
                    bottomStart = 20.dp
                )
            )
            .fillMaxWidth(1f)
            .align(CenterHorizontally)
            .background(Color.White)
    ) {

        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(10.dp))
            Divider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier
                    .clip(CircleShape)
                    .width(12.dp)
            )
            Text(
                text = "Окна",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Divider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth(0.9f)
            )
        }
        val listIcons = listOf(
            R.drawable.ic_friends,
            R.drawable.ic_chats,
            R.drawable.ic_search_menu
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
        ) {
            repeat(3) {
                IconButton(
                    onClick = {
                        when (it) {
                            0 -> {
                                viewModel.friends()
                                viewModel.requestFriends()
//                                    viewModel.repositoryUserDB.getFriends(msViewModel)
                                selectedScreens.value = Screens.FRIENDS
                                viewModel.selectedPage = Screens.FRIENDS
                            }
                            1 -> {
                                viewModel.viewModelScope.launch {
                                    viewModel.chats()
//                                    viewModel.repositoryUserDB.getChats(msViewModel)
                                }
                                selectedScreens.value = Screens.CHATS
                                viewModel.selectedPage = Screens.CHATS
                            }
                            2 -> {
                                //TODO Переделать
//                                msViewModel.connectToSearchUsers()
                                selectedScreens.value = Screens.SEARCH_USERS
                                viewModel.selectedPage = Screens.SEARCH_USERS
                            }

                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = listIcons[it]),
                        contentDescription = null,
                        modifier = Modifier
                            .size(33.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(10.dp))
            Divider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier
                    .clip(CircleShape)
                    .width(12.dp)
            )
            Text(
                text = "Режимы",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Divider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth(0.9f)
            )
        }
        val listIconsSecond = listOf(
            R.drawable.ic_shorts,
            R.drawable.ic_posts,
            R.drawable.ic_screen_chat
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally)
        ) {
            repeat(3) {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = listIconsSecond[it]),
                        contentDescription = null,
                        modifier = Modifier
                            .size(33.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ColumnScope.BottomNavigationMenu(
    nav_controller: NavHostController
) {

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth(0.85f)
            .align(CenterHorizontally)
            .background(Color.White)
    ) {
        IconButton(
            onClick = {

            },
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cloud),
                contentDescription = null,
                modifier = Modifier
                    .size(33.dp)
            )
        }
        IconButton(
            onClick = {
                nav_controller.navigate(NavTree.Profile.name)
            },
            modifier = Modifier.weight(7f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu_profile),
                contentDescription = null,
                modifier = Modifier
                    .size(33.dp)
            )
        }
        //TODO Кнопка облака
//        IconButton(
//            onClick = {
//                msViewModel.getCloudFiles()
//                msViewModel.isVisibleCloudMenu = true
//            },
//            modifier = Modifier.weight(1f)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_cloud),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(33.dp)
//            )
//        }
    }
}

@Composable
private fun ColumnScope.BottomMenu(viewModel: MenuViewModel, selectedPage: MutableState<Screens>) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth(0.85f)
            .align(CenterHorizontally)
            .background(Color.Transparent)
    ) {
        IconButton(
            onClick = {
                selectedPage.value = Screens.IDLE
                viewModel.selectedPage = Screens.IDLE
            },
            modifier = Modifier
                .padding(end = 20.dp)
                .align(CenterEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_menu_back),
                contentDescription = null,
                modifier = Modifier
                    .size(33.dp)
            )
        }
    }
}