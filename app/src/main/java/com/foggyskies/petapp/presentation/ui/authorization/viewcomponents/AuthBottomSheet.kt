package com.foggyskies.petapp.presentation.ui.authorization.viewcomponents

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.testingscrollcompose.visualtransformations.PhoneVisualTransformation
import kotlin.collections.set


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BoxScope.AuthBottomSheet(
    nav_controller: NavHostController,
    authorizationViewModel: AuthorizationViewModel
) {

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .align(BottomCenter)
            .background(Color(0xCC382424))
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween())
    ) {

        Column(
            Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.TopCenter)
        ) {

            Spacer(modifier = Modifier.fillMaxHeight(0.03f))
            Text(
                text = authorizationViewModel.top_label,
                fontSize = 26.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.03f))

            authorizationViewModel.list_labels.forEachIndexed { index, value ->

                OutlinedTextField(
                    value = authorizationViewModel.list_fields[value]!!.value,
                    onValueChange = {
                        if (value == "Телефон") {
                            if (it.isDigitsOnly() && it.length < 12)
                                authorizationViewModel.list_fields[value]!!.value = it
                        } else
                            if (value == "Пароль") {
                                val a = it.filterNot { it.code in 33..126 }
                                if (a.isEmpty()) {
                                    authorizationViewModel.list_fields[value]!!.value = it
                                }
                            } else
                                authorizationViewModel.list_fields[value]!!.value = it
                    },
                    label = { Text(text = value, fontWeight = FontWeight.Black) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        textColor = Color.Black,
                        unfocusedLabelColor = Color.White,
                        focusedLabelColor = Color(0xAA71A4D3),
                        cursorColor = Color(0xCC382424)
                    ),
                    visualTransformation =
                    when (value) {
                        "Телефон" -> PhoneVisualTransformation()
                        "Пароль" -> PasswordVisualTransformation()
                        else -> VisualTransformation.None
                    },
                    keyboardOptions = if (value == "Телефон") KeyboardOptions(keyboardType = KeyboardType.Phone) else KeyboardOptions.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (authorizationViewModel.selectedTextField == value)
                                Color.White
                            else
                                Color(0xFF9B9C9D)
                        )
                        .onFocusChanged {

                            when (it.isFocused) {
                                true -> {
                                    authorizationViewModel.selectedTextField = value
                                }
                                false -> {}
                            }
                        }
                )
                if (index != authorizationViewModel.list_labels.lastIndex)
                    Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    authorizationViewModel.executeAction(){
                        nav_controller.navigate(NavTree.Home.name)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF70A4D3)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 46.dp)
            ) {
                Text(text = authorizationViewModel.button_label, color = Color.White)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    authorizationViewModel.changeAuthState()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = Color(0xFF989A9A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 46.dp)
            ) {
                Text(text = authorizationViewModel.button_change_state_label, color = Color.White)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}