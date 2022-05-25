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
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.annotation.ExperimentalCoilApi
import coil.compose.*
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.customui.ChatTextField
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.globalviews.FullScreenImage
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    item: FormattedChatDC,
) {

    viewModel.chatEntity = item

    val lifecycleOwner = LocalLifecycleOwner.current
    val display_metrics = LocalContext.current.resources.displayMetrics
    val context = LocalContext.current

//    fun showSoftKeyboard() {
//        if (view.requestFocus()) {
//            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
//        }
//    }

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

//    BottomSheetScaffold(sheetContent = ]) {
//
//    }
    val state by remember(viewModel.state.value) {
        val messages = viewModel.state.value
        if (messages.messages.isNotEmpty()) {
            if (!messages.isLoading) {
                scope.launch {

                    lazy_state.animateScrollToItem(
                        messages.messages.size - 1,
//                        lazy_state.layoutInfo.visibleItemsInfo.last().offset
                    )
                }
                messages.isLoading = true
            }
            if (messages.messages.first().author == USERNAME) {
                scope.launch {
                    lazy_state.animateScrollToItem(
                        messages.messages.size - 1,
//                        lazy_state.layoutInfo.visibleItemsInfo.last().offset
                    )
                }
            }
            if (lazy_state.layoutInfo.visibleItemsInfo.isNotEmpty())
                if (lazy_state.layoutInfo.visibleItemsInfo.last().index == messages.messages.lastIndex - 1) {
                    scope.launch {
                        lazy_state.animateScrollToItem(
                            messages.messages.size - 1,
//                            lazy_state.layoutInfo.visibleItemsInfo.last().offset
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
                viewModel.connectToChat(item.id, context)
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            viewModel.galleryHandler!!.selectedItems = emptyList()
            viewModel.stateTextField = StateTextField.EMPTY
        }
    }

    val keyBoard = LocalSoftwareKeyboardController.current
    SideEffect {
        Log.e("MODAL BOTTOM ВЫШЕ", "Утечка")
    }

    Box(modifier = Modifier.fillMaxSize()) {

        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            sheetContent = {
                SideEffect {
                    Log.e("MODAL BOTTOM", "Утечка")
                }
//                Box(modifier = Modifier.height(100.dp).fillMaxWidth()) {
//
//                }
                DisposableEffect(key1 = sheetState.isVisible) {
                    onDispose {
                        if (!sheetState.isVisible)
                            keyBoard?.show()
                    }
                }
//                Box() {

                viewModel.galleryHandler!!.GalleryImageSelector(
                    listItems = viewModel.galleryHandler!!.listPath,
                    stateSheet = sheetState,
                    onSelectedImage = {
                        if (viewModel.galleryHandler!!.selectedItems.isNotEmpty())
                            viewModel.stateTextField = StateTextField.WRITING
                        else
                            viewModel.stateTextField = StateTextField.EMPTY
                    },
                    chatMode = true,
                    isManySelect = true,
                )
//                }
//                viewModel.galleryHandler.apply {
//                    GalleryImageSelector(
////                        stateSheet = sheetState,
////                        onSelectedImage = {
//////                            if (selectedItems.isNotEmpty())
//////                                viewModel.stateTextField = StateTextField.WRITING
//////                            else
//////                                viewModel.stateTextField = StateTextField.EMPTY
////                        },
//                        chatMode = true,
//                        isManySelect = true,
//                    )
//                }

            },
            sheetState = sheetState,
        ) {
            SideEffect {
                Log.e("IN                 MODAL BOTTOM", "Утечка")
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
                                    viewModel,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp, vertical = 7.dp)
                                        .fillMaxWidth()
                                        .wrapContentWidth(if (item.author == USERNAME) End else Start)
                                )
                            }
                        }
                    }
                }
//                BottomAppBar(
//                    viewModel,
//                    Modifier
//                        .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
//                        .background(Color(0xFFDAE0E4))
//                        .fillMaxWidth()
//                        .heightIn(max = 80.dp)
//                        .align(Alignment.BottomCenter),
//                    lazy_state,
//                    sheetState,
//                )
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
                AnimatedVisibility(
                    visible = viewModel.selectedImage != null,
                    modifier = Modifier
                        .align(Center)
                ) {
                    FullScreenImage(
                        viewModel.selectedImage
                    ) {
                        viewModel.selectedImage = null
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = viewModel.selectedImage == null, modifier = Modifier.align(
                BottomCenter
            )
        ) {

            BottomAppBar(
                viewModel,
                Modifier
                    .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
                    .background(Color(0xFFDAE0E4))
                    .fillMaxWidth()
                    .heightIn(max = 80.dp)
                    .align(Alignment.BottomCenter),
                lazy_state,
                sheetState,
            )
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
            AsyncImage(
                model = "http://$MAINENDPOINT/${item.image}",
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

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun BottomAppBar(
    viewModel: ChatViewModel,
    modifier: Modifier,
    lazy_state: LazyListState,
    state: ModalBottomSheetState,
) {

    var value by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val keyboard = LocalSoftwareKeyboardController.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                viewModel.heightBottomAppBar = it.height
                viewModel.changeStateChatToVisible()
            }
    ) {

        Row(
            verticalAlignment = Alignment.Bottom,
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
//            val isDowned by remember {
//                derivedStateOf {
//                    if (lazy_state.layoutInfo.visibleItemsInfo.isNotEmpty())
//                        lazy_state.layoutInfo.visibleItemsInfo.last().index == lazy_state.layoutInfo.totalItemsCount - 1
//                    else false
//                }
//            }
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
//                        if (isDowned)
//                            if (lazy_state.layoutInfo.totalItemsCount != 0)
//                                scope.launch {
//                                    try {
//                                        delay(300)
//                                        lazy_state.animateScrollToItem(
//                                            lazy_state.layoutInfo.totalItemsCount - 1,
//                                            lazy_state.layoutInfo.visibleItemsInfo.last().offset
//                                        )
//                                    } catch (e: Exception) {
//                                        e.printStackTrace()
//                                    }
//                                }
                    }
            )
            Button(
                onClick = {
                    if (viewModel.stateTextField == StateTextField.WRITING) {
                        if (viewModel.galleryHandler!!.listPath.isNotEmpty()) {
                            viewModel.addImageToMessage(value) {
                                scope.launch {
                                    state.hide()
                                }
                            }
                        } else {
                            if (!value.isBlank()) {
                                val formattedString =
                                    value.replace(regex = "(^\\s+)|(\\s+\$)".toRegex(), "")
                                viewModel.sendMessage(MessageDC(message = formattedString))
                            }
                        }
                        value = ""
                        viewModel.stateTextField = StateTextField.EMPTY
                    } else {
                        keyboard?.hide()
                        viewModel.galleryHandler!!.getCameraImages(context)
                        scope.launch {
                            state.show()
                        }
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

@OptIn(ExperimentalCoilApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Message(
    message: ChatMessage,
    viewModel: ChatViewModel,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    BoxWithConstraints(
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFDAE0E4))
                .requiredWidthIn(max = maxWidth * 0.75f)
        ) {


            if (message.listImages.size == 1) {
                val imageLink = "http://$MAINENDPOINT/${message.listImages[0]}"

                val cached = loader.diskCache?.get(message.listImages[0])?.data
                val request = if (cached != null) {
                    ImageRequest.Builder(context)
                        .data(cached.toFile())
                        .size(100, 100)
                        .diskCachePolicy(CachePolicy.READ_ONLY)
                        .diskCacheKey(message.listImages[0])
                        .crossfade(true)
                        .build()
                } else {
                    ImageRequest.Builder(context)
                        .data(imageLink)
                        .size(100, 100)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(message.listImages[0])
                        .crossfade(true)
                        .build()
                }
                loader.enqueue(request)

                AsyncImage(
                    model = request,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(bottom = if (message.message == "") 0.dp else 7.dp)
                        .requiredHeightIn(max = 300.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectedImage = SelectedImageMessage(
                                imageRequest = request,
                                message = message
                            )
                        }
                )
            } else if (message.listImages.size > 1) {
//
                message.listImages.windowed(2, 2, true).forEach {
                    Row(Modifier.padding(7.dp)) {
                        val imageLink = "http://$MAINENDPOINT/${it[0]}"

                        val cached = loader.diskCache?.get(it[0])?.data
                        val request = if (cached != null) {
                            ImageRequest.Builder(context)
                                .data(cached.toFile())
                                .size(100, 100)
                                .diskCachePolicy(CachePolicy.READ_ONLY)
                                .diskCacheKey(it[0])
                                .crossfade(true)
                                .build()
                        } else {
                            ImageRequest.Builder(context)
                                .data(imageLink)
                                .size(100, 100)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .diskCacheKey(it[0])
                                .crossfade(true)
                                .build()
                        }
                        loader.enqueue(request)

                        AsyncImage(
                            model = request,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .requiredHeightIn(max = 100.dp)
                                .weight(1f)
                                .clickable {
                                    viewModel.selectedImage = SelectedImageMessage(
                                        imageRequest = request,
                                        message = message
                                    )
                                }
                        )

                        if (it.size > 1) {
                            Spacer(modifier = Modifier.width(10.dp))

                            val imageLink = "http://$MAINENDPOINT/${it[1]}"

                            val cached = MainActivity.loader.diskCache?.get(it[1])?.data
                            val request = if (cached != null) {
                                ImageRequest.Builder(context)
                                    .data(cached.toFile())
                                    .diskCachePolicy(CachePolicy.READ_ONLY)
                                    .diskCacheKey(it[1])
                                    .size(100, 100)
                                    .crossfade(true)
                                    .build()
                            } else {
                                ImageRequest.Builder(context)
                                    .data(imageLink)
                                    .size(100, 100)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .diskCacheKey(it[1])
                                    .crossfade(true)
                                    .build()
                            }
                            loader.enqueue(request)

                            AsyncImage(
                                model = request,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .requiredHeightIn(max = 100.dp)
                                    .weight(1f)
                                    .clickable {
                                        viewModel.selectedImage = SelectedImageMessage(
                                            imageRequest = request,
                                            message = message
                                        )
                                    }
                            )
                        }
                    }
                }
            }
            if (message.message != "") {
                Text(
                    text = message.message,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                )
                val regexTime = "(?<=г. ).+(?=:)".toRegex()
                val result = regexTime.find(message.date)?.value!!
                Text(
                    text = result,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .align(End)
                )
            }
        }
        if (message.message == "") {
            val regexTime = "(?<=г. ).+(?=:)".toRegex()
            val result = regexTime.find(message.date)?.value!!
            Text(
                text = result,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 7.dp)
                    .align(BottomEnd)
            )
        }
    }
}