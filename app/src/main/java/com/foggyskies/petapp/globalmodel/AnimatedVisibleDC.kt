package com.foggyskies.petapp.globalmodel

import androidx.compose.runtime.MutableState

data class AnimatedVisibleDC<T>(
    var item: T,
    var isVisible: MutableState<Boolean>
)