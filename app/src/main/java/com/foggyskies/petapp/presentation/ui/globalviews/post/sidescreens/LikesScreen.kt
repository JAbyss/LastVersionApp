package com.foggyskies.petapp.presentation.ui.globalviews.post.sidescreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import com.foggyskies.petapp.presentation.ui.home.PostScreenHandler

@Composable
fun LikesScreen(postScreenHandler: PostScreenHandler) {

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(postScreenHandler.likedUsersList) { index, item ->
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable { }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box() {
                            AsyncImage(
                                model = "http://$MAINENDPOINT/images/${item.image}",
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(45.dp)
                            )
                            Canvas(
                                modifier = Modifier
                                    .offset(x = (-5).dp, y = (-5).dp)
                                    .align(Alignment.BottomEnd)
                            ) {
                                drawCircle(
                                    color = Color.White,
                                    radius = 26f,
                                )
                                drawCircle(
                                    color = Color.Gray,
                                    radius = 15f,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = item.username,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (index != postScreenHandler.likedUsersList.lastIndex)
                    Divider(
                        color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .fillMaxWidth(0.8f)
                    )
            }
        }
        AnimatedVisibility(
            visible = postScreenHandler.likedUsersList.isEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Лайки отсутствуют",
                fontSize = 20.sp
            )
        }
    }
}