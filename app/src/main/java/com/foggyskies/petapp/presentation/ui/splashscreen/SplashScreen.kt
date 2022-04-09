package com.foggyskies.petapp.presentation.ui.splash

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.registation.LoginUserDC
import com.foggyskies.petapp.presentation.ui.registation.authorization_save
import com.foggyskies.petapp.presentation.ui.registation.signInRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.use

@Composable
fun SplashScreen(nav_controller: NavHostController) {

    val context = LocalContext.current

    LaunchedEffect(key1 = isNetworkAvailable.value, block = {

        TOKEN = context.getSharedPreferences(
            "Token",
            Context.MODE_PRIVATE
        ).getString("Token", "").toString()

        if (TOKEN.isNullOrBlank()) {
            nav_controller.navigate("Authorization") {
                popUpTo("Splash") {
                    inclusive = true
                }
            }
        } else {

            USERNAME = context.getSharedPreferences(
                "User",
                Context.MODE_PRIVATE
            ).getString("username", "").toString()

            val PASSWORD = context.getSharedPreferences(
                "User",
                Context.MODE_PRIVATE
            ).getString("password", "").toString()
            if (isNetworkAvailable.value) {

                HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                    expectSuccess = false
                    install(HttpTimeout) {
                        requestTimeoutMillis = 3000
                    }
                }.use {
                    val response =
                        it.post<HttpResponse>("http://${MainActivity.MAINENDPOINT}/auth") {
                            headers["Content-Type"] = "Application/Json"
                            body = LoginUserDC(
                                username = USERNAME,
                                password = PASSWORD
                            )
                        }
                    if (!response.status.isSuccess()) {
                        nav_controller.navigate("Authorization") {
                            popUpTo("Splash") {
                                inclusive = true
                            }
                        }
                    } else {
                        TOKEN = response.readText()
                        context.getSharedPreferences(
                            "Token",
                            Context.MODE_PRIVATE
                        ).edit().putString("Token", TOKEN).apply()
                        nav_controller.navigate("Home") {
                            popUpTo("Splash") {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    })

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
                    text = "ZUM      PER",
                    style = TextStyle(
                        fontFamily = FontFamily(
                            Font(
                                R.font.nunito_black,
                                weight = FontWeight.Black
                            )
                        ),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Text(
                text = "Foggy Skies production",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
            )
        }

        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}