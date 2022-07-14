package com.foggyskies.petapp.presentation.ui.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.presentation.ui.globalviews.*
import com.foggyskies.petapp.temppackage.CloudScreen
import com.foggyskies.petapp.temppackage.CloudViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeMVIModel.HomeScreen(
    nav_controller: NavHostController? = null,
    msViewModel: MainSocketViewModel
) {
    BackHandler(enabled = msViewModel.isVisibleCloudMenu) {
        if (msViewModel.isVisibleCloudMenu){
            msViewModel.isVisibleCloudMenu = false
        }
    }

    LaunchedEffect(key1 = Unit) {
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
    val stateUi = this.state.collectAsState()
    ModalBottomSheetLayout(
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
            )
        ) {
            val isScrollable = remember{
                mutableStateOf(true)
            }
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = isScrollable.value,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Center)
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                itemsIndexed(stateUi.value.postsList) { index, item ->

                    val postScreenHandler = remember { PostScreenHandler() }

                    postScreenHandler.selectPost(
                        item,
                        action = {
                        }
                    )

                    postScreenHandler.PostScreen(onLongPress = {}, isScrollable)
                    if (index != stateUi.value.postsList.lastIndex) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Divider(
                            color = Color.LightGray,
                            thickness = 2.dp,
                            modifier = Modifier.fillMaxWidth(0.75f)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
//            AnimatedVisibility(
//                visible = menuHelper.getMenuVisibleValue(MENUS.POST).value,
//                enter = fadeIn(),
//                exit = fadeOut(),
//                modifier = Modifier
//                    .align(Center)
//            ) {
//                postScreenHandler.PostScreen(
//                    onLongPress = {
//                        swipableMenu.isReadyMenu = false
//                        menuHelper.changeVisibilityMenu(MENUS.POST)
//                        photoScreenClosed()
//                    }
//                )
//            }
//            AnimatedVisibility(
//                visible = menuHelper.getMenuVisibleValue(MENUS.RIGHT).value,
//                exit = slideOutHorizontally(),
//                modifier = Modifier
//                    .align(CenterEnd)
//            ) {
//                RightMenu(
//                    onClick = { itemMenu ->
//                        onSelectRightMenu(itemMenu, msViewModel)
//                    }
//                )
//            }
            if (swipableMenu.isTappedScreen)
                swipableMenu.CircularTouchMenu(param = swipableMenu)
//            AnimatedVisibility(
//                visible = menuHelper.getMenuVisibleValue(MENUS.CHATS).value,
//                modifier = Modifier
//                    .align(Center)
//            ) {
//                ChatsScreen(nav_controller, msViewModel)
//            }
//            AnimatedVisibility(
//                visible = menuHelper.getMenuVisibleValue(MENUS.SEARCHUSERS).value,
//                modifier = Modifier
//                    .align(Center)
//            ) {
//                SearchUsersScreen(
//                    nav_controller = nav_controller,
//                    viewModel = this@HomeScreen,
//                    msViewModel
//                )
//            }
//            AnimatedVisibility(
//                visible = menuHelper.getMenuVisibleValue(MENUS.FRIENDS).value,
//                modifier = Modifier
//                    .align(Center)
//            ) {
//                FriendsScreen(
//                    nav_controller = nav_controller!!, viewModel = this@HomeScreen,
//                    msViewModel = msViewModel
//                )
//            }
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
            AnimatedVisibility(
                visible = msViewModel.isVisibleCloudMenu,
                modifier = Modifier.align(BottomCenter)
            ) {
                CloudScreen(msViewModel.listFiles, viewModel = CloudViewModel())
            }
        }
    }

}

//@Composable
//fun StoriesCompose(modifier: Modifier) {
//
//    val list = listOf(
//        "123",
//        "321",
//        "123",
//        "123",
//        "321",
//        "123",
//        "123",
//        "321",
//        "123",
//    )
//
//    val display_metrics = LocalContext.current.resources.displayMetrics
//    val context = LocalContext.current
//
//
//    val width: Int =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            val windowMetrics: WindowMetrics =
//                (context as Activity).windowManager.currentWindowMetrics
//            windowMetrics.bounds.width()
//        } else {
//            display_metrics.widthPixels
//        }
//
//    val width_config = (width / display_metrics.density).toInt()
//
//    LazyRow(
//        modifier = modifier
//    ) {
//
//        itemsIndexed(list) { index, item ->
//
//            Spacer(modifier = Modifier.width(15.dp))
//
//            Box(
//                modifier = Modifier
//                    .clip(CircleShape)
//                    .background(Color(0xFFAFDED9))
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.image_dog),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .padding(2.dp)
//                        .clip(CircleShape)
//                        .size(
//                            ((width_config - 15 * 6) / 4).dp
//                        )
//                )
//            }
//            if (index == list.lastIndex)
//                Spacer(modifier = Modifier.width(15.dp))
//        }
//    }
//}


//@OptIn(ExperimentalCoilApi::class)
//@SuppressLint("CoroutineCreationDuringComposition")
//@NonRestartableComposable
//@Composable
//fun ColumnScope.PhotosFeed(viewModel: HomeMVIModel) {
//
//    val context = LocalContext.current
//
//    val state = viewModel.state.collectAsState()
//
//    LazyColumn(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .border(2.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
//            .align(CenterHorizontally)
//            .fillMaxWidth(0.9f)
//    ) {
//
//        item {
//            Spacer(modifier = Modifier.height(20.dp))
//        }
//
//        items(state.value.postsList.windowed(1, 1, true)) { item ->
//
//            var a = PostScreenHandler()
//
//            a.selectPost(
//                item[0],
//                action = {
//                }
//            )
//
//            a.PostScreen(onLongPress = {})
//            Spacer(modifier = Modifier.height(20.dp))
//
////            Row {
////                if (item.isNotEmpty()) {
////
////                    val cached = loader.diskCache?.get(item[0].item.address)?.data
////                    val request = if (cached != null) {
////                        val b = cached?.toFile()
////                        ImageRequest.Builder(context)
////                            .data(b)
////                            .diskCachePolicy(CachePolicy.READ_ONLY)
////                            .diskCacheKey(item[0].item.address)
////                            .crossfade(true)
////                            .build()
////                    } else {
////                        ImageRequest.Builder(context)
////                            .data("http://$MAINENDPOINT/${item[0].item.address}")
////                            .diskCachePolicy(CachePolicy.ENABLED)
////                            .diskCacheKey(item[0].item.address)
////                            .crossfade(true)
////                            .build()
////                    }
////                    loader.enqueue(request)
////
////                    AsyncImage(
////                        model = request,
////                        contentDescription = null,
////                        modifier = Modifier
////                            .padding(7.dp)
////                            .clickable {
////                                if (!viewModel.swipableMenu.isMenuOpen) {
////                                    viewModel.viewModelScope.launch {
////
////                                        viewModel.postScreenHandler.selectPost(
////                                            item[0],
////                                            action = {
////                                                viewModel.swipableMenu.isReadyMenu = false
////
////                                                viewModel.menuHelper.setVisibilityMenu(
////                                                    MENUS.POST,
////                                                    true
////                                                )
////                                            }
////                                        )
////                                    }
////                                }
////                            }
////                            .weight(1f)
////                    )
////                }
////                if (item.size > 1) {
////
////                    val cached = loader.diskCache?.get(item[1].item.address)?.data
////                    val request = if (cached != null) {
////                        val b = cached?.toFile()
////                        ImageRequest.Builder(context)
////                            .data(b)
////                            .diskCachePolicy(CachePolicy.READ_ONLY)
////                            .diskCacheKey(item[1].item.address)
////                            .crossfade(true)
////                            .build()
////                    } else {
////                        ImageRequest.Builder(context)
////                            .data("http://$MAINENDPOINT/${item[1].item.address}")
////                            .diskCachePolicy(CachePolicy.ENABLED)
////                            .diskCacheKey(item[1].item.address)
////                            .crossfade(true)
////
////                            .build()
////                    }
//////                    }
////                    loader.enqueue(request)
////                    AsyncImage(
////                        model = request,
////                        contentDescription = null,
////                        modifier = Modifier
////                            .padding(7.dp)
////                            .clickable {
////                                if (!viewModel.swipableMenu.isMenuOpen) {
////                                    viewModel.viewModelScope.launch {
////
////                                        viewModel.postScreenHandler.selectPost(
////                                            item[1],
////                                            action = {
////                                                viewModel.swipableMenu.isReadyMenu = false
////                                                viewModel.menuHelper.setVisibilityMenu(
////                                                    MENUS.POST,
////                                                    true
////                                                )
////                                            }
////                                        )
////                                    }
////                                }
////                            }
////                            .weight(1f)
////                    )
////                }
////            }
//        }
//    }
//}
