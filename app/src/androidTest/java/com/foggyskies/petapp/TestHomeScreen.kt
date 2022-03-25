package com.foggyskies.petapp

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.foggyskies.petapp.presentation.ui.home.HomeScreen
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import org.junit.Rule
import org.junit.Test

class TestHomeScreen {

    @get:Rule
    val composeTestRule = createComposeRule()


    @OptIn(ExperimentalFoundationApi::class)
    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @Test
    fun MyTest() {
        composeTestRule.setContent {

            val context = LocalContext.current
            val viewModelProvider = ViewModelProvider(context as ComponentActivity)
            val viewModel = viewModelProvider["HomeViewModel", (HomeViewModel::class.java)]

            val nav_controller = rememberNavController()

            HomeScreen(nav_controller = nav_controller, viewModel = viewModel)


        }

        composeTestRule.onNodeWithTag("clickBTN0").performClick()
        composeTestRule.onNodeWithTag("Photos").assertIsDisplayed()
    }
}