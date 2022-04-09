package com.foggyskies.petapp.presentation.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R

@Composable
fun BoxScope.PhotoScreen(
    viewModel: HomeViewModel
) {

    var isLiked by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.73f)
            .background(Color.White)
            .align(Alignment.Center)
    ) {
        var scale by remember { mutableStateOf(1f) }

        var offset by remember { mutableStateOf(Offset.Zero) }

        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            val isLikeAnimation = remember {
                mutableStateOf(false)
            }
            Image(
                painter = painterResource(id = R.drawable.image_dog_for_preview),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = if (scale < 1f) 1f else scale,
                        scaleY = if (scale < 1f) 1f else scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            if (scale != 1f)
                                offset += pan
                            scale *= zoom
                        }
                    }
                    .pointerInput(true) {

                        detectTapGestures(
                            onDoubleTap = {
                                viewModel.doubleTapLike()
                                isLiked = !isLiked
                            },
                            onTap = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            onLongPress = {
                                viewModel.swipableMenu.isReadyMenu = false
                    viewModel.isVisiblePhotoWindow = false
                    viewModel.photoScreenClosed()
                            }
                        )
                    }
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = viewModel.isVisibleLikeAnimation,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.Center)
            ) {

                val maxDimension = 80f
                val animationSpec = tween<Float>(700, easing = FastOutLinearInEasing)

                val animatables = listOf(
                    remember { Animatable(0f) },
                    remember { Animatable(0f) }
                )

                animatables.forEachIndexed { index, animatable ->
                    LaunchedEffect(animatable) {
                        animatable.animateTo(
                            targetValue = 1f,
                            animationSpec = animationSpec
                        )
                    }
                }

                val dys = animatables.map { it.value }
                Box(
                    Modifier.align(Alignment.Center)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .graphicsLayer(alpha = 0.99f)
                    ) {

                        dys.forEach { dy ->
                            if (maxDimension != null) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = maxDimension * dy * 2f,
                                        alpha = 1 - dy
                                    )
                            }
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = viewModel.isStartSecondStepAnimation,
                        modifier = Modifier.align(Alignment.Center)
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_like),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center),
                            Color.White
                        )
                    }
                }

            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(33.dp)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.test_avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "JAbyss",
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(0.35f)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {

                    Button(
                        onClick = {},
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        ),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier
                            .padding(end = 7.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = null,
                            modifier = Modifier,
                            Color.Black
                        )
                    }
                    Button(
                        onClick = {},
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        ),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier
                            .padding(end = 7.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chat_idle),
                            contentDescription = null,
                            modifier = Modifier,
                            Color.Black
                        )
                    }
                    Button(
                        onClick = {
                            isLiked = !isLiked
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        ),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = if (!isLiked) R.drawable.ic_like_not_clicked else R.drawable.ic_like),
                            contentDescription = null,
                            modifier = Modifier,
                            if (isLiked) Color.Red else Color.Black
                        )
                    }
                }
            }
        }
    }
}