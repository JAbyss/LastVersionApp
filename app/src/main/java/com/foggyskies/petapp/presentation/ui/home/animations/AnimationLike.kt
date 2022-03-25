package com.foggyskies.petapp.presentation.ui.home.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max

@Composable
fun AnimationLike(target: MutableState<ShowCaseProperty>) {

//   scope.launch {
//
//       isLikeAnimation.value = true
//       delay(500)
//       isLikeAnimation.value = false
//   }

//    val uniqueTargets = targets.values.sortedBy { it.index }
//    var currentTargetIndex by remember { mutableStateOf(0) }
//    val currentTarget = if (uniqueTargets.isNotEmpty() && currentTargetIndex < uniqueTargets.size)
//        uniqueTargets[currentTargetIndex] else null

//    currentTarget?.let {
//
//    }

    val targetCords = target.value.coordinate
    val targetRect = targetCords?.boundsInRoot()
    val maxDimension = targetCords?.size?.width?.absoluteValue?.let { max(it, targetCords.size.height.absoluteValue) }
//    val animationSpec = tween<Float>(2000, easing = FastOutLinearInEasing)

    val animationSpec = infiniteRepeatable<Float>(
        animation = tween(20, easing = FastOutLinearInEasing),
        repeatMode = RepeatMode.Restart
    )

    val animatables = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    animatables.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
//            delay(index + 1000L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = animationSpec
            )
        }
    }

    val dys = animatables.map { it.value }
    Box {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
//                .pointerInput(target) {
//                    detectTapGestures { tapOffset ->
//                        if (targetRect.contains(tapOffset)) {
//                            onShowCaseCompleted()
//                        }
//                    }
//                }
                .graphicsLayer(alpha = 0.99f)
        ) {

            dys.forEach { dy ->
                if (maxDimension != null) {
                    targetRect?.center?.let {
                        drawCircle(
                            color = Color.White,
                            radius = maxDimension * dy * 2f,
                            center = it,
                            alpha = 1 - dy
                        )
                    }
                }
            }
        }
    }
}