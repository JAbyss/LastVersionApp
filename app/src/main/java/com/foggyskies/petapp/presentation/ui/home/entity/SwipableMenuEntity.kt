package com.foggyskies.petapp.presentation.ui.home.entity

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.views.ClosedComposedFun
import kotlinx.coroutines.*

enum class SideScreen {
    TOP_RIGHT, TOP_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT
}

//data class ItemSwappableMenu(
//    var Image: Int,
//    var isAnimate: Boolean = false,
//    var animationImages: List<Int> = emptyList(),
//    var offset: Offset,
//    var onValueSelected: (NavHostController) -> Unit = {}
//)

//data class ItemSwappableMenu(
//    var Image: Int,
//    var isAnimate: Boolean = false,
//    var animationImages: List<Int> = emptyList(),
//    var position: SwappableMenu.PositionsIcons,
//    var onValueSelected: (NavHostController) -> Unit = {}
//)

data class ItemSwappableMenu(
    var Image: Int,
    var isAnimate: Boolean = false,
    var animationImages: List<Int> = emptyList(),
    var position: SwappableMenu.PositionsIcons,
    var onValueSelected: () -> Unit = {}
)

//data class SwappableIconSetting(
//    var listIcon: List<ItemSwappableMenu>,
//    var listOffset:
//)

class SwappableMenu() : CircularSelector() {


    private val radiusCircle = 80f
    private val radiusMenu = 320f
    var sizeIcon = 35.dp
    private val centerIcon = sizeIcon / 2

    lateinit var navController: NavHostController

    var sizeScreen by mutableStateOf(Size(0f, 0f))

    private var offsetStart by mutableStateOf(Offset.Zero)
    var density by mutableStateOf(2.75f)

    var isReadyMenu by mutableStateOf(true)
    var isMenuOpen by mutableStateOf(false)
    var isTappedScreen by mutableStateOf(false)

    private val mapOffsetItems = mapOf(
        PositionsIcons.TOP to Offset(x = 0f, y = -75f * density),
        PositionsIcons.TOP_RIGHT to Offset(x = 49.5f * density, y = -49.5f * density),
        PositionsIcons.RIGHT to Offset(x = 75f * density, y = 0f),
        PositionsIcons.RIGHT_BOTTOM to Offset(x = 49.5f * density, y = -49.5f * density),
        PositionsIcons.BOTTOM to Offset(x = 0f, y = 75f * density),
        PositionsIcons.LEFT_BOTTOM to Offset(x = -49.5f * density, y = 49.5f * density),
        PositionsIcons.LEFT to Offset(x = -75f * density, y = 0f),
        PositionsIcons.TOP_LEFT to Offset(x = -49.5f * density, y = -49.5f * density)
    )

    enum class PositionsIcons {
        TOP, TOP_RIGHT, RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, LEFT, TOP_LEFT
    }

    private val listIconsOffsets by derivedStateOf {
        listIcon.map { mapOffsetItems[it.position]!! }
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

    /**
     * Зеркалит иконки в меню
     * */
    private val listOffsetsForCircle by derivedStateOf {

        listIconsOffsets.map { item ->
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
    }

    private val offsetStartDp by derivedStateOf {
        DpOffset(x = (offsetStart.x / density).dp, y = (offsetStart.y / density).dp)
    }

    var listIcon by mutableStateOf(
        listOf(
            ItemSwappableMenu(
                Image = R.drawable.ic_sleep,
                position = PositionsIcons.TOP
            ),
            ItemSwappableMenu(
                Image = R.drawable.ic_walk,
                position = PositionsIcons.TOP_LEFT
            ),
            ItemSwappableMenu(
                Image = R.drawable.ic_gamepad,
                position = PositionsIcons.LEFT,
            )
        )
    )

    val listOffsetGlobal by derivedStateOf {
        listOffsetsForCircle.map {
            it + offsetStart
        }
    }

    fun onDragStart(startOffsetPx: Offset, callback: () -> Unit) {

        if (
            (startOffsetPx.x > sizeScreen.width * 0.1f) &&
            (startOffsetPx.x < sizeScreen.width * 0.9f) &&
            (startOffsetPx.y > sizeScreen.height * 0.2f) &&
            (startOffsetPx.y < sizeScreen.height * 0.9f)
        ) {
            offsetStart = startOffsetPx
            isTappedScreen = true
            isMenuOpen = true
        } else {
            // FIXME Не 100 проц что работает
            callback()
            isTappedScreen = false
            isMenuOpen = false
        }
    }

    private suspend fun menuClosing() {
        isMenuOpen = true
        delay(300)
        isMenuOpen = false
    }

    suspend fun touchMenuListener(pis: PointerInputScope, onDragStart: () -> Unit) =
        pis.run {
            var offset = Offset.Zero
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    if (isReadyMenu) {
                        offset = it
                        onDragStart(it, onDragStart)
                        startOffsetCS = offsetStartDp
                        radius = radiusMenu
                    }
                },
                onDragEnd = {
                    if (isReadyMenu) {
                        val listDistance = listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < radiusCircle) {
                            listIcon[listDistance.indexOf(minDistance)].onValueSelected()
                        }
                        isTappedScreen = false
                        CoroutineScope(Dispatchers.IO).launch {
                            menuClosing()
                        }
                        selectedTarget = StateCS.IDLE
                    }
                },
                onDrag = { change, _ ->
                    if (isReadyMenu) {
                        offset = change.position

                        val listDistance = listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < radiusCircle) {
                            sizeCS = radiusCircle
                            selectedTargetOffset =
                                listOffsetsForCircle[listDistance.indexOf(minDistance)]
                            selectedTarget = StateCS.SELECTED
                        } else {
                            selectedTarget = StateCS.IDLE
                            startOffsetCS = offsetStartDp
                            sizeCS = radiusMenu
                        }
                    }
                },
                onDragCancel = {
                    isTappedScreen = false
                }
            )
        }

    private val animates = derivedStateOf {
        val list = listIcon.map {
            listOf(
                Animatable(0f),
                Animatable(0f)
            )
        }.toMutableList()
        list.add(
            listOf(
                Animatable(0f),
                Animatable(0f)
            )
        )
        list
    }

    private val animationStart by mutableStateOf(Animatable(0f))

    private val offsetDpList = derivedStateOf {
        listIconsOffsets.map {
            DpOffset(
                offsetStartDp.x + (it.x / density).dp - centerIcon,
                offsetStartDp.y + (it.y / density).dp - centerIcon
            )
        }.toMutableList()
    }

    private val deferredList = mutableListOf<Deferred<Unit>>()

    @Composable
    fun CircularTouchMenu() {

        val animationSpec = infiniteRepeatable<Float>(
            animation = tween(1300, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )

        DisposableEffect(key1 = isMenuOpen) {
            onDispose {
                animates.value.clear()
                val list = listIcon.map {
                    listOf(
                        Animatable(0f),
                        Animatable(0f)
                    )
                }.toMutableList()
                list.add(
                    listOf(
                        Animatable(0f),
                        Animatable(0f)
                    )
                )
                animates.value.addAll(list)
                offsetDpList.value.clear()
                deferredList.forEach { it.cancel() }
                deferredList.clear()
//                offsetDpList.value.addAll(listIconsOffsets.map {
//                    DpOffset(
//                        offsetStartDp.x + (it.x / density).dp - centerIcon,
//                        offsetStartDp.y + (it.y / density).dp - centerIcon
//                    )
//                })
            }
        }

        LaunchedEffect(key1 = isMenuOpen) {

            animationStart.animateTo(1f, animationSpec = tween(300))

            animates.value.forEachIndexed { index, animatable ->
                val deferred = async {
                    delay(index * 125L)
                    animatable[1].animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 100
                        )
                    )
                    animatable[0].animateTo(
                        targetValue = 1f,
                        animationSpec = animationSpec
                    )
                    Unit
                }
                deferredList.add(deferred)
            }
        }

        @Composable
        fun CircularSelectorWidget() {

            val infiniteTransition = rememberInfiniteTransition()

            val animationSpecStart by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1800
                        1.1f at 600 with LinearEasing
                        1f at 1200 with LinearEasing
                        0.9f at 1800 with LinearEasing
                    },
                    repeatMode = RepeatMode.Reverse
                )
            )

            val animationRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 75f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 3000
                        15f at 500 with LinearEasing
                        20f at 700 with LinearEasing
                        35f at 1200 with LinearEasing
                        45f at 2000 with LinearEasing
                        50f at 2400 with LinearEasing
                        75f at 3000 with LinearEasing
                    },
                    repeatMode = RepeatMode.Reverse
                )
            )

            val animationSize by animateFloatAsState(targetValue = if (selectedTarget == StateCS.IDLE) radiusMenu * animationStart.value else radiusCircle)

            val animationOffset by animateOffsetAsState(
                targetValue = if (selectedTarget == StateCS.IDLE) Offset(
                    x = -(radius * animationSpecStart) * 1.15f,
                    y = -(radius * animationSpecStart) * 1.15f,
                ) else Offset(
                    x = -(radius) * animationSpecStart / 4f + selectedTargetOffset.x,
                    y = -(radius) * animationSpecStart / 4f + selectedTargetOffset.y,
                )
            )

            Canvas(
                modifier = Modifier
                    .offset(x = startOffsetCS.x, y = startOffsetCS.y)
                    .rotate(if (selectedTarget == StateCS.IDLE) animationRotation else 0f)
            ) {

                val size = if (selectedTarget == StateCS.IDLE)
                    Size(
                        animationSize * animationSpecStart * 2.3f,
                        animationSize * animationSpecStart * 2.3f,
                    ) else
                    Size(
                        animationSize * animationSpecStart * 2f,
                        animationSize * animationSpecStart * 2f,
                    )

                drawArc(
                    color = Color.White,
                    startAngle = 0f,
                    sweepAngle = 25f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthArcs, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 45f,
                    sweepAngle = 50f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthArcs, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 110f,
                    sweepAngle = 35f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthArcs, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 160f,
                    sweepAngle = 40f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthArcs, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 220f,
                    sweepAngle = 28f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthArcs, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 280f,
                    sweepAngle = 37f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthArcs, cap = StrokeCap.Round)
                )
            }
        }

        val infiniteTransition = rememberInfiniteTransition()

        val animationSpecStart by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1800
                    1.1f at 600 with LinearEasing
                    1f at 1200 with LinearEasing
                    0.9f at 1800 with LinearEasing
                },
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .wrapContentSize()
        )
        {


            ClosedComposedFun {
                Canvas(
                    modifier = Modifier
                        .offset(x = offsetStartDp.x, y = offsetStartDp.y)
                        .wrapContentSize()
                ) {

                    drawCircle(
                        color = Color.Black,
                        center = Offset.Zero,
                        radius = radiusMenu * animationSpecStart * animationStart.value,
                        alpha = 0.9f
                    )

                    animates.value.forEachIndexed { index, list ->
                        drawCircle(
                            color = Color.White,
                            radius = radiusCircle * list[0].value,
                            center = if (index == 0) Offset.Zero else listOffsetsForCircle[index - 1],
                            alpha = 1f - list[0].value
                        )
                    }
                }
            }

            ClosedComposedFun {
                CircularSelectorWidget()
            }

            ClosedComposedFun {

                animates.value.forEachIndexed { index, list ->
                    if (index > 0) {

                        val item = listIcon[index - 1]

//                        val offsetS = DpOffset(
//                            offsetStartDp.x + (listOffsetsForCircle[index - 1].x / density).dp - centerIcon,
//                            offsetStartDp.y + (listOffsetsForCircle[index - 1].y / density).dp - centerIcon
//                        )
//                        if (item.isAnimate) {
//                            repeat(item.animationImages.size) { index ->
//
//                                var degrees = 0f
//
//                                val offset = when (index) {
//                                    0 -> {
//                                        degrees = -15f
//                                        DpOffset(offsetS.x - 12.dp, offsetS.y)
//                                    }
//                                    2 -> DpOffset(offsetS.x, offsetS.y)
//                                    1 -> {
//                                        degrees = 15f
//                                        DpOffset(offsetS.x + 12.dp, offsetS.y)
//                                    }
//                                    else -> {
//                                        DpOffset.Zero
//                                    }
//                                }
//
//                                Image(
//                                    painter = painterResource(id = item.animationImages[index]),
//                                    contentDescription = null,
//                                    modifier = Modifier
//                                        .offset(x = offset.x, y = offset.y)
//                                        .size(sizeIcon)
//                                        .rotate(degrees)
//                                        .alpha(1f),
//                                )
//                            }
//                        } else
                            Image(
                                painter = painterResource(id = item.Image),
                                contentDescription = null,
                                modifier = Modifier
                                    .offset(x = offsetDpList.value[index-1].x, y = offsetDpList.value[index-1].y)
                                    .size(sizeIcon)
                                    .alpha(list[0].value),
                            )
                    }
                }
            }
        }
    }
}