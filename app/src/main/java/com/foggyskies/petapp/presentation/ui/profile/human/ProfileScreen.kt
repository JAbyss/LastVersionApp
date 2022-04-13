package com.foggyskies.petapp.presentation.ui.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.presentation.ui.globalviews.CircularTouchMenu
import com.foggyskies.petapp.presentation.ui.globalviews.FullInvisibleBack
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.profile.human.PageProfileFormattedDC
import com.foggyskies.petapp.presentation.ui.profile.human.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.human.StateProfile
import com.foggyskies.petapp.presentation.ui.profile.human.views.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    nav_controller: NavHostController,
    viewModel: ProfileViewModel,
    viewModelHome: HomeViewModel,
    msViewModel: MainSocketViewModel
) {

    val context = LocalContext.current

    val state = rememberLazyListState()

    val backHandler = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(key1 = Unit) {
        msViewModel.sendAction("getPagesProfile|")
    }

    BackHandler {
        if (viewModel.stateProfile == StateProfile.PET) {
            viewModel.stateProfile = StateProfile.HUMAN
            viewModel.a
        } else
            nav_controller.navigate(nav_controller.backQueue[1].destination.route!!)
    }

    val density = LocalContext.current.resources.displayMetrics

    viewModel.density = density.density
    viewModel.swipableMenu.density = density.density
    viewModel.swipableMenu.sizeScreen =
        Size(width = density.widthPixels.toFloat(), height = density.heightPixels.toFloat())
    viewModel.a
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var offset = Offset.Zero

                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        offset = it
                        viewModel.swipableMenu.onDragStart(it)
                        viewModel.circularSelector.offset = viewModel.swipableMenu.offsetStartDp
                        viewModel.circularSelector.radius = viewModel.swipableMenu.radiusMenu
                    },
                    onDragEnd = {

                        val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
                            when (listDistance.indexOf(minDistance)) {
                                0 -> {
                                    backHandler?.onBackPressed()
                                }
                                1 -> {
                                    nav_controller?.navigate("AdsHomeless")
                                }
                                2 -> {
                                    nav_controller?.navigate("Chat")
                                }
                                3 -> {
                                    msViewModel.sendAction("logOut")
                                    viewModelHome.viewModelScope.launch {
                                        msViewModel.mainSocket?.close()
                                    }
                                    TOKEN = ""
                                    USERNAME = ""
                                    context
                                        .getSharedPreferences(
                                            "Token",
                                            Context.MODE_PRIVATE
                                        )
                                        .edit()
                                        .clear()
                                        .apply()
                                    context
                                        .getSharedPreferences(
                                            "User",
                                            Context.MODE_PRIVATE
                                        )
                                        .edit()
                                        .clear()
                                        .apply()
                                    nav_controller.navigate("Authorization") {
                                        popUpTo("Home") {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                            Toast
                                .makeText(
                                    context,
                                    "SELECTED ${listDistance.indexOf(minDistance)}",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            Log.e("SELECTOR", "SELECTED ${listDistance.indexOf(minDistance)}")
                        }
                        viewModel.swipableMenu.isTappedScreen = false
                        viewModel.viewModelScope.launch {
                            viewModel.swipableMenu.menuClosing()
                        }
                        viewModel.circularSelector.selectedTarget = StateCS.IDLE
                    },
                    onDrag = { change, dragAmount ->
                        offset = change.position

                        val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
                            viewModel.circularSelector.size =
                                viewModel.swipableMenu.radiusCircle
                            viewModel.circularSelector.selectedTargetOffset =
                                viewModel.swipableMenu.listOffsetsForCircle[listDistance.indexOf(
                                    minDistance
                                )]
                            viewModel.circularSelector.selectedTarget = StateCS.SELECTED
                        } else {
                            viewModel.circularSelector.selectedTarget = StateCS.IDLE
                            viewModel.circularSelector.offset =
                                viewModel.swipableMenu.offsetStartDp
                            viewModel.circularSelector.size =
                                viewModel.swipableMenu.radiusMenu
                        }
                    }
                )
            }
    ) {

        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            HeadProfile(viewModel = viewModel, state = state)

            // Профиль Человека
            item {
                MainProfilePages(viewModel, msViewModel)
            }

            item {
                AnimatedVisibility(visible = viewModel.stateProfile == StateProfile.PET) {
                    Column() {
                        LazyRow {
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

                            itemsIndexed(list) { index, item ->
                                StoriesProfile(
                                    index,
                                    list.lastIndex,
                                    modifier = Modifier
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            if (viewModel.stateProfile == StateProfile.PET)
                items(viewModel.listPostImages.windowed(3, 3, true)) { item ->
                    Log.e("AAAAAAAAAAAAAAAAA", "SIZE ${viewModel.listPostImages.size}")
                    Row {
                        if (item.isNotEmpty())
                        AsyncImage(
                            model = "http://$MAINENDPOINT/${item[0].address}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                        )
                        if (item.size > 1)
                        AsyncImage(
                            model = "http://$MAINENDPOINT/${item[1].address}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                        )
                        if (item.size > 2)
                        AsyncImage(
                            model = "http://$MAINENDPOINT/${item[2].address}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                        )
                    }
                }
        }

        AnimatedVisibility(
            visible = viewModel.isMyContactClicked,
            modifier = Modifier
                .align(Center)
        ) {
            MyLinkCard(onClickClose = { viewModel.isMyContactClicked = false })
        }
        AnimatedVisibility(
            visible = viewModel.isStatusClicked,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Center)
        ) {
            CircularStatuses(
                onClickClose = { viewModel.isStatusClicked = false },
                onClickAdd = {
                    viewModel.nowSelectedStatus = it
                    viewModel.isStatusClicked = false
                },
                onClickStatus = {
                    viewModel.nowSelectedStatus = it
                    viewModel.isStatusClicked = false
                }
            )
        }

        AnimatedVisibility(
            visible = viewModel.stateProfile == StateProfile.PET,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .align(BottomCenter)
        ) {
            PetBottomMenu()
        }
        if (viewModel.swipableMenu.isTappedScreen)
            CircularTouchMenu(param = viewModel.swipableMenu, viewModel.circularSelector)
        AnimatedVisibility(
            visible = viewModel.isAddingNewCard,
            modifier = Modifier
                .align(Center),
        ) {

            FullInvisibleBack(onBackClick = { viewModel.isAddingNewCard = false }) {

                PetCard(
                    item = PageProfileFormattedDC(
                        id = "",
                        image = "",
                        title = "Заголовок",
                        description = "Описание",
                        countContents = "",
                        countSubscribers = ""
                    ),
                    onClickPetCard = { _, _ -> },
                    viewModel = viewModel,
                    creatingModifier = Modifier
                        .border(3.dp, Color.Gray, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .height(345.dp)
                        .width(287.5.dp)
                        .align(Center)
                )
            }
        }
    }
}