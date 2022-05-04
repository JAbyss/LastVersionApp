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
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
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
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.disk.DiskCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.ChatsScreen
import com.foggyskies.petapp.presentation.ui.globalviews.FriendsScreen
import com.foggyskies.petapp.presentation.ui.globalviews.InternalNotificationScreen
import com.foggyskies.petapp.presentation.ui.globalviews.SearchUsersScreen
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.home.views.RightMenu
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.profile.human.MENUS
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.path.Path

@Composable
fun HomeMVIModel.HomeScreen(
    nav_controller: NavHostController? = null,
    msViewModel: MainSocketViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
//        getPosts()
        getContent()
//        getChats(msViewModel.chatDao!!, msViewModel)
    }


    val displayMetrics = LocalContext.current.resources.displayMetrics

    swipableMenu.listIcon = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_profile,
            offset = Offset(x = 10f, y = -70f)
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
            offset = Offset(x = -50f, y = -45f)
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_gamepad,
            offset = Offset(x = -70f, y = 10f),
        ),
    )

    swipableMenu.density = displayMetrics.density
    swipableMenu.sizeScreen =
        Size(
            width = displayMetrics.widthPixels.toFloat(),
            height = displayMetrics.heightPixels.toFloat()
        )
//    density = displayMetrics.density
    swipableMenu.navController = nav_controller!!
    SideEffect {
        Log.e("УТЕЧКА", "ИДЕТ УТЕЧКА")
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
                            if (swipableMenu.isReadyMenu) {
                                offset = it
                                swipableMenu.onDragStart(it)
                                swipableMenu.startOffsetCS =
                                    swipableMenu.offsetStartDp
                                swipableMenu.radius =
                                    swipableMenu.radiusMenu
                            }
                        },
                        onDragEnd = {
                            if (swipableMenu.isReadyMenu) {

                                val listDistance = swipableMenu.listOffsetGlobal.map {
                                    (it - offset).getDistance()
                                }
                                val minDistance = listDistance.minOrNull()

                                if (minDistance!! < swipableMenu.radiusCircle) {
                                    when (listDistance.indexOf(minDistance)) {
                                        0 -> {
                                            nav_controller?.navigate(NavTree.Profile.name)
                                        }
                                        1 -> {
                                            nav_controller?.navigate(NavTree.AdsHomeless.name)
                                        }
                                        2 -> {
                                            nav_controller?.navigate(NavTree.Home.name)
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
                                swipableMenu.selectedTarget = StateCS.IDLE
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (swipableMenu.isReadyMenu) {

                                offset = change.position

                                val listDistance = swipableMenu.listOffsetGlobal.map {
                                    (it - offset).getDistance()
                                }
                                val minDistance = listDistance.minOrNull()

                                if (minDistance!! < swipableMenu.radiusCircle) {
                                    swipableMenu.sizeCS =
                                        swipableMenu.radiusCircle
                                    swipableMenu.selectedTargetOffset =
                                        swipableMenu.listOffsetsForCircle[listDistance.indexOf(
                                            minDistance
                                        )]
                                    swipableMenu.selectedTarget = StateCS.SELECTED
                                } else {
                                    swipableMenu.selectedTarget = StateCS.IDLE
                                    swipableMenu.startOffsetCS =
                                        swipableMenu.offsetStartDp
                                    swipableMenu.sizeCS =
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
            visible = menuHelper.getMenuVisibleValue(MENUS.POST).value,
//                    isVisiblePhotoWindow,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Center)
                .testTag("Photos")
        ) {
            postScreenHandler.PostScreen(
                onLongPress = {
                    swipableMenu.isReadyMenu = false
                    menuHelper.changeVisibilityMenu(MENUS.POST)
//                    isVisiblePhotoWindow = false
                    photoScreenClosed()
                }
            )
        }
        AnimatedVisibility(
            visible = menuHelper.getMenuVisibleValue(MENUS.RIGHT).value,
//            isRightMenuOpen,
            exit = slideOutHorizontally(),
            modifier = Modifier
                .align(CenterEnd)
        ) {
            RightMenu(
                onClick = { itemMenu ->
                    when (itemMenu) {
                        "Пользователи" -> {
                            menuHelper.changeVisibilityMenu(MENUS.SEARCHUSERS, secondAction = {
                                menuHelper.setVisibilityMenu(MENUS.RIGHT, false)
                            })
//                            searchUsersSwitch()
                            msViewModel.connectToSearchUsers()
                        }
                        "Беседы" -> {
                            menuHelper.changeVisibilityMenu(MENUS.CHATS)
//                            chatsMenuSwitch()
//                            val a = FeedReaderDbHelper(context)
                            getChats(msViewModel)
//                            msViewModel.sendAction("getChats|")
                        }
                        "Друзья" -> {
                            menuHelper.changeVisibilityMenu(MENUS.FRIENDS)
//                            friendMenuSwitch()
                            msViewModel.sendAction("getFriends|")
                            msViewModel.sendAction("getRequestsFriends|")

                        }
                    }
                }
            )
        }
        if (swipableMenu.isTappedScreen)
            swipableMenu.CircularTouchMenu(param = swipableMenu)
        AnimatedVisibility(
            visible = menuHelper.getMenuVisibleValue(MENUS.CHATS).value,
//            isChatsMenuOpen,
            modifier = Modifier
                .align(Center)
        ) {
            ChatsScreen(nav_controller, this@HomeScreen, msViewModel)
        }
        AnimatedVisibility(
            visible = menuHelper.getMenuVisibleValue(MENUS.SEARCHUSERS).value,
//            isUsersMenuOpen,
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
//            isFriendsMenuOpen,
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


@OptIn(ExperimentalCoilApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@NonRestartableComposable
@Composable
fun PhotosFeed(viewModel: HomeMVIModel) {

    val context = LocalContext.current

    val state = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LazyColumn(
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
                        viewModel.menuHelper.setVisibilityMenu(MENUS.RIGHT, true)
//                        viewModel.isRightMenuOpen = true
                        viewModel.swipableMenu.isReadyMenu = false
//                        viewModel.isReadyMenu = false
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
                        viewModel.menuHelper.setVisibilityMenu(MENUS.RIGHT, false)

                        viewModel.swipableMenu.isReadyMenu = true

//                        viewModel.isRightMenuOpen = false
//                        viewModel.isReadyMenu = true
                    }
                }
            }
    ) {


        items(state.value.postsList.windowed(2, 2, true)) { item ->
//                Log.e("ЧТО не так", viewModel.listContents.size.toString())
            Row {
                if (item.isNotEmpty()) {
//                    var data = loader.diskCache?.get(item[0].item.address)?.data
//                    val request = if (data != null) {
//                        ImageRequest.Builder(context)
//                            .diskCacheKey(item[0].item.address)
//                            .build()
//                    } else {
                    var a = loader.diskCache?.get(item[0].item.address)?.data
                    val request = if (a != null) {
                        val b = a?.toFile()
                         ImageRequest.Builder(context)
                            .data(b)
//                            .data("http://$MAINENDPOINT/${item[0].item.address}")
                            .diskCachePolicy(CachePolicy.READ_ONLY)
                            .diskCacheKey(item[0].item.address)
                            .crossfade(true)
                            .build()
                    } else {
                        ImageRequest.Builder(context)
//                            .data(b)
                            .data("http://$MAINENDPOINT/${item[0].item.address}")
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(item[0].item.address)
                            .crossfade(true)
                            .build()
                    }
//                    }
                    loader.enqueue(request)


//                    var bb = loader.diskCache?.get(item[0].item.address)?.data
//                    loader.enqueue(bb)
//                    ImageRequest.Builder(LocalContext.current)
//                        .data("http://$MAINENDPOINT/${item[0].item.address}")
//                        .crossfade(true)
//                        .diskCachePolicy(CachePolicy.ENABLED)// it's the same even removing comments
//                        .build()
                    AsyncImage(
                        model = request,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable {
                                if (!viewModel.swipableMenu.isMenuOpen) {
//                                    viewModel.postScreenHandler.selectedPost = item[0]
                                    viewModel.viewModelScope.launch {

                                        viewModel.postScreenHandler.selectPost(
                                            item[0],
                                            action = {
                                                viewModel.swipableMenu.isReadyMenu = false

                                                viewModel.menuHelper.setVisibilityMenu(
                                                    MENUS.POST,
                                                    true
                                                )
//                                                viewModel.isVisiblePhotoWindow = true
                                            }
                                        )
//                                        viewModel.postScreenHandler.selectedPage = viewModel.listContents
                                    }
                                }
                            }
                            .weight(1f)
                    )
                }
                if (item.size > 1) {

//                    scope.launch {
//
//                        val loader = ImageLoader(context)
//                        val request = ImageRequest.Builder(context)
//                            .data("http://$MAINENDPOINT/${item[1].item.address}")
//                            .allowHardware(false) // Disable hardware bitmaps.
//                            .build()
//                        val result = request.context.imageLoader.execute(request)
//                        var _imageDrawable = result.drawable
//                        // Converting it to bitmap and using it to calculate the palette
//                        val bitmap = _imageDrawable?.toBitmap()
//
////                        val result = (loader.execute(request) as SuccessResult).drawable
////                        val bitmap = (result as BitmapDrawable).bitmap
//                        print("")
//                    }
                    var a = loader.diskCache?.get(item[0].item.address)?.data
                    val request = if (a != null) {
                        val b = a?.toFile()
                        ImageRequest.Builder(context)
                            .data(b)
//                            .data("http://$MAINENDPOINT/${item[0].item.address}")
                            .diskCachePolicy(CachePolicy.READ_ONLY)
                            .diskCacheKey(item[0].item.address)
                            .crossfade(true)
                            .build()
                    } else {
                        ImageRequest.Builder(context)
//                            .data(b)
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
//                                                viewModel.isVisiblePhotoWindow = true
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

