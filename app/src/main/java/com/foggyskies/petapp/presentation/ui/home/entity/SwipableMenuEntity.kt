package com.foggyskies.petapp.presentation.ui.home.entity

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.R
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SideScreen {
    TOP_RIGHT, TOP_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT
}

data class ItemSwappableMenu(
    var Image: Int,
    var isAnimate: Boolean = false,
    var animationImages: List<Int> = emptyList(),
    var onValueSelected: () -> Unit = {}
)

class SwappableMenu() {

    var sizeScreen by mutableStateOf(Size(0f, 0f))

    var offsetStart by mutableStateOf(Offset.Zero)
    var density by mutableStateOf(2.75f)

    var isReadyMenu by mutableStateOf(true)
    var isMenuOpen by mutableStateOf(false)
    var isTappedScreen by mutableStateOf(false)

    var itemsOffset by mutableStateOf(listOf(
        Offset(x = 10f, y = -70f),
        Offset(x = -45f, y = -45f),
        Offset(x = -70f, y = 10f),
    ))

    private val listItems by derivedStateOf {
        itemsOffset.map {
            Offset(x = it.x * density, y = it.y * density)
        }
    }

    private val sideScreen by derivedStateOf {

        if (offsetStartDp.x < (80 + radiusCircle / 2 / density).dp)
            if (offsetStartDp.y < (80 + radiusCircle / 2 / density).dp)
                SideScreen.TOP_LEFT
            else
                SideScreen.BOTTOM_LEFT
        else
            if (offsetStartDp.y > (80 + radiusCircle / 2 / density).dp)
                SideScreen.BOTTOM_RIGHT
            else
                SideScreen.TOP_RIGHT
    }

    val listOffsetsForCircle by derivedStateOf {

        val a = listItems.map { item ->
            var x: Float = 0f
            var y: Float = 0f
            when (sideScreen) {
                SideScreen.BOTTOM_RIGHT -> {
                    x = item.x
                    y = item.y
                }
                SideScreen.BOTTOM_LEFT -> {
                    x = -item.x
                    y = item.y
                }
                SideScreen.TOP_LEFT -> {
                    x = -item.x
                    y = -item.y
                }
                SideScreen.TOP_RIGHT -> {
                    x = item.x
                    y = -item.y
                }
                else -> {}
            }
            Offset(x, y)
        }
        a
    }

    val radiusCircle = 80f

    val radiusMenu = 320f

    val offsetStartDp by derivedStateOf {
        DpOffset(x = (offsetStart.x / density).dp, y = (offsetStart.y / density).dp)
    }

    var sizeIcon = 35.dp

    var listIcon by mutableStateOf(
        listOf(
            ItemSwappableMenu(Image = R.drawable.ic_sleep),
            ItemSwappableMenu(Image = R.drawable.ic_walk),
            ItemSwappableMenu(Image = R.drawable.ic_gamepad)
        )
    )


    val listOffsetGlobal by derivedStateOf {
        listOffsetsForCircle.map {
            it + offsetStart
        }
    }

    fun onDragStart(startOffsetPx: Offset) {

        if (
            (startOffsetPx.x > sizeScreen.width * 0.1f) &&
            (startOffsetPx.x < sizeScreen.width * 0.9f) &&
            (startOffsetPx.y > sizeScreen.height * 0.1f) &&
            (startOffsetPx.y < sizeScreen.height * 0.9f)
        ) {
            offsetStart = startOffsetPx
            isTappedScreen = true
            isMenuOpen = true
        } else {
            isTappedScreen = false
            isMenuOpen = false
        }
    }

    suspend fun menuClosing() {
        isMenuOpen = true
        delay(300)
        isMenuOpen = false
    }

    fun Modifier.touchMenuListener(){
//        pointerInput(Unit) {
//            var offset = Offset.Zero
//
//            detectDragGesturesAfterLongPress(
//                onDragStart = {
//                    offset = it
//                    onDragStart(it)
//                    offset = swipableMenu.offsetStartDp
//                    circularSelector.radius = viewModel.swipableMenu.radiusMenu
//                },
//                onDragEnd = {
//
//                    val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
//                        (it - offset).getDistance()
//                    }
//                    val minDistance = listDistance.minOrNull()
//
//                    if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
//                        when (listDistance.indexOf(minDistance)) {
//                            0 -> {
//                                backHandler?.onBackPressed()
//                            }
//                            1 -> {
//                                nav_controller?.navigate("AdsHomeless")
//                            }
//                            2 -> {
//                                nav_controller?.navigate("Chat")
//                            }
//                            3 -> {
//                                msViewModel.sendAction("logOut")
//                                viewModelHome.viewModelScope.launch {
//                                    msViewModel.mainSocket?.close()
//                                }
//                                MainActivity.TOKEN = ""
//                                MainActivity.USERNAME = ""
//                                context
//                                    .getSharedPreferences(
//                                        "Token",
//                                        Context.MODE_PRIVATE
//                                    )
//                                    .edit()
//                                    .clear()
//                                    .apply()
//                                context
//                                    .getSharedPreferences(
//                                        "User",
//                                        Context.MODE_PRIVATE
//                                    )
//                                    .edit()
//                                    .clear()
//                                    .apply()
//                                nav_controller.navigate("Authorization") {
//                                    popUpTo("Home") {
//                                        inclusive = true
//                                    }
//                                }
//                            }
//                        }
//                        Toast
//                            .makeText(
//                                context,
//                                "SELECTED ${listDistance.indexOf(minDistance)}",
//                                Toast.LENGTH_SHORT
//                            )
//                            .show()
//                        Log.e("SELECTOR", "SELECTED ${listDistance.indexOf(minDistance)}")
//                    }
//                    viewModel.swipableMenu.isTappedScreen = false
//                    viewModel.viewModelScope.launch {
//                        viewModel.swipableMenu.menuClosing()
//                    }
//                    viewModel.circularSelector.selectedTarget = StateCS.IDLE
//                },
//                onDrag = { change, dragAmount ->
//                    offset = change.position
//
//                    val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
//                        (it - offset).getDistance()
//                    }
//                    val minDistance = listDistance.minOrNull()
//
//                    if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
//                        viewModel.circularSelector.size =
//                            viewModel.swipableMenu.radiusCircle
//                        viewModel.circularSelector.selectedTargetOffset =
//                            viewModel.swipableMenu.listOffsetsForCircle[listDistance.indexOf(
//                                minDistance
//                            )]
//                        viewModel.circularSelector.selectedTarget = StateCS.SELECTED
//                    } else {
//                        viewModel.circularSelector.selectedTarget = StateCS.IDLE
//                        viewModel.circularSelector.offset =
//                            viewModel.swipableMenu.offsetStartDp
//                        viewModel.circularSelector.size =
//                            viewModel.swipableMenu.radiusMenu
//                    }
//                }
//            )
//        }
    }
}

