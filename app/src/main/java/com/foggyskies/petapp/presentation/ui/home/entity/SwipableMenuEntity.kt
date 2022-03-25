package com.foggyskies.petapp.presentation.ui.home.entity

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.R
import kotlinx.coroutines.delay

enum class SideScreen {
    TOP_RIGHT, TOP_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT, ABROAD
}

data class ItemSwipableMenu(
    var Image: Int,
    var isAnimatable: Boolean = false,
    var animationImages: List<Int> = emptyList(),
    var onValueSelected: () -> Unit = {}
)

class SwipableMenu() {

    //    var swipableMenuEntity by mutableStateOf(SwipableMenuEntity())
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

    val listItems by derivedStateOf {
        itemsOffset.map {
            Offset(x = it.x * density, y = it.y * density)
        }
    }

//    val listItemsOffset by derivedStateOf {
//        listItems.
//    }

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

        var a = listItems.map { item ->
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
            ItemSwipableMenu(Image = R.drawable.ic_sleep),
            ItemSwipableMenu(Image = R.drawable.ic_walk),
            ItemSwipableMenu(Image = R.drawable.ic_gamepad)
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
}

