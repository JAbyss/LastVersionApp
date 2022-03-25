package com.foggyskies.petapp.presentation.ui.home.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys
import com.foggyskies.testingscrollcompose.extendfun.forEachKeysNotCompose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalAnimationApi
@Composable
fun BoxScope.RightMenu(
    onClick: (String) -> Unit
) {

    val display_metrics = LocalContext.current.resources.displayMetrics

    val mapVisibility = remember {
        mutableStateMapOf<String, Boolean>()
    }

    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .fillMaxHeight(0.5f)
            .align(Alignment.CenterEnd)
    ) {

        val menuItems = mapOf(
            "Пользователи" to R.drawable.ic_sleep,
            "Беседы" to R.drawable.ic_sleep,
            "Друзья" to R.drawable.ic_sleep,
            "Настройки" to R.drawable.ic_sleep,
            "Стройки" to R.drawable.ic_sleep,
            "Питомцы" to R.drawable.ic_sleep,
            "Прочее" to R.drawable.ic_sleep
        )

        menuItems.forEachKeys { key, value, index ->
            if (mapVisibility.contains(key)){

            }else{
                mapVisibility[key] = false
            }
            AnimatedVisibility(
                visible = mapVisibility[key]!!,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally()
            ) {
                OneMenuItem(
                    value = key,
                    icon = value,
                    maxWidth = maxWidth,
                    maxHeight = maxHeight,
                    density = display_metrics.density,
                    index = index,
                    count = menuItems.size,
                    onClick = {
                        onClick(it)
                    }
                )
            }
        }
    }

    var list = listOf(
        "Пользователи",
        "Беседы",
        "Друзья",
        "Настройки",
        "Стройки",
        "Питомцы",
        "Прочее",
    )
    scope.launch {
        list.forEach { value ->
                mapVisibility[value] = true
            delay(100)
        }
    }
}

@Composable
fun OneMenuItem(
    value: String,
    icon: Int,
    maxWidth: Dp,
    maxHeight: Dp,
    density: Float,
    index: Int,
    count: Int,
    onClick: (String) -> Unit
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

    var width by remember {
        mutableStateOf(0)
    }

    var height by remember {
        mutableStateOf(0)
    }

    val halfWidth by remember {
        derivedStateOf {
            (width / 2 / density).dp
        }
    }

    fun getFirstHalf(count: Int): IntRange {

        return 1..(count / 2)
    }

    val offset by remember {
        derivedStateOf {

            when (index) {
                0 -> DpOffset(x = maxWidth - (width / density).dp, y = 0.dp)
                count - 1 -> DpOffset(
                    x = maxWidth - (width / density).dp,
                    y = (maxHeight / count * index)
                )
                count / 2 -> {
                    if (count % 2 == 0) {
                        DpOffset(0.dp, (maxHeight / count * index))
                    } else {
                        val multiplierY = (maxHeight / count * index)
                        val multiplierX = (maxWidth / (count / 2) / index.toFloat()) - halfWidth / 2
                        DpOffset(x = multiplierX, y = multiplierY)
                    }
                }
                in 1..(count / 2) -> {
                    val multiplierY = (maxHeight / count * index)
                    val multiplierX = (maxWidth / (count / 2) / index.toFloat()) - halfWidth / 2
                    DpOffset(x = multiplierX, y = multiplierY)
                }
                in (count / 2) until count -> {
                    Log.e("ggggggggggg", index.toString())

                    val multiplierY = (maxHeight / count * index)
                    val multiplierX =
                        (maxWidth.times(index.toFloat() / count.toFloat()) - (width / density).dp)
                    DpOffset(x = multiplierX, y = multiplierY)
                }
                else -> {
                    DpOffset(x = 0.dp, y = 0.dp)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .offset(x = offset.x, y = offset.y)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF23262F))
            .onSizeChanged {
                width = it.width
                height = it.height
            }
            .clickable(onClick = {onClick(value)})
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
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