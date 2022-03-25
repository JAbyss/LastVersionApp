package com.foggyskies.petapp.presentation.ui.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.customui.ChatTextField
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    item: FormattedChatDC
) {

    val lifecycleOwner = LocalLifecycleOwner.current


    val display_metrics = LocalContext.current.resources.displayMetrics
    val context = LocalContext.current

    val height: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics =
                (context as Activity).windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.bottom - insets.top
        } else {
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                display_metrics.heightPixels - context.resources.getDimensionPixelSize(resourceId)
            } else
                0
        }

    viewModel.height_config = (height / display_metrics.density).toInt()
    viewModel.density = display_metrics.density

    val lazy_state = rememberLazyListState()
    val scope = rememberCoroutineScope()


    val state by remember(viewModel.state.value) {
        val messages = viewModel.state.value
        if (messages.messages.isNotEmpty()) {
            if (!messages.isLoading) {
                scope.launch {
                    lazy_state.animateScrollToItem(
                        messages.messages.size - 1,
                        lazy_state.layoutInfo.visibleItemsInfo.last().offset
                    )
                }
                messages.isLoading = true
            }
            if (messages.messages.first().author == USERNAME) {
                scope.launch {
                    lazy_state.animateScrollToItem(
                        messages.messages.size - 1,
                        lazy_state.layoutInfo.visibleItemsInfo.last().offset
                    )
                }
            }
            if (lazy_state.layoutInfo.visibleItemsInfo.isNotEmpty())
                if (lazy_state.layoutInfo.visibleItemsInfo.last().index == messages.messages.lastIndex - 1) {
                    scope.launch {
                        lazy_state.animateScrollToItem(
                            messages.messages.size - 1,
                            lazy_state.layoutInfo.visibleItemsInfo.last().offset
                        )
                    }
                } else {
                    lazy_state.layoutInfo.visibleItemsInfo.last().index

                    val countUnreadMessage =
                        messages.messages.size - lazy_state.layoutInfo.visibleItemsInfo.last().index - 1
                    viewModel.countUnreadMessage = countUnreadMessage
                    viewModel.visibleButtonDown = true
                }
        }
        mutableStateOf(messages)
    }



    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.connectToChat(item.id)
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxHeight()
            .onSizeChanged {
                viewModel.height_keyboard = (height - it.height)
                if (viewModel.height_keyboard > viewModel.height_config * 0.15) {
                    Log.e("Here", viewModel.height_keyboard.toString());
                    viewModel.changeStateChatToVisible()
                } else {
                    viewModel.height_keyboard = 0
                    viewModel.changeStateChatToHidden()
                }
            }
    ) {
        Column {

            HeaderChat(viewModel, item)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(kotlin.run {

                        return@run viewModel.heightChat
                    })
            ) {

                LazyColumn(
                    state = lazy_state,
                    modifier = Modifier
                        .fillMaxWidth()
//                        .height(kotlin.run {
//
//                            return@run viewModel.heightChat
//                        })
                        .align(BottomCenter)
                ) {
                    items(state.messages.reversed()) { item ->
                        Message(
                            message = item,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(if (item.author == USERNAME) End else Start)
                        )
                    }
                }
            }
        }
        BottomAppBar(
            viewModel,
            Modifier
                .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
                .background(Color(0xFFDAE0E4))
                .fillMaxWidth()
                .heightIn(max = 80.dp)
                .align(Alignment.BottomCenter),
            lazy_state

        )
        AnimatedVisibility(
            visible = viewModel.visibleButtonDown,
            modifier = Modifier
                .padding(end = 30.dp, bottom = 60.dp)
                .align(BottomEnd)
        ) {
            Box {
                IconButton(
                    onClick = {
                        scope.launch {
                            viewModel.visibleButtonDown = false
                            viewModel.countUnreadMessage = 0
                            lazy_state.animateScrollToItem(
                                state.messages.size - 1,
                                lazy_state.layoutInfo.visibleItemsInfo.last().offset
                            )
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .align(Center)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chat_down),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
                Text(
                    text = viewModel.countUnreadMessage.toString(),
                    color = Color.Red,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(TopEnd)
                )
            }
        }
    }
}

@Composable
fun HeaderChat(viewModel: ChatViewModel, item: FormattedChatDC) {

    val back = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDAE0E4))
            .onSizeChanged {
                viewModel.heightHeaderAppBar = it.height
            }
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = {
                back?.onBackPressed()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFDAE0E4)
            ),
            shape = CircleShape,
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .defaultMinSize(0.dp, 0.dp)
                .size(32.dp)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp),
                Color.DarkGray
            )
        }
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
                    text = item.nameChat[0].toString(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF20B6F6)
                )
            }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.nameChat,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun BottomAppBar(
    viewModel: ChatViewModel,
    modifier: Modifier,
    lazy_state: LazyListState
) {

    var value by remember {
        mutableStateOf("")
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                viewModel.heightBottomAppBar = it.height
//                    Log.e("dwadawd", "dwdad")

                viewModel.changeStateChatToVisible()
//                    scope.invalidate()
            }
    ) {

        Row(
            verticalAlignment = Alignment.Bottom,
//            modifier =
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFDAE0E4)
                ),
                shape = CircleShape,
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .padding(5.dp)
                    .defaultMinSize(0.dp, 0.dp)
                    .size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_sentiment_satisfied_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(34.dp)
                )
            }
            val scope = rememberCoroutineScope()
            //FIXME
            val isDowned by remember {
                derivedStateOf {
                    if (lazy_state.layoutInfo.visibleItemsInfo.isNotEmpty())
                        lazy_state.layoutInfo.visibleItemsInfo.last().index == lazy_state.layoutInfo.totalItemsCount - 1
                    else false
                }
            }
//            Spacer(modifier = Modifier.width(5.dp))
            ChatTextField(
                value = value,
                onValueChange = {
                    value = it
                    if (value == "")
                        viewModel.stateTextField = StateTextField.EMPTY
                    else
                        viewModel.stateTextField = StateTextField.WRITING
                },
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(0.8f)
                    .align(CenterVertically)
                    .onFocusEvent {
                        if (isDowned)
                            if (lazy_state.layoutInfo.totalItemsCount != 0)
                                scope.launch {
                                    try {
                                        delay(300)
                                        lazy_state.animateScrollToItem(
                                            lazy_state.layoutInfo.totalItemsCount - 1,
                                            lazy_state.layoutInfo.visibleItemsInfo.last().offset
                                        )
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                    }
                                }
                    }
            )

            Button(
                onClick = {
                    if (viewModel.stateTextField == StateTextField.WRITING) {
                        if (!value.isBlank()) {
                            val formattedString = value.replace(regex = "(^\\s+)|(\\s+\$)".toRegex(), "")
                            viewModel.sendMessage(formattedString)
                        }
                        value = ""
                        viewModel.stateTextField = StateTextField.EMPTY
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFDAE0E4)
                ),
                shape = CircleShape,
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .padding(5.dp)
                    .defaultMinSize(0.dp, 0.dp)
                    .size(36.dp)
            ) {
                AnimatedContent(
                    targetState = viewModel.stateTextField,
                    transitionSpec = {
                        ContentTransform(
                            initialContentExit = shrinkHorizontally(),
                            targetContentEnter = expandHorizontally()
                        )
                    },
                    modifier = Modifier
//                            .rotate(-135f)
                        .size(34.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = if (viewModel.stateTextField == StateTextField.EMPTY)
                                R.drawable.ic_round_attach_file_24
                            else
                                R.drawable.ic_send
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(if (viewModel.stateTextField == StateTextField.EMPTY) -135f else 0f)
                            .size(34.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Message(
    message: ChatMessage,
    modifier: Modifier
) {

    BoxWithConstraints(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFDAE0E4))
                .requiredWidthIn(max = maxWidth * 0.75f)
        ) {
            Text(
                text = message.message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            )
            Text(
                text = message.date,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(End)
            )
        }
    }
}