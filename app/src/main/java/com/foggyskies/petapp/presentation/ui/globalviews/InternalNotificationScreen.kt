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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.get
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.*
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.routs.Routes
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt

@Composable
fun IndicatorLine(
    viewModel: MainSocketViewModel,
    item: NewMessagesCollectionWA,
    timer: HashMap<NewMessagesCollectionWA, MutableState<Int>>,
    timerMax: Int = 250
) {

    fun reset() {
        timer[item]?.value = timerMax
    }

    LaunchedEffect(key1 = viewModel.listNewMessages.size) {
        while (timer[item]?.value != 0) {
            timer[item]?.value = timer[item]?.value?.minus(1)!!
            delay(10)
        }
        if (timer[item]?.value == 0) {
            item.isVisible.value = false
            delay(300)
            viewModel.listNewMessages.remove(item)
            timer.remove(item)
        }
        reset()
    }

    Canvas(modifier = Modifier.fillMaxWidth()) {

        val width = derivedStateOf {
            (size.width / timerMax) * timer[item]?.value!!
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
    item: NewMessagesCollectionWA,
    nav_controller: NavHostController,
    timerMap: HashMap<NewMessagesCollectionWA, MutableState<Int>>
) {
    Box(
        modifier = Modifier
            .clickable {
                val json = Json.encodeToString(item.new_message.toFC().copy(image = item.image, id = item.id, nameChat = item.username))
                val b = bundleOf("itemChat" to json)
                nav_controller.navigate(nav_controller.graph[NavTree.ChatSec.name].id, b)
            }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            Row {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Spacer(modifier = Modifier.width(15.dp))
                    if (item.image.isNotEmpty())
                        AsyncImage(
                            model = "${Routes.SERVER.REQUESTS.BASE_URL}/${item.image}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .clip(CircleShape)
                                .size(45.dp)
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
                                text = item.username[0].toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF20B6F6)
                            )
                        }

                    Spacer(modifier = Modifier.width(20.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Text(
                            text = item.username,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = item.new_message.message,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
//                        Spacer(modifier = Modifier.height(5.dp))
                        if (item.new_message.listImages.isNotEmpty())
                            Text(
                                text =
                                if (item.new_message.listImages.size == 1)
                                    "Избражение"
                                else
                                    "Изображения: ${item.new_message.listImages.size}",
                                maxLines = 1,
                                color = Color.LightGray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
            IndicatorLine(msViewModel, item, timerMap)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InternalNotificationScreen(
    modifier: Modifier,
    msViewModel: MainSocketViewModel,
    nav_controller: NavHostController
) {
    val squareSize = 480.dp

    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { -squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states

    data class OldListInfo<T>(
        var equalItemsList: MutableList<T>,
        var newItemsList: MutableList<T>,
        var deprecatedItemsList: MutableList<T>
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
            deprecatedItemsList = depricatedItemsList
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

            val map_queue = remember { hashMapOf<NewMessagesCollectionWA, MutableState<Int>>() }

            if (msViewModel.listNewMessages.isNotEmpty()) {
                if (!map_queue.containsKey(msViewModel.listNewMessages[0])) {
                    val timer = remember {
                        mutableStateOf(250)
                    }
                    map_queue[msViewModel.listNewMessages[0]] = timer
                }
                AnimatedVisibility(
                    visible = msViewModel.listNewMessages[0].isVisible.value
                ) {
                    OneNotificationMessage(
                        msViewModel,
                        msViewModel.listNewMessages[0],
                        nav_controller,
                        map_queue
                    )
                }
            }
            if (msViewModel.listNewMessages.size >= 2) {
                if (!map_queue.containsKey(msViewModel.listNewMessages[1])) {
                    val timer = remember {
                        mutableStateOf(250)
                    }
                    map_queue[msViewModel.listNewMessages[1]] = timer
                }
                AnimatedVisibility(
                    visible = msViewModel.listNewMessages[1].isVisible.value
                ) {
                    OneNotificationMessage(
                        msViewModel,
                        msViewModel.listNewMessages[1],
                        nav_controller,
                        map_queue
                    )
                }
            }
            if (msViewModel.listNewMessages.size >= 3) {
                if (!map_queue.containsKey(msViewModel.listNewMessages[2])) {
                    val timer = remember {
                        mutableStateOf(250)
                    }
                    map_queue[msViewModel.listNewMessages[2]] = timer
                }
                AnimatedVisibility(
                    visible = msViewModel.listNewMessages[2].isVisible.value
                ) {
                    OneNotificationMessage(
                        msViewModel,
                        msViewModel.listNewMessages[2],
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
                .offset(0.dp, 100.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
//                    .align(Alignment.Center)
            ) {

                SegmentProgressBar(
                    msViewModel.selectedMuteBatItem,
                    msViewModel.listValuesMute,
                    modifier = Modifier
                        .height(15.dp)
                        .fillMaxWidth()
                )
                IconButton(
                    onClick = {
                        msViewModel.isMuteBarVisible = false
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}