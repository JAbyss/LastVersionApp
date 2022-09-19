package com.foggyskies.petapp.presentation.ui.splashscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.foggyskies.petapp.PasswordCoder
import com.foggyskies.petapp.R
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.authorization.models.LoginUserDC
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.splashscreen.requests.auth
import com.foggyskies.petapp.presentation.ui.splashscreen.requests.checkToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(nav_controller: NavHostController) {
    LaunchedEffect(key1 = Unit) {
        delay(500)
        if (MainPreference.Token.isEmpty()) {
            nav_controller.navigate(NavTree.Authorization.name) {
                popUpTo(NavTree.Splash.name) {
                    inclusive = true
                }
            }
        } else
            checkToken(
                onOk = {
//                        CoroutineScope(Main).launch {
                    nav_controller.navigate(NavTree.Home.name) {
                        popUpTo(NavTree.Splash.name) {
                            inclusive = true
                        }
                    }
//                        }
                },
                onError = {
                    this.launch {
                        auth(
                            data = LoginUserDC(
                                username = MainPreference.Username ?: "",
                                password = PasswordCoder.encodeStringFS(MainPreference.Password)
                            ),
                            onOk = {
//                                    CoroutineScope(Main).launch {
                                nav_controller.navigate(NavTree.Home.name) {
                                    popUpTo(NavTree.Splash.name) {
                                        inclusive = true
                                    }
                                }
//                                    }
                            },
                            onError = {
//                                    CoroutineScope(Main).launch {
                                nav_controller.navigate(NavTree.Authorization.name) {
                                    popUpTo(NavTree.Splash.name) {
                                        inclusive = true
                                    }
                                }
//                                    }
                            }
                        )
                    }
                }
            )
    }

    Content()
}

@Composable
private fun Content() {

    Box(
        modifier = Modifier
            .background(Color(0xFF54B175))
            .fillMaxSize()
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
        ) {

            Box() {

                Text(
                    text = "TIXIS",
                    style = TextStyle(
                        fontFamily = FontFamily(
                            Font(
                                R.font.nunito_black,
                                weight = FontWeight.Black
                            )
                        ),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 20.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Text(
                text = "Not Moron production",
                color = Color.White,
                fontSize = 16.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier
            )
        }
    }
}