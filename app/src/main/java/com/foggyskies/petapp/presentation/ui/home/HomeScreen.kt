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
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.*
import com.foggyskies.petapp.presentation.ui.home.views.RightMenu
import com.foggyskies.petapp.presentation.ui.profile.human.MENUS
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
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
    }
    val displayMetrics = LocalContext.current.resources.displayMetrics

    LaunchedEffect(key1 = Unit) {
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

    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    if (swipableMenu.modalBottomSheetState == null)
        swipableMenu.modalBottomSheetState = modalBottomSheetState

    val scope = rememberCoroutineScope()
//    val bottomSheetState = rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(initialValue = BottomSheetValue.Expanded))
//    BottomSheetScaffold(sheetContent = ) {
//
//    }

    ModalBottomSheetLayout(
//        sheetShape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp),
//        scaffoldState = bottomSheetState,
        sheetState = modalBottomSheetState,
        sheetContentColor = Color.Transparent,
        sheetBackgroundColor = Color.Transparent,
        scrimColor = Color.Transparent,
        sheetElevation = 0.dp,
        sheetContent = {
            BottomSheetMenu(
                this@HomeScreen,
                msViewModel,
                menuHelper,
                nav_controller!!
            )
        }
    ) {

        Box(
            modifier =
            swipableMenu.Modifier(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5FFFA)),
                callback = {
                    scope.launch {
                        modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
//                .background(Color(0xFFFCEEEC))
            )
        ) {

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
//                .fillMaxWidth()
//                .height(300.dp)
//                .border(2.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
                    .fillMaxSize()
//                .fillMaxWidth(0.9f)
//                .fillMaxHeight()
                    .align(Center)
//                    .pointerInput(Unit) {
//                        detectHorizontalDragGestures { change, dragAmount ->
//                            var newY = change.position.y
//                            var oldY = change.previousPosition.y
//                            val vibrator =
//                                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
//                            val canVibrate = vibrator?.hasVibrator()
//                            if (dragAmount <= -50 && newY - oldY <= 10) {
//                                if (canVibrate == true) {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        // API 26
//                                        vibrator.vibrate(
//                                            createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
//                                        )
//
//                                    } else {
//                                        // This method was deprecated in API level 26
//                                        vibrator.vibrate(100)
//                                    }
//                                }
//                                menuHelper.setVisibilityMenu(MENUS.RIGHT, true)
//                                swipableMenu.isReadyMenu = false
//                            } else if (dragAmount >= 50) {
//
//                                if (canVibrate == true) {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        // API 26
//                                        vibrator.vibrate(
//                                            createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
//                                        )
//
//                                    } else {
//                                        vibrator.vibrate(100)
//                                    }
//                                }
//                                menuHelper.setVisibilityMenu(MENUS.RIGHT, false)
//
//                                swipableMenu.isReadyMenu = true
//                            }
//                        }
//                    }
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                itemsIndexed(state.value.postsList) { index, item ->

                    val postScreenHandler = PostScreenHandler()

                    postScreenHandler.selectPost(
                        item,
                        action = {
                        }
                    )

                    postScreenHandler.PostScreen(onLongPress = {})
                    if (index != state.value.postsList.lastIndex) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Divider(
                            color = Color.LightGray,
                            thickness = 2.dp,
                            modifier = Modifier.fillMaxWidth(0.75f)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
//                PhotosFeed(this@HomeScreen)
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
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
fun ColumnScope.PhotosFeed(viewModel: HomeMVIModel) {

    val context = LocalContext.current

    val state = viewModel.state.collectAsState()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(2.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
            .align(CenterHorizontally)
            .fillMaxWidth(0.9f)
    ) {

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(state.value.postsList.windowed(1, 1, true)) { item ->

            var a = PostScreenHandler()

            a.selectPost(
                item[0],
                action = {
                }
            )

            a.PostScreen(onLongPress = {})
            Spacer(modifier = Modifier.height(20.dp))

//            Row {
//                if (item.isNotEmpty()) {
//
//                    val cached = loader.diskCache?.get(item[0].item.address)?.data
//                    val request = if (cached != null) {
//                        val b = cached?.toFile()
//                        ImageRequest.Builder(context)
//                            .data(b)
//                            .diskCachePolicy(CachePolicy.READ_ONLY)
//                            .diskCacheKey(item[0].item.address)
//                            .crossfade(true)
//                            .build()
//                    } else {
//                        ImageRequest.Builder(context)
//                            .data("http://$MAINENDPOINT/${item[0].item.address}")
//                            .diskCachePolicy(CachePolicy.ENABLED)
//                            .diskCacheKey(item[0].item.address)
//                            .crossfade(true)
//                            .build()
//                    }
//                    loader.enqueue(request)
//
//                    AsyncImage(
//                        model = request,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(7.dp)
//                            .clickable {
//                                if (!viewModel.swipableMenu.isMenuOpen) {
//                                    viewModel.viewModelScope.launch {
//
//                                        viewModel.postScreenHandler.selectPost(
//                                            item[0],
//                                            action = {
//                                                viewModel.swipableMenu.isReadyMenu = false
//
//                                                viewModel.menuHelper.setVisibilityMenu(
//                                                    MENUS.POST,
//                                                    true
//                                                )
//                                            }
//                                        )
//                                    }
//                                }
//                            }
//                            .weight(1f)
//                    )
//                }
//                if (item.size > 1) {
//
//                    val cached = loader.diskCache?.get(item[1].item.address)?.data
//                    val request = if (cached != null) {
//                        val b = cached?.toFile()
//                        ImageRequest.Builder(context)
//                            .data(b)
//                            .diskCachePolicy(CachePolicy.READ_ONLY)
//                            .diskCacheKey(item[1].item.address)
//                            .crossfade(true)
//                            .build()
//                    } else {
//                        ImageRequest.Builder(context)
//                            .data("http://$MAINENDPOINT/${item[1].item.address}")
//                            .diskCachePolicy(CachePolicy.ENABLED)
//                            .diskCacheKey(item[1].item.address)
//                            .crossfade(true)
//
//                            .build()
//                    }
////                    }
//                    loader.enqueue(request)
//                    AsyncImage(
//                        model = request,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(7.dp)
//                            .clickable {
//                                if (!viewModel.swipableMenu.isMenuOpen) {
//                                    viewModel.viewModelScope.launch {
//
//                                        viewModel.postScreenHandler.selectPost(
//                                            item[1],
//                                            action = {
//                                                viewModel.swipableMenu.isReadyMenu = false
//                                                viewModel.menuHelper.setVisibilityMenu(
//                                                    MENUS.POST,
//                                                    true
//                                                )
//                                            }
//                                        )
//                                    }
//                                }
//                            }
//                            .weight(1f)
//                    )
//                }
//            }
        }
    }
}
