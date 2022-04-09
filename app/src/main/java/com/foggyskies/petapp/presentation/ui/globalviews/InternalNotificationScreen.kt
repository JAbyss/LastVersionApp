package com.foggyskies.petapp.presentation.ui.globalviews

import SegmentProgressBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.Notification
import com.foggyskies.petapp.NotificationWithVisilble
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt

@Composable
fun IndicatorLine(
    viewModel: MainSocketViewModel,
    item: NotificationWithVisilble,
    timer: HashMap<NotificationWithVisilble, MutableState<Int>>
) {

    val TIMER_MAX = 1000

    fun reset() {
        timer[item]?.value = TIMER_MAX
    }

    LaunchedEffect(key1 = viewModel.listNotifications.size) {
//        while (timer[item]?.value != 0) {
//            timer[item]?.value = timer[item]?.value?.minus(1)!!
//            delay(10)
//        }
        if (timer[item]?.value == 0) {
            item.isVisible.value = false
            delay(300)
            viewModel.listNotifications.remove(item)
            timer.remove(item)
//            viewModel.de
        }
        reset()
    }

    Canvas(modifier = Modifier.fillMaxWidth()) {

        val width = derivedStateOf {
            (size.width / TIMER_MAX) * timer[item]?.value!!
        }

        drawLine(
            color = Color(0xFFFFAB00),
            strokeWidth = 10f,
            cap = StrokeCap.Round,
            start = Offset(0f, 0f),
            end = Offset(width.value, 0f),
        )
    }
}

@Composable
fun OneNotificationMessage(
    msViewModel: MainSocketViewModel,
    item: NotificationWithVisilble,
    nav_controller: NavHostController,
    timerMap: HashMap<NotificationWithVisilble, MutableState<Int>>
) {

    Box(
        modifier = Modifier
            .clickable {
                val json = Json.encodeToString(item.toFormattedChat())
                nav_controller.navigate("Chat/$json")
            }
    ) {
        Column(
            modifier = Modifier
//                .clip(RoundedCornerShape(15.dp))
                .align(Alignment.CenterStart)
        ) {
            Row() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Spacer(modifier = Modifier.width(15.dp))
                    if (item.image != "")
                        Image(
                            painter = rememberImagePainter(data = item.image),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .clip(CircleShape)
                                .size(70.dp)
                        )
                    else
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(vertical = 7.dp)
                                .clip(CircleShape)
                                .size(45.dp)
                                .background(Color(0xFFC4E9FB))
                        ) {
                            Text(
                                text = item.title[0].toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF20B6F6)
                            )
                        }

                    Spacer(modifier = Modifier.width(20.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = item.description,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            IndicatorLine(msViewModel, item, timerMap)
        }
    }
}

data class AnimationClass(
    var item: Notification,
    var isVisible: MutableState<Boolean>
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InternalNotificationScreen(
    viewModel: HomeViewModel,
    modifier: Modifier,
    msViewModel: MainSocketViewModel,
    nav_controller: NavHostController
) {

    val width = 96.dp
    val squareSize = 480.dp

    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { -squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states

    val context = LocalContext.current


    val item = Notification(
        id = "wdwaf",
        idUser = "fawfwfafwf",
        title = "3235fawf",
        description = "Привет",
        image = "faf",
        status = "hthrt"
    )

    data class OldListInfo<T>(
        var equalItemsList: MutableList<T>,
        var newItemsList: MutableList<T>,
        var depricatedItemsList: MutableList<T>
    )

    fun <T> checkOldListByNewList(
        oldList: MutableList<T>,
        newList: MutableList<T>
    ): OldListInfo<T> {
        val equalItemsList = mutableListOf<T>()
        val newItemsList = mutableListOf<T>()
        val depricatedItemsList = mutableListOf<T>()

        newList.forEach { item ->
            if (oldList.contains(item)) {
                equalItemsList.add(item)
            } else {
                newItemsList.add(item)
            }
        }
        if (oldList != equalItemsList) {
            depricatedItemsList.addAll(oldList - newList)
        }
        val result = OldListInfo(
            newItemsList = newItemsList,
            equalItemsList = equalItemsList,
            depricatedItemsList = depricatedItemsList
        )

        return result
    }

    Box(
        modifier = modifier
            .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
        ) {

            val map_queue = remember { hashMapOf<NotificationWithVisilble, MutableState<Int>>() }

            if (msViewModel.listNotifications.isNotEmpty()) {
                if (!map_queue.containsKey(msViewModel.listNotifications[0])) {
                    val timer = remember {
                        mutableStateOf(1000)
                    }
                    map_queue[msViewModel.listNotifications[0]] = timer
                }
                AnimatedVisibility(
                    visible = msViewModel.listNotifications[0].isVisible.value
                ) {
                    OneNotificationMessage(
                        msViewModel,
                        msViewModel.listNotifications[0],
                        nav_controller,
                        map_queue
                    )
                }
            }
            if (msViewModel.listNotifications.size >= 2) {
                if (!map_queue.containsKey(msViewModel.listNotifications[1])) {
                    val timer = remember {
                        mutableStateOf(1000)
                    }
                    map_queue[msViewModel.listNotifications[1]] = timer
                }
                AnimatedVisibility(
                    visible = msViewModel.listNotifications[1].isVisible.value
                ) {
                    OneNotificationMessage(
                        msViewModel,
                        msViewModel.listNotifications[1],
                        nav_controller,
                        map_queue
                    )
                }
            }
            if (msViewModel.listNotifications.size >= 3) {
                if (!map_queue.containsKey(msViewModel.listNotifications[2])) {
                    val timer = remember {
                        mutableStateOf(1000)
                    }
                    map_queue[msViewModel.listNotifications[2]] = timer
                }
                AnimatedVisibility(
                    visible = msViewModel.listNotifications[2].isVisible.value
                ) {
                    OneNotificationMessage(
                        msViewModel,
                        msViewModel.listNotifications[2],
                        nav_controller,
                        map_queue
                    )
                }
            }
        }
        IconButton(
            onClick = {
                      msViewModel.isMuteBarVisible = !msViewModel.isMuteBarVisible
            },
            modifier = Modifier
                .offset(y = 40.dp)
                .align(Alignment.BottomEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_notify_off),
                contentDescription = null,
                modifier = Modifier
                    .size(27.dp)
            )
        }

        AnimatedVisibility(
            visible = msViewModel.isMuteBarVisible,
            modifier = Modifier
                .offset(0.dp, 50.dp)
                .align(Alignment.BottomCenter)
        ) {
            SegmentProgressBar(
                msViewModel.selectedMuteBatItem,
                msViewModel.listValuesMute,
                modifier = Modifier
                    .height(15.dp)
                    .fillMaxWidth()
            )
        }
    }
}