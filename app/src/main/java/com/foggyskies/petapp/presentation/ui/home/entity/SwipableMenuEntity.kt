package com.foggyskies.petapp.presentation.ui.home.entity

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.foggyskies.petapp.R
import kotlinx.coroutines.*

enum class SideScreen {
    TOP_RIGHT, TOP_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT
}

data class ItemSwappableMenu(
    var Image: Int,
    var isAnimate: Boolean = false,
    var animationImages: List<Int> = emptyList(),
    var offset: Offset,
    var onValueSelected: (NavHostController) -> Unit = {}
)

//data class SwappableIconSetting(
//    var listIcon: List<ItemSwappableMenu>,
//    var listOffset:
//)

class SwappableMenu() : CircularSelector() {


    fun Modifier(modifier: Modifier, callback: () -> Unit = {}): Modifier {
        return modifier.touchMenuListener(callback)
    }

    @OptIn(ExperimentalMaterialApi::class)
    var modalBottomSheetState: ModalBottomSheetState? = null

    lateinit var navController: NavHostController

    var sizeScreen by mutableStateOf(Size(0f, 0f))

    var offsetStart by mutableStateOf(Offset.Zero)
    var density by mutableStateOf(2.75f)

    var isReadyMenu by mutableStateOf(true)
    var isMenuOpen by mutableStateOf(false)
    var isTappedScreen by mutableStateOf(false)

    private val listItems by derivedStateOf {
        listIcon.map {
            Offset(x = it.offset.x * density, y = it.offset.y * density)
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
            ItemSwappableMenu(
                Image = R.drawable.ic_sleep,
                offset = Offset(x = 10f, y = -70f)
            ),
            ItemSwappableMenu(
                Image = R.drawable.ic_walk,
                offset = Offset(x = -50f, y = -45f)
            ),
            ItemSwappableMenu(
                Image = R.drawable.ic_gamepad,
                offset = Offset(x = -70f, y = 10f),
            )
        )
    )

    val listOffsetGlobal by derivedStateOf {
        listOffsetsForCircle.map {
            it + offsetStart
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
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
//            CoroutineScope(Dispatchers.Default).launch {
//                modalBottomSheetState?.show()
//            }
            callback()
            isTappedScreen = false
            isMenuOpen = false
        }
    }

    suspend fun menuClosing() {
        isMenuOpen = true
        delay(300)
        isMenuOpen = false
    }

    @SuppressLint("SuspiciousIndentation")
    fun Modifier.touchMenuListener(callback: () -> Unit): Modifier {
        return pointerInput(Unit) {
            var offset = Offset.Zero
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        if (isReadyMenu) {
                            offset = it
                            onDragStart(it, callback)
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
                                listIcon[listDistance.indexOf(minDistance)].onValueSelected.invoke(
                                    navController
                                )
//                        listenerSelector(listDistance.indexOf(minDistance))
                            }
                            isTappedScreen = false
                            CoroutineScope(Dispatchers.IO).launch {
                                menuClosing()
                            }
                            selectedTarget = StateCS.IDLE
                        }
                    },
                    onDrag = { change, dragAmount ->
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
                    }
                )
        }
    }

    @Composable
    fun CircularTouchMenu(
        param: SwappableMenu,
    ) {

        @Composable
        fun CircularSelectorWidget(
            swappableMenu: SwappableMenu,
            animationStart: Animatable<Float, AnimationVector1D>,
        ) {

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

            val animationSize by animateFloatAsState(targetValue = if (swappableMenu.selectedTarget == StateCS.IDLE) 320f * animationStart.value else 80f)

            val animationOffset by animateOffsetAsState(
                targetValue = if (swappableMenu.selectedTarget == StateCS.IDLE) Offset(
                    x = -(swappableMenu.radius * animationSpecStart) * 1.15f,
                    y = -(swappableMenu.radius * animationSpecStart) * 1.15f,
                ) else Offset(
                    x = -(swappableMenu.radius) * animationSpecStart / 4f + swappableMenu.selectedTargetOffset.x,
                    y = -(swappableMenu.radius) * animationSpecStart / 4f + swappableMenu.selectedTargetOffset.y,
                )
            )

            Canvas(
                modifier = Modifier
                    .offset(x = swappableMenu.startOffsetCS.x, y = swappableMenu.startOffsetCS.y)
                    .rotate(if (swappableMenu.selectedTarget == StateCS.IDLE) animationRotation else 0f)
            ) {

                val offset = if (swappableMenu.selectedTarget == StateCS.IDLE)
                    Offset(
                        x = -(swappableMenu.radius * animationSpecStart) * 1.15f,
                        y = -(swappableMenu.radius * animationSpecStart) * 1.15f,
                    ) else
                    Offset(
                        x = -(swappableMenu.radius) / 4f + swappableMenu.selectedTargetOffset.x,
                        y = -(swappableMenu.radius) / 4f + swappableMenu.selectedTargetOffset.y,
                    )
                val size = if (swappableMenu.selectedTarget == StateCS.IDLE)
                    Size(
                        animationSize * animationSpecStart * 2.3f,
                        animationSize * animationSpecStart * 2.3f,
                    ) else
                    Size(
                        animationSize * animationSpecStart * 2f,
                        animationSize * animationSpecStart * 2f,
                    )

                val widthStroke = swappableMenu.widthArcs

                drawArc(
                    color = Color.White,
                    startAngle = 0f,
                    sweepAngle = 25f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthStroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 45f,
                    sweepAngle = 50f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthStroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 110f,
                    sweepAngle = 35f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthStroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 160f,
                    sweepAngle = 40f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthStroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 220f,
                    sweepAngle = 28f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthStroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White,
                    startAngle = 280f,
                    sweepAngle = 37f,
                    useCenter = false,
                    topLeft = animationOffset,
                    size = size,
                    style = Stroke(width = widthStroke, cap = StrokeCap.Round)
                )
            }
        }

        val centerIcon = param.sizeIcon / 2

        val animationSpec = infiniteRepeatable<Float>(
            animation = tween(1300, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
        val animatables = mutableListOf(listOf(
            remember { Animatable(0f) },
            remember { Animatable(0f) }
        ))
        param.listIcon.forEach { _ ->
            animatables.add(listOf(
                remember { Animatable(0f) },
                remember { Animatable(0f) }
            ))
        }

        val animationStart = remember { Animatable(0f) }

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

        LaunchedEffect(key1 = Unit) {
            animationStart.animateTo(1f, animationSpec = tween(300))
        }

        animatables.forEachIndexed { index, animatable ->
            LaunchedEffect(Unit) {

                delay(index * 200L)
                animatable[1].animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 100
                    )
                )
//            animationStart.animateTo(1f, animationSpec = tween(300))
                animatable[0].animateTo(
                    targetValue = 1f,
                    animationSpec = animationSpec
                )
            }
        }

        val values_list = mutableListOf<List<Float>>()

        animatables.forEachIndexed { index, list ->
            val dys = list.map { it.value }
            values_list.add(dys)
        }

        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {


            Canvas(
                modifier = Modifier
                    .offset(x = param.offsetStartDp.x, y = param.offsetStartDp.y)
                    .wrapContentSize()
            ) {

                drawCircle(
                    color = Color.Black,
                    center = Offset.Zero,
                    radius = param.radiusMenu * animationSpecStart * animationStart.value,
                    alpha = 0.9f
                )

                values_list.forEachIndexed { index, list ->
                    drawCircle(
                        color = Color.White,
                        radius = param.radiusCircle * list[0],
                        center = if (index == 0) Offset.Zero else param.listOffsetsForCircle[index - 1],
                        alpha = 1f - list[0]
                    )
                }
            }

            CircularSelectorWidget(
                param,
                animationStart
            )

            values_list.forEachIndexed { index, list ->
                if (index > 0) {

                    val item = param.listIcon[index - 1]

                    val offsetS = DpOffset(
                        param.offsetStartDp.x + (param.listOffsetsForCircle[index - 1].x / param.density).dp - centerIcon,
                        param.offsetStartDp.y + (param.listOffsetsForCircle[index - 1].y / param.density).dp - centerIcon
                    )
                    if (item.isAnimate) {
                        repeat(item.animationImages.size) { index ->

                            var degrees = 0f

                            val offset = when (index) {
                                0 -> {
                                    degrees = -15f
                                    DpOffset(offsetS.x - 12.dp, offsetS.y)
                                }
                                2 -> DpOffset(offsetS.x, offsetS.y)
                                1 -> {
                                    degrees = 15f
                                    DpOffset(offsetS.x + 12.dp, offsetS.y)
                                }
                                else -> {
                                    DpOffset.Zero
                                }
                            }

                            Image(
                                painter = painterResource(id = item.animationImages[index]),
                                contentDescription = null,
                                modifier = Modifier
                                    .offset(x = offset.x, y = offset.y)
                                    .size(param.sizeIcon)
                                    .rotate(degrees)
                                    .alpha(1f),
                            )
                        }
                    } else
                        Image(
                            painter = painterResource(id = item.Image),
                            contentDescription = null,
                            modifier = Modifier
                                .offset(x = offsetS.x, y = offsetS.y)
                                .size(param.sizeIcon)
                                .alpha(list[1]),
                        )
                }
            }
        }
    }
}