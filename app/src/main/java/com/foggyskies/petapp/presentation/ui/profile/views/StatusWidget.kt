package com.foggyskies.petapp.presentation.ui.profile.views

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys
import com.foggyskies.testingscrollcompose.presentation.ui.registation.customui.animation.animateDpOffsetAsState
import kotlinx.coroutines.delay

@Composable
fun StatusWidget(
    index: Int,
    maxWidth: Dp,
    value: String,
    density: Float,
    icon: Int,
    onClick: (String) -> Unit
) {

    var width by remember {
        mutableStateOf(0)
    }

    val halfWidth by remember {
        derivedStateOf {
            (width / 2 / density).dp
        }
    }

    val list_offsets = listOf(
        DpOffset(
            x = ((maxWidth - (width / density).dp) / 2.dp).dp,
            y = 0.dp
        ),
        DpOffset(x = maxWidth.times(0.75f) - halfWidth, y = 60.dp),
        DpOffset(
            x = maxWidth - (width / density).dp,
            y = 120.dp
        ),
        DpOffset(x = maxWidth.times(0.75f) - halfWidth, y = 180.dp),
        DpOffset(
            x = (maxWidth - (width / density).dp) / 2,
            y = 240.dp
        ),
        DpOffset(x = maxWidth.times(0.25f) - halfWidth, y = 60.dp),
        DpOffset(x = 0.dp, y = 120.dp),
        DpOffset(x = maxWidth.times(0.25f) - halfWidth, y = 180.dp)
    )

    var isClicked by remember { mutableStateOf(false) }

    val offsetImage: DpOffset by animateDpOffsetAsState(
        if (isClicked)
            DpOffset(x = ((maxWidth - (width / density).dp) / 2.dp).dp, y = (-40).dp)
        else
            list_offsets[index]
    )

    Box(
        modifier = Modifier
            .offset(x = offsetImage.x, y = offsetImage.y)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF23262F))
            .onSizeChanged {
                width = it.width
            }
            .clickable {
                isClicked = true
                onClick(value)
            }
    ) {

        Row(
            Modifier
                .padding(vertical = 7.dp, horizontal = 10.dp)
                .align(Alignment.Center)
        ) {

            Image(
                painter = painterResource(id = icon),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                value,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun StatusWidget(
    value: String,
    icon: Int,
    onClick: () -> Unit,
    viewModel: ProfileViewModel
) {
    val default_list_status = mapOf(
        "Сплю" to R.drawable.ic_sleep,
        "Занят" to R.drawable.ic_clock,
        "Работаю" to R.drawable.ic_work,
        "Играю" to R.drawable.ic_gamepad,
        "Добавить свое" to R.drawable.ic_add,
        "Чилю" to R.drawable.ic_cool,
        "Ем" to R.drawable.ic_fast_food,
        "Гуляю" to R.drawable.ic_walk,
    )


    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF23262F))
//            .clickable(onClick = onClick)
            .toggleable(
                value = true,
                !viewModel.swipableMenu.isMenuOpen,
                onValueChange = { onClick() })
    ) {

        Row(
            Modifier
                .padding(vertical = 7.dp, horizontal = 10.dp)
                .align(Alignment.Center)
        ) {

            Image(
                painter = painterResource(id = if (default_list_status.containsKey(value)) default_list_status[value]!! else icon),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                value,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

enum class StateCircularStatus {
    CHOICE, ADD
}

@ExperimentalAnimationApi
@Composable
fun BoxScope.CircularStatuses(
    onClickClose: () -> Unit,
    onClickAdd: (String) -> Unit,
    onClickStatus: (String) -> Unit
) {

    val display_metrics = LocalContext.current.resources.displayMetrics

    val scope = rememberCoroutineScope()

    var stateCircularStatus by remember {
        mutableStateOf(StateCircularStatus.CHOICE)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.7f)
            .align(Alignment.Center)
    ) {

        val list_status = mapOf(
            "Сплю" to R.drawable.ic_sleep,
            "Занят" to R.drawable.ic_clock,
            "Работаю" to R.drawable.ic_work,
            "Играю" to R.drawable.ic_gamepad,
            "Добавить свое" to R.drawable.ic_add,
            "Чилю" to R.drawable.ic_cool,
            "Ем" to R.drawable.ic_fast_food,
            "Гуляю" to R.drawable.ic_walk,
        )

        AnimatedVisibility(
            visible = stateCircularStatus == StateCircularStatus.CHOICE,
            enter = expandHorizontally(),
            exit = fadeOut()
        ) {
            list_status.forEachKeys { key, icon, index ->

                var animationVisible by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(
                    key1 = Unit
                ){
                    delay(200 + (index * 100).toLong())
                    animationVisible = true
                }

                AnimatedVisibility(
                    visible = animationVisible,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {

                    StatusWidget(
                        index = index,
                        maxWidth,
                        key,
                        display_metrics.density,
                        icon,
                        onClick = {
                            if (it == "Добавить свое")
                                stateCircularStatus = StateCircularStatus.ADD
                            else
                                onClickStatus(it)
                        }
                    )
                }
            }
        }
        val status = remember { mutableStateOf("") }

        AnimatedVisibility(
            visible = stateCircularStatus == StateCircularStatus.ADD,
            enter = expandHorizontally(),
            exit = fadeOut(),
            modifier = Modifier.offset(
                x = (maxWidth - (maxWidth.times(0.4f) + 20.dp + 7.dp)) / 2,
                y = 120.dp
            )
        ) {
            AddCustomStatus(status)
        }

        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = null,
            modifier = Modifier
                .offset(
                    x = maxWidth / 2 - 17.5.dp,
                    y = if (stateCircularStatus == StateCircularStatus.CHOICE) 120.dp else 60.dp
                )
                .size(35.dp)
                .clickable {
                    if (stateCircularStatus == StateCircularStatus.CHOICE)
                        onClickClose()
                    else
                        stateCircularStatus = StateCircularStatus.CHOICE
                }
        )
        AnimatedVisibility(visible = stateCircularStatus == StateCircularStatus.ADD) {
            Image(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier
                    .offset(
                        x = maxWidth / 2 - 17.5.dp,
                        y = 180.dp
                    )
                    .size(35.dp)
                    .clickable {
                        onClickAdd(status.value)
                    }
            )
        }
    }
}

@Composable
fun BoxScope.AddCustomStatus(status: MutableState<String>) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF23262F))
            .align(Alignment.Center)
    ) {

        Row(
            Modifier
                .padding(vertical = 7.dp, horizontal = 10.dp)
                .align(Alignment.Center)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(7.dp))

            BasicTextField(
                value = status.value,
                onValueChange = {
                    if (it.length < 15)
                        status.value = it
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.4f)
            )
        }
    }
}