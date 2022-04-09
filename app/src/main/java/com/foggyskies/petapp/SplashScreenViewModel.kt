package com.foggyskies.petapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenViewModel: ViewModel() {

    var isLoading by mutableStateOf(true)

    init {
        viewModelScope.launch {
            delay(3000)
            isLoading = true
        }
    }

}