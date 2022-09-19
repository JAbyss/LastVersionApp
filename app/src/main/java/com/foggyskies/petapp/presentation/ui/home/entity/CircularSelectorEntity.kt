package com.foggyskies.petapp.presentation.ui.home.entity

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset

enum class StateCS {
    IDLE, SELECTED
}

open class CircularSelector {

    var selectedTarget by mutableStateOf(StateCS.IDLE)

    var sizeCS by mutableStateOf(0f)

    val widthArcs by derivedStateOf {
        if (selectedTarget == StateCS.IDLE)
            30f
        else
            10f
    }

    var radius by mutableStateOf(0f)

    var startOffsetCS by mutableStateOf(DpOffset.Zero)

    var selectedTargetOffset by mutableStateOf(Offset.Zero)

}