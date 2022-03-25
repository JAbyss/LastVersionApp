package com.foggyskies.petapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.network.ConnectionLiveData
import com.foggyskies.petapp.presentation.ui.adhomeless.AdsHomelessScreen
import com.foggyskies.petapp.presentation.ui.adhomeless.AdsHomelessViewModel
import com.foggyskies.petapp.presentation.ui.chat.ChatScreen
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.home.HomeScreen
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import com.foggyskies.petapp.presentation.ui.profile.ProfileScreen
import com.foggyskies.petapp.presentation.ui.profile.human.HumanProfileViewModel
import com.foggyskies.petapp.presentation.ui.registation.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.splash.SplashScreen
import com.foggyskies.testingscrollcompose.presentation.ui.registation.AuthorizationScreen

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    @ExperimentalCoilApi
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, PushNotificationService::class.java).also{
            startService(it)
        }

        setContent {
//            PetAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    LoadingApp()
//                }
            }
        }
    }

    companion object{
        var USERNAME = ""
        var TOKEN = ""
//        194.67.93.244:8089
//        192.168.0.28
//        26.228.47.11
//        94.41.84.183:2526
//        192.168.0.199:2525
        val MAINENDPOINT = "94.41.84.183:2526"
        lateinit var isNetworkAvailable: State<Boolean>
    }

}

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun LoadingApp() {

    val nav_controller = rememberNavController()

    val context = LocalContext.current

    val window: Window = (context as Activity).window
    val statusBarColor: Int = Color(0xFFC2C8CC).toArgb()
    window.statusBarColor = statusBarColor

    val connection_live_data = ConnectionLiveData(context)
    isNetworkAvailable = connection_live_data.observeAsState(false)

    val viewModelProvider = ViewModelProvider(context as ComponentActivity)

    NavHost(navController = nav_controller, startDestination = "Splash"){
        composable("Splash"){
            SplashScreen(nav_controller = nav_controller)
        }
        composable("Authorization") {
            val viewModel = viewModelProvider["AuthorizationViewModel", (AuthorizationViewModel::class.java)]

            viewModelProvider["HomeViewModel", (HomeViewModel::class.java)].onCleared()

            AuthorizationScreen(nav_controller, viewModel)
        }
        composable("Home") {
            val viewModel = viewModelProvider["HomeViewModel", (HomeViewModel::class.java)]

            viewModel.HomeScreen(nav_controller)
        }
        composable("AdsHomeless") {
            val viewModel = viewModelProvider["AdsHomelessViewModel", (AdsHomelessViewModel::class.java)]

            AdsHomelessScreen(nav_controller, viewModel = viewModel)
        }
        composable("Chat") {
            val item: FormattedChatDC =
                it.arguments?.get("itemChat") as FormattedChatDC

            val viewModel = viewModelProvider["ChatViewModel", (ChatViewModel::class.java)]
            ChatScreen(viewModel, item)
        }
        composable("Profile") {
            val viewModel = viewModelProvider["ProfileViewModel", (HumanProfileViewModel::class.java)]
            val viewModelHome = viewModelProvider["HomeViewModel", (HomeViewModel::class.java)]


            ProfileScreen(nav_controller = nav_controller, viewModel, viewModelHome)
        }
    }
}