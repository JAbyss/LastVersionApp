package com.foggyskies.petapp.presentation.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys

@Composable
fun DashBoardComposition(
    viewModel: ProfileViewModel,
    modifier: Modifier
) {

    val list_dashboard = mapOf(
        "Настройки" to R.drawable.ic_privacy,
        "Мои контакты" to R.drawable.ic_link
    )


    Column(modifier = modifier) {
        list_dashboard.forEachKeys { key, value, _ ->

            Button(
                onClick = {
                    if (key == "Мои контакты")
                        viewModel.isMyContactClicked = !viewModel.isMyContactClicked
                },
                enabled = !viewModel.swipableMenu.isMenuOpen,
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (key == "Настройки")
                                    Color(0xFF00C853)
                                else
                                    Color(0xFFFFD600)
                            )
                            .size(50.dp)
                    ) {

                        Icon(
                            painter = painterResource(id = value),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = key,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4F4F4F),
                        modifier = Modifier
                            .align(CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}