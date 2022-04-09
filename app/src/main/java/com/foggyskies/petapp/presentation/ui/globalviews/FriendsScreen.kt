package com.foggyskies.petapp.presentation.ui.globalviews

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
import androidx.navigation.NavHostController
import androidx.navigation.get
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class StateFriendScreen {
    FRIENDS, REQUESTS
}

@Composable
fun OneItemFriend(item: UserIUSI, nav_controller: NavHostController?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
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
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {

                    HttpClient(Android) {
                        install(JsonFeature) {
                            serializer = KotlinxSerializer()
                        }
                        install(HttpTimeout) {
                            requestTimeoutMillis = 3000
                        }
                    }.use {
                        val idChat = it.post<String>("http://$MAINENDPOINT/createChat") {
                            this.headers["Auth"] = TOKEN
                            this.headers["Content-Type"] = "Application/Json"
                            this.body = CreateChat(
                                username = USERNAME,
                                idUserSecond = item.id
                            )
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            val formattedChat = FormattedChatDC(
                                id = idChat,
                                nameChat = item.username,
                                idCompanion = item.id,
                                image = item.image
                            )
                            var str = Json.encodeToString(formattedChat)
//                            val Bundle_1 = Bundle()
//                            Bundle_1.putParcelable("itemChat", formattedChat)
                            nav_controller?.navigate("Chat/$str")
                        }
                    }
                }
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
                .align(Alignment.CenterEnd)
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

@Composable
fun OneItemRequest(
    item: UserIUSI,
    nav_controller: NavHostController?,
    viewModel: HomeViewModel,
    msViewModel: MainSocketViewModel
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
                    msViewModel.sendAction("acceptRequestFriend|${item.id}")
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
    viewModel: HomeViewModel,
    nav_controller: NavHostController,
    msViewModel: MainSocketViewModel
) {
    var stateFriendsScreen by remember { mutableStateOf(StateFriendScreen.FRIENDS) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.friendMenuSwitch()
                }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
        ) {

            AnimatedVisibility(
                visible = msViewModel.listRequests.isNotEmpty(),
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
                                if (msViewModel.listRequests.size != 0)
                                    when (stateFriendsScreen) {
                                        StateFriendScreen.FRIENDS -> stateFriendsScreen =
                                            StateFriendScreen.REQUESTS
                                        else -> stateFriendsScreen = StateFriendScreen.FRIENDS
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
                            text = msViewModel.listRequests.size.toString(),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth(1f)
                    .background(Color.White)
            ) {

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {

                    itemsIndexed(msViewModel.listFriends) { index, item ->
                        androidx.compose.animation.AnimatedVisibility(visible = stateFriendsScreen == StateFriendScreen.FRIENDS) {
                            AnimationLoad(index = index, delayItems = 100) {
                                Column() {
                                    OneItemFriend(item, nav_controller)
                                    if (index != msViewModel.listFriends.lastIndex)
                                        Spacer(modifier = Modifier.height(5.dp))
                                }
                            }
                        }
                    }

                    itemsIndexed(msViewModel.listRequests) { _, item ->
                        androidx.compose.animation.AnimatedVisibility(visible = stateFriendsScreen == StateFriendScreen.REQUESTS) {
                            OneItemRequest(item, nav_controller, viewModel, msViewModel)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
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