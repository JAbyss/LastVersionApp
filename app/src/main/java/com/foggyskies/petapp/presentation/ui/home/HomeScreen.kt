package com.foggyskies.petapp.presentation.ui.home

import android.app.Activity
import android.content.Context
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
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
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.*
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.home.views.RightMenu
import kotlinx.coroutines.launch

@Composable
fun HomeViewModel.HomeScreen(
    nav_controller: NavHostController? = null,
    msViewModel: MainSocketViewModel
) {

    val context = LocalContext.current

    val displayMetrics = LocalContext.current.resources.displayMetrics

    swipableMenu.listIcon = listOf(
        ItemSwappableMenu(Image = R.drawable.ic_menu_profile),
        ItemSwappableMenu(Image = R.drawable.ic_menu_ads),
        ItemSwappableMenu(Image = R.drawable.ic_gamepad),
    )

    swipableMenu.density = displayMetrics.density
    swipableMenu.sizeScreen =
        Size(
            width = displayMetrics.widthPixels.toFloat(),
            height = displayMetrics.heightPixels.toFloat()
        )
    density = displayMetrics.density

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
                    when (itemMenu) {
                        "Пользователи" -> {
                            searchUsersSwitch()
                            msViewModel.connectToSearchUsers()
                        }
                        "Беседы" -> {
                            chatsMenuSwitch()
                            msViewModel.sendAction("getChats|")
                        }
                        "Друзья" -> {
                            friendMenuSwitch()
                            msViewModel.sendAction("getFriends|")
                            msViewModel.sendAction("getRequestsFriends|")

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
            ChatsScreen(nav_controller, this@HomeScreen, msViewModel)
        }
        AnimatedVisibility(
            visible = isUsersMenuOpen,
            modifier = Modifier
                .align(Center)
        ) {
            SearchUsersScreen(
                nav_controller = nav_controller,
                viewModel = this@HomeScreen,
                msViewModel
            )
        }
        AnimatedVisibility(
            visible = isFriendsMenuOpen,
            modifier = Modifier
                .align(Center)
        ) {
            FriendsScreen(
                nav_controller = nav_controller!!, viewModel = this@HomeScreen,
                msViewModel = msViewModel
            )
        }
        AnimatedVisibility(
            visible = msViewModel.isNotifyVisible,
            modifier = Modifier
                .align(TopCenter)
        ) {
            InternalNotificationScreen(
                this@HomeScreen,
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(0.9f)
                    .align(TopCenter),
                msViewModel,
                nav_controller!!
            )
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
                        )
                )
            }
            if (index == list.lastIndex)
                Spacer(modifier = Modifier.width(15.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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