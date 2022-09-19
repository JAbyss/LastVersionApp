package com.foggyskies.petapp.presentation.ui.authorization

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.foggyskies.petapp.R
import com.foggyskies.petapp.extendfun.inRange
import com.foggyskies.petapp.presentation.ui.authorization.viewcomponents.AuthBottomSheet
import com.foggyskies.petapp.presentation.ui.authorization.viewcomponents.CodeWindow
import com.foggyskies.petapp.presentation.ui.globalviews.FullInvisibleBack
import kotlinx.coroutines.delay

@Composable
fun AuthorizationScreen(
    nav_controller: NavHostController,
    authorizationViewModel: AuthorizationViewModel
) {

    Box(modifier = Modifier.fillMaxSize()) {

        BackGroundImage()

        AuthBottomSheet(nav_controller, authorizationViewModel)

        CodeWindowComp(
            authorizationViewModel,
            nav_controller
        )

        ErrorWindowComp(authorizationViewModel)
    }
}

@Composable
fun BoxScope.ErrorWindowComp(viewModel: AuthorizationViewModel) {
    val density = LocalDensity.current.density
    AnimatedVisibility(
        visible = viewModel.errors.isNotEmpty(),
        modifier = Modifier
            .align(TopCenter)
    ) {

        var height by remember {
            mutableStateOf(0.dp)
        }

        LaunchedEffect(key1 = viewModel.errors.isNotEmpty()) {
            if (viewModel.errors.isNotEmpty()){
                delay(10000)
                viewModel.errors.clear()
            }
        }
        var offsetY by remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier
                .offset(y = offsetY.dp)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(Color(0xCC382424))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .onSizeChanged {
                        height = (it.height / density).dp
                    }
            ) {
                LazyColumn(
                    horizontalAlignment = CenterHorizontally,
                    modifier = Modifier
                        .heightIn(max = 150.dp)
                        .align(CenterHorizontally)
                ) {
                    itemsIndexed(viewModel.errors) { index, item ->
                        Text(
                            text = item,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 20.dp
                                )
                        )
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .background(Color.White)
                        .draggable(
                            state = rememberDraggableState { delta ->

                                if (offsetY <= 0) {
                                    if (offsetY + delta <= 0) {
                                        if ((offsetY + height.value).inRange(
                                                10f,
                                                20f
                                            )
                                        ) {
                                            viewModel.errors.clear()
                                            offsetY = 0f
                                        }
                                        if (offsetY < -height.value) {
                                            viewModel.errors.clear()
                                            offsetY = 0f
                                        } else {
                                            offsetY += delta
                                        }
                                    }
                                }
                            },
                            Orientation.Vertical
                        )
                )
            }
        }
    }
}

@Composable
fun BoxScope.CodeWindowComp(
    viewModel: AuthorizationViewModel,
    nav_controller: NavHostController
) {

    AnimatedVisibility(
        visible = viewModel.isCodeGenerated,
        modifier = Modifier.align(Center)
    ) {
        FullInvisibleBack(onBackClick = {
            viewModel.isCodeGenerated = false
        }) {
            CodeWindow(
                viewModel,
                nav_controller
            )
        }
    }
}

@Composable
fun BackGroundImage() {
    Image(
        painter = painterResource(id = R.drawable.back_2),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
    )
}