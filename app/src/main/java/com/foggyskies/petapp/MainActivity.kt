package com.foggyskies.petapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.annotation.ExperimentalCoilApi
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.PushNotificationService.Companion.ISAPPLIFE
import com.foggyskies.petapp.PushNotificationService.Companion.notificationsList
import com.foggyskies.petapp.network.ConnectionLiveData
import com.foggyskies.petapp.presentation.ui.adhomeless.AdsHomelessScreen
import com.foggyskies.petapp.presentation.ui.adhomeless.AdsHomelessViewModel
import com.foggyskies.petapp.presentation.ui.chat.ChatScreen
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.home.HomeScreen
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import com.foggyskies.petapp.presentation.ui.onboard.OnBoardScreen
import com.foggyskies.petapp.presentation.ui.profile.ProfileScreen
import com.foggyskies.petapp.presentation.ui.profile.human.HumanProfileViewModel
import com.foggyskies.petapp.presentation.ui.registation.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.splash.SplashScreen
import com.foggyskies.testingscrollcompose.presentation.ui.registation.AuthorizationScreen
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    @ExperimentalCoilApi
    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ISAPPLIFE = true

//        if (!LIFESERVICE){
//            Intent(this, PushNotificationService::class.java).also {
//                startService(it)
//                super.onPause()
//            }
//            Log.e("SERVISE STARTED", "dsssssssssssssss $LIFESERVICE")
//        }
//        val splashviewModel = SplashScreenViewModel()
//
//        installSplashScreen()
//            .setKeepOnScreenCondition{
//            !splashviewModel.isLoading
//        }


        setContent {
//            PetAppTheme {
            // A surface container using the 'background' color from the theme
            Surface(color = MaterialTheme.colors.background) {
                LoadingApp()
//                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ISAPPLIFE = false
        Log.e("FLAGGGGGGG", "SETTTTTTTTTTTTTTTTTTTTTTT $ISAPPLIFE")
    }
//    override fun onPause() {
//
//        super.onPause()
//    }

    companion object {
        var USERNAME = ""
        var TOKEN = ""

        /**        194.67.93.244:8089
        192.168.0.28

        26.228.47.11

        94.41.84.183:2526

        192.168.0.88:2525
         */

        val MAINENDPOINT = "192.168.0.11:2525"
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
    val uri = "https://www.example.com"
    val nav_controller = rememberNavController()

    val context = LocalContext.current

    val window: Window = (context as Activity).window
    val statusBarColor: Int = Color(0xFFC2C8CC).toArgb()
    window.statusBarColor = statusBarColor

    val connection_live_data = ConnectionLiveData(context)
    isNetworkAvailable = connection_live_data.observeAsState(false)

    val viewModelProvider = ViewModelProvider(context as ComponentActivity)

    val mainSocketViewModel =
        viewModelProvider["MainSocketViewModel", (MainSocketViewModel::class.java)]

    NavHost(navController = nav_controller, startDestination = "Splash") {
        composable("Splash") {
            SplashScreen(nav_controller = nav_controller)
        }
        composable("Authorization") {
            val viewModel =
                viewModelProvider["AuthorizationViewModel", (AuthorizationViewModel::class.java)]

//            viewModelProvider["HomeViewModel", (HomeViewModel::class.java)]

            AuthorizationScreen(nav_controller, viewModel)
        }
        composable("Home") {
            val viewModel = viewModelProvider["HomeViewModel", (HomeViewModel::class.java)]

            if (mainSocketViewModel.mainSocket == null)
                mainSocketViewModel.createMainSocket()

            viewModel.HomeScreen(nav_controller, mainSocketViewModel)
        }
        composable("AdsHomeless") {
            val viewModel =
                viewModelProvider["AdsHomelessViewModel", (AdsHomelessViewModel::class.java)]

            AdsHomelessScreen(nav_controller, viewModel = viewModel)
        }
        composable(
            "Chat/{itemChat}",
            arguments = listOf(navArgument("itemChat") {
                type = NavType.StringType
            }),
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/itemChat={itemChat}token={token}" })
        ) {
//            it.arguments?.getString("itemChat")
            val Token = it.arguments?.getString("token")
            if (Token != null) {
                TOKEN = Token
                USERNAME = context.getSharedPreferences(
                    "User",
                    Context.MODE_PRIVATE
                ).getString("username", "").toString()
                mainSocketViewModel.sendAction("deleteAllSentNotifications|")
                notificationsList = mutableListOf()
                Log.e("ARGS", "Эксперимент удался я не нул")
            } else {
                Log.e("ARGS", "Все хуйня давай поновой")
            }

            val str = it.arguments?.getString("itemChat") as String
            val item = Json.decodeFromString<FormattedChatDC>(str)

            val viewModel = viewModelProvider["ChatViewModel", (ChatViewModel::class.java)]
            ChatScreen(viewModel, item)
        }
        composable("Profile") {
            val viewModel =
                viewModelProvider["ProfileViewModel", (HumanProfileViewModel::class.java)]
            val viewModelHome = viewModelProvider["HomeViewModel", (HomeViewModel::class.java)]

            ProfileScreen(
                nav_controller = nav_controller,
                viewModel,
                viewModelHome,
                mainSocketViewModel
            )
        }
//        composable("OnBoard"){
//            OnBoardScreen()
//        }
    }

//    DisposableEffect(key1 = Unit) {
//        onDispose {
//            mainSocketViewModel.onCleared()
//        }
//    }
}