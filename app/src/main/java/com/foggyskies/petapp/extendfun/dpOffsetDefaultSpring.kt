package com.foggyskies.testingscrollcompose.presentation.ui.registation.customui.animation

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.DpOffset

private val dpOffsetDefaultSpring = spring(visibilityThreshold = DpOffset.VisibilityThreshold)


@Composable
fun animateDpOffsetAsState(
    targetValue: DpOffset,
    animationSpec: AnimationSpec<DpOffset> = dpOffsetDefaultSpring,
    finishedListener: ((DpOffset) -> Unit)? = null
): State<DpOffset> {
    return animateValueAsState(
        targetValue, DpOffset.VectorConverter, animationSpec, finishedListener = finishedListener
    )
}