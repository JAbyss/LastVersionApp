package com.foggyskies.petapp.presentation.ui.home

//import com.foggyskies.petapp.presentation.ui.home.animations.ShowCaseView

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import android.util.Log
import android.view.WindowMetrics
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.foggyskies.petapp.PushNotificationService
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.ChatsScreen
import com.foggyskies.petapp.presentation.ui.globalviews.CircularTouchMenu
import com.foggyskies.petapp.presentation.ui.globalviews.FriendsScreen
import com.foggyskies.petapp.presentation.ui.globalviews.SearchUsersScreen
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwipableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.home.views.RightMenu
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun HomeViewModel.HomeScreen(nav_controller: NavHostController? = null) {

    val context = LocalContext.current

    val displayMetrics = LocalContext.current.resources.displayMetrics


    swipableMenu.listIcon = listOf(
        ItemSwipableMenu(Image = R.drawable.ic_menu_profile),
        ItemSwipableMenu(Image = R.drawable.ic_menu_ads),
        ItemSwipableMenu(Image = R.drawable.ic_gamepad),
    )

    swipableMenu.density = displayMetrics.density
    swipableMenu.sizeScreen =
        Size(width = displayMetrics.widthPixels.toFloat(), height = displayMetrics.heightPixels.toFloat())
    density = displayMetrics.density

    LaunchedEffect(key1 = Unit){
        createMainSocket()
    }

    Box(
        modifier =
        if (swipableMenu.isReadyMenu) {

            Modifier
                .fillMaxSize()
                .background(Color(0xFFFCEEEC))
                .pointerInput(Unit) {
                    var offset = Offset.Zero

                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            if (isReadyMenu) {
                                offset = it
                                swipableMenu.onDragStart(it)
                                circularSelector.offset =
                                    swipableMenu.offsetStartDp
                                circularSelector.radius =
                                    swipableMenu.radiusMenu
                            }
                        },
                        onDragEnd = {
                            if (isReadyMenu) {

                                val listDistance = swipableMenu.listOffsetGlobal.map {
                                    (it - offset).getDistance()
                                }
                                val minDistance = listDistance.minOrNull()

                                if (minDistance!! < swipableMenu.radiusCircle) {
                                    when (listDistance.indexOf(minDistance)) {
                                        0 -> {
                                            nav_controller?.navigate("Profile")
                                        }
                                        1 -> {
                                            nav_controller?.navigate("AdsHomeless")
                                        }
                                        2 -> {
                                            nav_controller?.navigate("Chat")
                                        }
                                    }
                                    Toast
                                        .makeText(
                                            context,
                                            "SELECTED ${listDistance.indexOf(minDistance)}",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    Log.e(
                                        "SELECTOR",
                                        "SELECTED ${listDistance.indexOf(minDistance)}"
                                    )
                                }
                                swipableMenu.isTappedScreen = false
                                viewModelScope.launch {
                                    swipableMenu.menuClosing()
                                }
                                circularSelector.selectedTarget = StateCS.IDLE
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (isReadyMenu) {

                                offset = change.position

                                val listDistance = swipableMenu.listOffsetGlobal.map {
                                    (it - offset).getDistance()
                                }
                                val minDistance = listDistance.minOrNull()

                                if (minDistance!! < swipableMenu.radiusCircle) {
                                    circularSelector.size =
                                        swipableMenu.radiusCircle
                                    circularSelector.selectedTargetOffset =
                                        swipableMenu.listOffsetsForCircle[listDistance.indexOf(
                                            minDistance
                                        )]
                                    circularSelector.selectedTarget = StateCS.SELECTED
                                } else {
                                    circularSelector.selectedTarget = StateCS.IDLE
                                    circularSelector.offset =
                                        swipableMenu.offsetStartDp
                                    circularSelector.size =
                                        swipableMenu.radiusMenu
                                }
                            }
                        }
                    )
                }
        } else {
            Modifier
                .fillMaxSize()
                .background(Color(0xFFFCEEEC))
        }
    ) {

        Column {
            Spacer(modifier = Modifier.height(20.dp))
            StoriesCompose(
                Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(35.dp))
            PhotosFeed(this@HomeScreen)
        }
        AnimatedVisibility(
            visible = isVisiblePhotoWindow,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Center)
                .testTag("Photos")

        ) {
            PhotoScreen(this@HomeScreen)
        }
        AnimatedVisibility(
            visible = isRightMenuOpen,
            exit = slideOutHorizontally(),
            modifier = Modifier
                .align(CenterEnd)
        ) {
            RightMenu(
                onClick = { itemMenu ->
                    when(itemMenu){
                        "Пользователи" -> {
                            searchUsersSwitch()
                            connectToSearchUsers()
                        }
                        "Беседы" -> {
                            chatsMenuSwitch()
                            sendAction("getChats|")
                        }
                        "Друзья" -> {
                            friendMenuSwitch()
                            sendAction("getFriends|")
                            sendAction("getRequestsFriends|")

                        }
                    }
                }
            )
        }
        if (swipableMenu.isTappedScreen)
            CircularTouchMenu(param = swipableMenu, circularSelector)
        AnimatedVisibility(
            visible = isChatsMenuOpen,
            modifier = Modifier
                .align(Center)
        ) {
            ChatsScreen(nav_controller, this@HomeScreen)
        }
//        ChatsScreen(nav_controller)
        AnimatedVisibility(
            visible = isUsersMenuOpen,
            modifier = Modifier
                .align(Center)
        ) {
            SearchUsersScreen(nav_controller = nav_controller, viewModel = this@HomeScreen)
        }
        AnimatedVisibility(
            visible = isFriendsMenuOpen,
            modifier = Modifier
                .align(Center)
        ) {
            FriendsScreen(nav_controller = nav_controller!!, viewModel = this@HomeScreen)
        }
    }
}

@Composable
fun StoriesCompose(modifier: Modifier) {

    var list = listOf(
        "123",
        "321",
        "123",
        "123",
        "321",
        "123",
        "123",
        "321",
        "123",
    )

    val display_metrics = LocalContext.current.resources.displayMetrics
    val context = LocalContext.current


    val width: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics =
                (context as Activity).windowManager.currentWindowMetrics
//            val insets: Insets = windowMetrics.windowInsets
//                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width()
        } else {
            display_metrics.widthPixels
        }

    val width_config = (width / display_metrics.density).toInt()

    LazyRow(
        modifier = modifier
    ) {

        itemsIndexed(list) { index, item ->

            Spacer(modifier = Modifier.width(15.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFAFDED9))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image_dog),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .size(
                            ((width_config - 15 * 6) / 4).dp
//                            55.dp
                        )
                )
            }
            if (index == list.lastIndex)
                Spacer(modifier = Modifier.width(15.dp))
        }
    }
}


@ExperimentalFoundationApi
@Composable
fun PhotosFeed(viewModel: HomeViewModel) {

    val test_list = listOf(
        "123",
        "321",
        "123",
        "123",
        "321",
        "123",
        "123",
        "321",
        "123",
        "123",
        "321",
        "123",
    )

    val context = LocalContext.current

    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        modifier = Modifier
//            .padding(bottom = 60.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    var newX = change.position.x
                    var oldX = change.previousPosition.x
                    var newY = change.position.y
                    var oldY = change.previousPosition.y
                    val vibrator =
                        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                    val canVibrate = vibrator?.hasVibrator()
                    if (dragAmount <= -50 && newY - oldY <= 10) {
//                        viewModel.isVisiblePhotoWindow = true

                        if (canVibrate == true) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // API 26
                                vibrator.vibrate(
                                    createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                                )

                            } else {
                                // This method was deprecated in API level 26
                                vibrator.vibrate(100)
                            }
                        }
                        viewModel.isRightMenuOpen = true
                        viewModel.isReadyMenu = false
                    } else if (dragAmount >= 50) {

                        if (canVibrate == true) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // API 26
                                vibrator.vibrate(
                                    createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                                )

                            } else {
                                // This method was deprecated in API level 26
                                vibrator.vibrate(100)
                            }
                        }
                        viewModel.isRightMenuOpen = false
                        viewModel.isReadyMenu = true
                    }
                }
            }
    ) {

        itemsIndexed(test_list) { index, item ->

            Image(
                painter = painterResource(id = R.drawable.image_dog),
                contentDescription = null,
                modifier = Modifier
                    .padding(7.dp)
                    .clickable {
                        if (!viewModel.swipableMenu.isMenuOpen) {
                            viewModel.swipableMenu.isReadyMenu = false
                            viewModel.isVisiblePhotoWindow = true
                        }
                    }
                    .testTag("clickBTN$index")
            )

        }
    }
}

@Composable
fun BottomNavigation(
    map_item: Map<String, Int>,
    onClick: (String) -> Unit,
    modifier: Modifier,
    isTextVisible: Boolean = true
) {

    var nowSelected by remember {
        mutableStateOf("Главная")
    }

    Box(
        modifier = modifier
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(60.dp)
                .background(Color(0xFF47456D))
                .align(BottomCenter)
        ) {

            map_item.forEachKeys { key, value, _ ->
                Button(
                    onClick = {
                        onClick(key)
                        nowSelected = key
                    },
                    shape = CircleShape,
                    contentPadding = PaddingValues(vertical = 3.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (nowSelected == key) Color(0xFFB27F8F) else Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    ),
                    modifier = Modifier
                        .offset(y = if (nowSelected == key) (-20).dp else 0.dp)
                        .weight(1f)
                        .height(60.dp)
                        .align(Bottom)
                ) {
                    Column(
                        horizontalAlignment = CenterHorizontally,
                    ) {
                        Icon(
                            painter = painterResource(id = value),
                            contentDescription = null,
                            modifier = Modifier,
                            Color(0xFFBDBDBD)

                        )
                        if (isTextVisible)
                            Text(
                                text = key,
                                color = Color(0xFF828282)
                            )
                    }
                }
            }
        }
    }

}

