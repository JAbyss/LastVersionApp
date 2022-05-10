package com.foggyskies.petapp.presentation.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import android.util.Log
import android.view.WindowMetrics
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.ChatsScreen
import com.foggyskies.petapp.presentation.ui.globalviews.FriendsScreen
import com.foggyskies.petapp.presentation.ui.globalviews.InternalNotificationScreen
import com.foggyskies.petapp.presentation.ui.globalviews.SearchUsersScreen
import com.foggyskies.petapp.presentation.ui.home.views.RightMenu
import com.foggyskies.petapp.presentation.ui.profile.human.MENUS
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@Composable
fun HomeMVIModel.HomeScreen(
    nav_controller: NavHostController? = null,
    msViewModel: MainSocketViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = isNetworkAvailable.value) {
        checkInternet(this@HomeScreen::getContent)
        if (msViewModel.mainSocket == null)
            checkInternet(msViewModel::createMainSocket)
//        if (isNetworkAvailable.value) {
//            if (msViewModel.mainSocket == null)
//                msViewModel.createMainSocket()
////            getContent()
//        }
    }
    val displayMetrics = LocalContext.current.resources.displayMetrics

    LaunchedEffect(key1 = Unit ){


        swipableMenu.density = displayMetrics.density
        swipableMenu.sizeScreen =
            Size(
                width = displayMetrics.widthPixels.toFloat(),
                height = displayMetrics.heightPixels.toFloat()
            )
        swipableMenu.navController = nav_controller!!
    }


    SideEffect {
        Log.e("УТЕЧКА", "ИДЕТ УТЕЧКА")
    }

    Box(
        modifier =
        swipableMenu.Modifier(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFFCEEEC))
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
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
                            menuHelper.setVisibilityMenu(MENUS.RIGHT, true)
                            swipableMenu.isReadyMenu = false
                        } else if (dragAmount >= 50) {

                            if (canVibrate == true) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // API 26
                                    vibrator.vibrate(
                                        createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                                    )

                                } else {
                                    vibrator.vibrate(100)
                                }
                            }
                            menuHelper.setVisibilityMenu(MENUS.RIGHT, false)

                            swipableMenu.isReadyMenu = true
                        }
                    }
                }
        ) {
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
            visible = menuHelper.getMenuVisibleValue(MENUS.POST).value,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Center)
        ) {
            postScreenHandler.PostScreen(
                onLongPress = {
                    swipableMenu.isReadyMenu = false
                    menuHelper.changeVisibilityMenu(MENUS.POST)
                    photoScreenClosed()
                }
            )
        }
        AnimatedVisibility(
            visible = menuHelper.getMenuVisibleValue(MENUS.RIGHT).value,
            exit = slideOutHorizontally(),
            modifier = Modifier
                .align(CenterEnd)
        ) {
            RightMenu(
                onClick = { itemMenu ->
                    onSelectRightMenu(itemMenu, msViewModel)
                }
            )
        }
        if (swipableMenu.isTappedScreen)
            swipableMenu.CircularTouchMenu(param = swipableMenu)
        AnimatedVisibility(
            visible = menuHelper.getMenuVisibleValue(MENUS.CHATS).value,
            modifier = Modifier
                .align(Center)
        ) {
            ChatsScreen(nav_controller, this@HomeScreen, msViewModel)
        }
        AnimatedVisibility(
            visible = menuHelper.getMenuVisibleValue(MENUS.SEARCHUSERS).value,
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
            visible = menuHelper.getMenuVisibleValue(MENUS.FRIENDS).value,
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

    val list = listOf(
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


@OptIn(ExperimentalCoilApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@NonRestartableComposable
@Composable
fun PhotosFeed(viewModel: HomeMVIModel) {

    val context = LocalContext.current

    val state = viewModel.state.collectAsState()

    LazyColumn() {

        items(state.value.postsList.windowed(2, 2, true)) { item ->

            Row {
                if (item.isNotEmpty()) {

                    val cached = loader.diskCache?.get(item[0].item.address)?.data
                    val request = if (cached != null) {
                        val b = cached?.toFile()
                        ImageRequest.Builder(context)
                            .data(b)
                            .diskCachePolicy(CachePolicy.READ_ONLY)
                            .diskCacheKey(item[0].item.address)
                            .crossfade(true)
                            .build()
                    } else {
                        ImageRequest.Builder(context)
                            .data("http://$MAINENDPOINT/${item[0].item.address}")
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(item[0].item.address)
                            .crossfade(true)
                            .build()
                    }
                    loader.enqueue(request)

                    AsyncImage(
                        model = request,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable {
                                if (!viewModel.swipableMenu.isMenuOpen) {
                                    viewModel.viewModelScope.launch {

                                        viewModel.postScreenHandler.selectPost(
                                            item[0],
                                            action = {
                                                viewModel.swipableMenu.isReadyMenu = false

                                                viewModel.menuHelper.setVisibilityMenu(
                                                    MENUS.POST,
                                                    true
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            .weight(1f)
                    )
                }
                if (item.size > 1) {

                    val cached = loader.diskCache?.get(item[0].item.address)?.data
                    val request = if (cached != null) {
                        val b = cached?.toFile()
                        ImageRequest.Builder(context)
                            .data(b)
                            .diskCachePolicy(CachePolicy.READ_ONLY)
                            .diskCacheKey(item[0].item.address)
                            .crossfade(true)
                            .build()
                    } else {
                        ImageRequest.Builder(context)
                            .data("http://$MAINENDPOINT/${item[1].item.address}")
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(item[0].item.address)
                            .crossfade(true)

                            .build()
                    }
//                    }
                    loader.enqueue(request)
                    AsyncImage(
                        model = request,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable {
                                if (!viewModel.swipableMenu.isMenuOpen) {
                                    viewModel.viewModelScope.launch {

                                        viewModel.postScreenHandler.selectPost(
                                            item[1],
                                            action = {
                                                viewModel.swipableMenu.isReadyMenu = false
                                                viewModel.menuHelper.setVisibilityMenu(
                                                    MENUS.POST,
                                                    true
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            .weight(1f)
                    )
                }
            }
        }
    }
}
