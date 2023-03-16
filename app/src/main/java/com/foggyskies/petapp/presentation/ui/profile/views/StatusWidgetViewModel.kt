package com.foggyskies.petapp.presentation.ui.profile.views

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

class StatusWidgetViewModel : ViewModel() {

    var density by mutableStateOf(0f)


    fun setGovno (value: Int){
        widthStatus = value
    }

    var widthStatus by mutableStateOf(0)

    val halfStatus by derivedStateOf {
        (widthStatus / 2 / density).dp
    }

}