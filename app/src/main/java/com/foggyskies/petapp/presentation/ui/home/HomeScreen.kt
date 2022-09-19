package com.foggyskies.petapp.presentation.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.presentation.ui.mainmenu.BottomSheetMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeMVIModel,
    nav_controller: NavHostController
) {
//    BackHandler(enabled = msViewModel.isVisibleCloudMenu) {
//        if (msViewModel.isVisibleCloudMenu) {
//            msViewModel.isVisibleCloudMenu = false
//        }
//    }

//    LaunchedEffect(key1 = Unit) {
//        checkInternet(this@HomeScreen::getContent)
//        if (msViewModel.mainSocket == null)
//            checkInternet(msViewModel::createMainSocket)
//    }
    val displayMetrics = LocalContext.current.resources.displayMetrics

    LaunchedEffect(key1 = Unit) {
        viewModel.swipableMenu.density = displayMetrics.density
        viewModel.swipableMenu.sizeScreen =
            Size(
                width = displayMetrics.widthPixels.toFloat(),
                height = displayMetrics.heightPixels.toFloat()
            )
//        viewModel.swipableMenu.navController = nav_controller!!
    }

    SideEffect {
        Log.e("УТЕЧКА", "ИДЕТ УТЕЧКА")
    }
//    if (swipableMenu.modalBottomSheetState == null)
//        swipableMenu.modalBottomSheetState = modalBottomSheetState

    BottomSheet(nav_controller) { bottomSheetState ->
        ContentScreen(bottomSheetState, viewModel)
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
//    viewModel: HomeMVIModel,
    nav_controller: NavHostController,
    content: @Composable (ModalBottomSheetState) -> Unit
) {

    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContentColor = Color.Transparent,
        sheetBackgroundColor = Color.Transparent,
        scrimColor = Color.Transparent,
        sheetElevation = 0.dp,
        sheetContent = {
            BottomSheetMenu(nav_controller = nav_controller)
        }
    ) {
        content(modalBottomSheetState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentScreen(bottomSheetState: ModalBottomSheetState, viewModel: HomeMVIModel) {

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                viewModel.swipableMenu.touchMenuListener(this) {
                    scope.launch {
                        bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
            }
            .fillMaxSize()
            .background(Color(0xFFF5FFFA))
    ) {
        val isScrollable = remember {
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
            itemsIndexed(viewModel.listPosts) { index, item ->

                val postScreenHandler = remember { PostScreenHandler(item) }

//                postScreenHandler.selectPost(
//                    item,
//                    action = {}
//                )

                postScreenHandler.PostScreen(onLongPress = {}, isScrollable)

                if (index != viewModel.listPosts.lastIndex) {
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
        SideEffect {
            Log.e("ТУТ ПЗИДЕЦ", "animatables[0][0].value.value .toString()")
        }
        if (viewModel.swipableMenu.isTappedScreen)
            viewModel.swipableMenu.CircularTouchMenu()

    }

}
//TODO Cloud
//@Composable
//fun BoxScope.CloudWindow() {
//
//    AnimatedVisibility(
//        visible = msViewModel.isVisibleCloudMenu,
//        modifier = Modifier.align(BottomCenter)
//    ) {
//        CloudScreen(msViewModel.listFiles, viewModel = CloudViewModel())
//    }
//
//}

//@Composable
//fun BoxScope.InternalNotificationWindow() {
//    AnimatedVisibility(
//        visible = msViewModel.isNotifyVisible,
//        modifier = Modifier
//            .align(TopCenter)
//    ) {
//        InternalNotificationScreen(
//            modifier = Modifier
//                .padding(top = 30.dp)
//                .fillMaxWidth(0.9f)
//                .align(TopCenter),
//            msViewModel,
//            nav_controller!!
//        )
//    }
//}