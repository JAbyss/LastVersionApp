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
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.registation.LoginUserDC
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(nav_controller: NavHostController) {

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit, block = {

        delay(500)

        TOKEN = context.getSharedPreferences(
            "Token",
            Context.MODE_PRIVATE
        ).getString("Token", "").toString()

        if (TOKEN.isNullOrBlank()) {
            nav_controller.navigate(NavTree.Authorization.name) {
                popUpTo(NavTree.Splash.name) {
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

            IDUSER = context.getSharedPreferences(
                "User",
                Context.MODE_PRIVATE
            ).getString("idUser", "").toString()
            if (isNetworkAvailable.value) {

                HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
//                    install(ContentNegotiation){
//                        json(Json {
//                            prettyPrint = true
//                            isLenient = true
//                        })
//                    }
//                    install(ContentNegotiation){
//                        json(Json {
//                            prettyPrint = true
//                            isLenient = true
//                        })
//                    }
                    expectSuccess = false
                    install(HttpTimeout) {
                        requestTimeoutMillis = 30000
                    }
                }.use {
                    val response: HttpResponse =
                        it.post("${Routes.SERVER.REQUESTS.BASE_URL}/auth") {
                            headers["Content-Type"] = "Application/Json"
                            body = (LoginUserDC(
                                username = USERNAME,
                                password = PASSWORD
                            ))
                        }
                    if (!response.status.isSuccess()) {
                        nav_controller.navigate(NavTree.Authorization.name) {
                            popUpTo(NavTree.Splash.name) {
                                inclusive = true
                            }
                        }
                    } else {
                        val responseText = response.readText().split("|")
                        TOKEN = responseText[0]
                        IDUSER = responseText[1]
                        context.getSharedPreferences(
                            "Token",
                            Context.MODE_PRIVATE
                        ).edit().putString("Token", TOKEN).apply()
                        context.getSharedPreferences(
                            "User",
                            Context.MODE_PRIVATE
                        ).edit().putString("idUser", IDUSER).apply()
                        nav_controller.navigate(NavTree.Home.name) {
                            popUpTo(NavTree.Splash.name) {
                                inclusive = true
                            }
                        }
                    }
                }
            } else {
                nav_controller.navigate(NavTree.Home.name){
                    popUpTo(NavTree.Splash.name) {
                        inclusive = true
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