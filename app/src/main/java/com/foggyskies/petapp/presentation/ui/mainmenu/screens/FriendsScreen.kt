package com.foggyskies.petapp.presentation.ui.mainmenu.screens

import android.os.Bundle
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.get
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.mainmenu.MenuViewModel
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.acceptFriend
import com.foggyskies.petapp.presentation.ui.mainmenu.requests.createChat
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay

enum class StateFriendScreen {
    FRIENDS, REQUESTS
}

@Composable
fun OneItemFriend(
    item: UserIUSI,
    nav_controller: NavHostController?,
    createChat: (UserIUSI, (Bundle) -> Unit) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                nav_controller?.navigate(
                    nav_controller.graph[NavTree.Profile.name].id,
                    bundleOf(
                        "mode" to false,
                        "username" to item.username,
                        "image" to item.image,
                        "idUser" to item.id
                    )
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(1f)
                .align(Alignment.CenterStart)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.width(15.dp))
            if (item.image != "")
                AsyncImage(
                    model = "${Routes.SERVER.REQUESTS.BASE_URL}/${item.image}",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .clip(CircleShape)
                        .size(45.dp)
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
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text(
                    text = item.username,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (item.status != "") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = item.status,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            IconButton(
                onClick = {
                    createChat(item){
                        nav_controller?.navigate(
                            nav_controller.graph[NavTree.ChatSec.name].id,
                            it
                        )
                    }
//                    if (isNetworkAvailable.value)
//                    CoroutineScope(Dispatchers.IO).launch {
//
//                        HttpClient(Android) {
////                            install(JsonFeature) {
////                                serializer = KotlinxSerializer()
////                            }
//                            install(HttpTimeout) {
//                                requestTimeoutMillis = 3000
//                            }
//                        }.use {
//                            val idChat: HttpResponse = it.post("${Routes.SERVER.REQUESTS.BASE_URL}/createChat") {
//                                this.headers["Auth"] = TOKEN
//                                this.headers["Content-Type"] = "Application/Json"
//                                setBody(
//                                    CreateChat(
//                                    username = USERNAME,
//                                    idUserSecond = item.id
//                                )
//                                )
//                            }
//                            CoroutineScope(Dispatchers.Main).launch {
//                                val formattedChat = FormattedChatDC(
//                                    id = idChat.bodyAsText(),
//                                    nameChat = item.username,
//                                    idCompanion = item.id,
//                                    image = item.image
//                                )
//                                val string = Json.encodeToString(formattedChat)
//                                val b = bundleOf("itemChat" to string)
//                                nav_controller?.navigate(
//                                    nav_controller.graph[NavTree.ChatSec.name].id,
//                                    b
//                                )
//                            }
//                        }
//                    }
//                    else {
//                        //FIXME Надо доделать офлайн версию
////                        val string = Json.encodeToString(item)
////                        val b = bundleOf("itemChat" to string)
////                        nav_controller?.navigate(
////                            nav_controller.graph[NavTree.ChatSec.name].id,
////                            b
////                        )
//                    }
                },
//            shape = RoundedCornerShape(20.dp),
//            colors = ButtonDefaults.buttonColors(
//                backgroundColor = Color.White
//            ),
//            elevation = ButtonDefaults.elevation(
//                defaultElevation = 0.dp
//            ),
//            contentPadding = PaddingValues(0.dp),
                modifier = Modifier
//                    .align(Alignment.CenterEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                )
            }
        }
    }
}

@Composable
fun OneItemRequest(
    item: UserIUSI,
    viewModel: MenuViewModel
//    msViewModel: MainSocketViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(0.75f)
                .align(Alignment.CenterStart)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.width(15.dp))
            if (item.image != "")
                Image(
                    painter = rememberImagePainter(data = item.image),
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(CenterEnd)) {

            Button(
                onClick = {
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cancel),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    viewModel.acceptFriend(item.id)
//                    msViewModel.sendAction("acceptRequestFriend|${item.id}")
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}

@Composable
fun FriendsScreen(
    viewModel: MenuViewModel,
    nav_controller: NavHostController,
//    msViewModel: MainSocketViewModel
) {
    var stateFriendsScreen by remember { mutableStateOf(StateFriendScreen.FRIENDS) }

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
//                    viewModel.menuHelper.changeVisibilityMenu(MENUS.FRIENDS)
////                    viewModel.friendMenuSwitch()
//                }
//        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
//                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
        ) {
//
            AnimatedVisibility(
                visible = viewModel.listRequestsFriends.isNotEmpty(),
                modifier = Modifier.align(End)
            ) {
                Box(
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add_user),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(CircleShape)
                            .size(35.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (viewModel.listRequestsFriends.size != 0)
                                    stateFriendsScreen = when (stateFriendsScreen) {
                                        StateFriendScreen.FRIENDS -> StateFriendScreen.REQUESTS
                                        else -> StateFriendScreen.FRIENDS
                                    }
                            }
                    )
                    Box(
                        modifier = Modifier
                            .align(TopEnd)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .size(15.dp)
                    ) {

                        Text(
                            text = viewModel.listRequestsFriends.size.toString(),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Center)
                        )
                    }
                }
            }
//
            Spacer(modifier = Modifier.height(30.dp))
//
//            Box(
//                modifier = Modifier
//                    .padding(horizontal = 24.dp)
//                    .clip(RoundedCornerShape(20.dp))
//                    .fillMaxWidth(1f)
//                    .background(Color.White)
//            ) {

            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {

                itemsIndexed(viewModel.listFriends) { index, item ->
                    androidx.compose.animation.AnimatedVisibility(visible = stateFriendsScreen == StateFriendScreen.FRIENDS) {
                        AnimationLoad(index = index, delayItems = 100) {
                            Column() {
                                OneItemFriend(item, nav_controller, viewModel::createChat)
                                if (index != viewModel.listFriends.lastIndex)
                                    Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }

                itemsIndexed(viewModel.listRequestsFriends) { _, item ->
                    androidx.compose.animation.AnimatedVisibility(visible = stateFriendsScreen == StateFriendScreen.REQUESTS) {
                        OneItemRequest(item, viewModel)
                    }
                }
            }
        }
//            }
//            Spacer(modifier = Modifier.height(5.dp))
//        }
//    }
}

@Composable
fun AnimationLoad(
    delayStart: Long = 0,
    delayItems: Long = 250,
    index: Int = 0,
    enter: EnterTransition = slideInHorizontally(),
    exit: ExitTransition = slideOutHorizontally(),
    modifier: Modifier = Modifier,
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    var animationVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        delay(delayStart + (delayItems * index))
        animationVisible = true
    }

    AnimatedVisibility(
        visible = animationVisible,
        enter = enter,
        exit = exit,
        content = content,
        modifier = modifier
    )
}