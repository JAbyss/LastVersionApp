package com.foggyskies.petapp.presentation.ui.globalviews

//import com.foggyskies.petapp.presentation.ui.home.animations.ShowCaseView

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.extendfun.forEachComposable
import com.foggyskies.petapp.extendfun.forEachRepeatable
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.home.entity.SwipableMenu
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CircularTouchMenu(
    param: SwipableMenu,
    circularSelector: CircularSelector
) {

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
            circularSelector,
            animationStart
        )

        values_list.forEachIndexed { index, list ->
            if (index > 0) {

                val item = param.listIcon[index - 1]

                val offsetS = DpOffset(
                    param.offsetStartDp.x + (param.listOffsetsForCircle[index - 1].x / param.density).dp - centerIcon,
                    param.offsetStartDp.y + (param.listOffsetsForCircle[index - 1].y / param.density).dp - centerIcon
                )
                if (item.isAnimatable) {
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
                            else -> {DpOffset.Zero}
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

@Composable
fun CircularSelectorWidget(
    circularSelector: CircularSelector,
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

    val animationSize by animateFloatAsState(targetValue = if (circularSelector.selectedTarget == StateCS.IDLE) 320f * animationStart.value else 80f)

    val animationOffset by animateOffsetAsState(
        targetValue = if (circularSelector.selectedTarget == StateCS.IDLE) Offset(
            x = -(circularSelector.radius * animationSpecStart) * 1.15f,
            y = -(circularSelector.radius * animationSpecStart) * 1.15f,
        ) else Offset(
            x = -(circularSelector.radius) * animationSpecStart / 4f + circularSelector.selectedTargetOffset.x,
            y = -(circularSelector.radius) * animationSpecStart / 4f + circularSelector.selectedTargetOffset.y,
        )
    )

    Canvas(
        modifier = Modifier
            .offset(x = circularSelector.offset.x, y = circularSelector.offset.y)
            .rotate(if (circularSelector.selectedTarget == StateCS.IDLE) animationRotation else 0f)
    ) {

        val offset = if (circularSelector.selectedTarget == StateCS.IDLE)
            Offset(
                x = -(circularSelector.radius * animationSpecStart) * 1.15f,
                y = -(circularSelector.radius * animationSpecStart) * 1.15f,
            ) else
            Offset(
                x = -(circularSelector.radius) / 4f + circularSelector.selectedTargetOffset.x,
                y = -(circularSelector.radius) / 4f + circularSelector.selectedTargetOffset.y,
            )
        val size = if (circularSelector.selectedTarget == StateCS.IDLE)
            Size(
                animationSize * animationSpecStart * 2.3f,
                animationSize * animationSpecStart * 2.3f,
            ) else
            Size(
                animationSize * animationSpecStart * 2f,
                animationSize * animationSpecStart * 2f,
            )

        val widthStroke = circularSelector.widthArcs

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