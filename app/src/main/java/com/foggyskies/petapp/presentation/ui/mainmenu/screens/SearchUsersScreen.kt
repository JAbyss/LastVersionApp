package com.foggyskies.petapp.presentation.ui.globalviews

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.globalmodel.AnimatedVisibleDC
import com.foggyskies.petapp.presentation.ui.home.HomeMVIModel
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.addFriend
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.AnimationLoad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UsersSearch(
    var id: String,
    var username: String,
    var image: String,
    var status: String,
    var isFriend: Boolean,
    var awaitAccept: Boolean
)

@kotlinx.serialization.Serializable
data class CreateChat(
    var username: String,
    var idUserSecond: String
)

@Composable
fun OneItemUser(
    viewModel: MenuViewModel,
    item: UsersSearch
) {

    val recomposition = currentRecomposeScope

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.align(CenterStart)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth(
                        if (item.isFriend) 1f else
                            0.75f
                    )
                    .clickable {
                    }
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.width(15.dp))
                if (item.image != "")
                    AsyncImage(
                        model = item.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .clip(CircleShape)
                            .size(70.dp)
                    )
                else
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(vertical = 7.dp)
                            .clip(CircleShape)
                            .size(45.dp)
                            .background(Color(0xFFC4E9FB))
                    ) {
                        Text(
                            text = item.username[0].toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF20B6F6)
                        )
                    }

                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (item.status != "") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = item.status, maxLines = 1, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterVertically)
            ) {
                if (!item.isFriend && !item.awaitAccept)
                    AnimationLoad(delayStart = 300, modifier = Modifier.align(Center)) {

                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.addFriend(item.id)
//                                    viewModel.sendAction("addFriend|${item.id}")
                                    item.awaitAccept = true
                                    recomposition.invalidate()
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_add_user),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(35.dp)
                            )
                        }
                    }
                else if (item.awaitAccept) {
                    AnimationLoad(
                        delayStart = 300, modifier = Modifier.align(Center)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_clock),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun SearchUsersScreen(
    nav_controller: NavHostController?,
    viewModel: MenuViewModel
) {

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                ) {
//                    msViewModel.disconnect()
//                    viewModel.menuHelper.changeVisibilityMenu(MENUS.SEARCHUSERS)
////                    viewModel.searchUsersSwitch()
//                }
//        )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.align(Alignment.Center)
    ) {

//        val listUsers = msViewModel.users

        var localListUsers by remember {
            mutableStateOf(mutableListOf<AnimatedVisibleDC<UsersSearch>>())
        }

        val previos = remember { mutableListOf<Int>(0, 0) }

        val scope = rememberCoroutineScope()

//        remember(listUsers.value.users.size) {
//            scope.launch {
//                try {
//                    previos[0] = previos[1]
//                    previos[1] = listUsers.value.users.size
//
//                    if (previos[0] != 0 && previos[1] == 0) {
//                        localListUsers.forEach {
//                            it.isVisible.value = false
//                        }
//                        localListUsers = mutableListOf()
//                    } else if (previos[0] > previos[1]) {
//                        val newList = mutableListOf<AnimatedVisibleDC<UsersSearch>>()
//                        var listForRemove = mutableListOf<AnimatedVisibleDC<UsersSearch>>()
//                        localListUsers.forEachIndexed { index, item ->
//                            if (!listUsers.value.users.contains(item.item)) {
//                                item.isVisible.value = false
//                                delay(50)
//                                listForRemove.add(item)
//                            } else {
//                                newList.add(item)
//                            }
//                        }
//                        localListUsers.removeAll(listForRemove)
//                        localListUsers = newList
//                    } else if (previos[0] != 0 && previos[0] < previos[1]) {
//                        val listForSave = mutableListOf<UsersSearch>()
//                        val listForSave_2 = mutableListOf<AnimatedVisibleDC<UsersSearch>>()
//                        val listForRemove = mutableListOf<AnimatedVisibleDC<UsersSearch>>()
//                        localListUsers.forEachIndexed { index, item ->
//                            if (listUsers.value.users.contains(item.item)) {
//                                listForSave.add(item.item)
//                                listForSave_2.add(item)
//                            } else {
//                                item.isVisible.value = false
//                                delay(50)
//                                listForRemove.add(item)
//                            }
//                        }
//                        if (listForRemove.isNotEmpty())
//                            localListUsers.removeAll(listForRemove)
//                        val newList = mutableListOf<AnimatedVisibleDC<UsersSearch>>()
//                        listUsers.value.users.forEach { item ->
//                            if (!listForSave.contains(item)) {
//                                val newItem = AnimatedVisibleDC<UsersSearch>(
//                                    item = item,
//                                    isVisible = mutableStateOf(false)
//                                )
//                                newList.add(newItem)
//                            }
//                        }
//                        listForSave_2.forEach {
//                            newList.add(it)
//                        }
//                        localListUsers = newList
//                    } else {
//                        val newList = mutableListOf<AnimatedVisibleDC<UsersSearch>>()
//                        listUsers.value.users.forEach { item_1 ->
//                            val newItem = AnimatedVisibleDC<UsersSearch>(
//                                item = item_1,
//                                isVisible = mutableStateOf(false)
//                            )
//                            newList.add(newItem)
//                        }
//                        localListUsers = newList
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//            mutableStateOf(0)
//        }

        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
            itemsIndexed(viewModel.listFoundedUsers) { index, item ->

                Column() {
                    OneItemUser(viewModel, item)
                    if (index != localListUsers.lastIndex)
                        Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))

        SearchBar(viewModel)
    }
}

// TODO ТУТ нужно жестко разобраться