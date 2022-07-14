package com.foggyskies.petapp.presentation.ui.chat

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.customui.*
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.globalviews.FullScreenImage
import com.foggyskies.petapp.presentation.ui.profile.human.MENUS
import com.foggyskies.petapp.presentation.ui.profile.human.views.ClosedComposedFun
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@ExperimentalMaterialApi
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    item: FormattedChatDC,
    msViewModel: MainSocketViewModel,
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

    val state by remember(viewModel.state.value) {
        val messages = viewModel.state.value
        mutableStateOf(messages)
    }

    DisposableEffect(key1 = Unit){
        onDispose {
            viewModel.disconnect()
        }
    }

//    DisposableEffect(key1 = lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_START) {
//                viewModel.connectToChat(item.id, context)
//            } else if (event == Lifecycle.Event.ON_STOP) {
////                viewModel.disconnect()
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
    LaunchedEffect(key1 = Unit){
        viewModel.connectToChat(item.id, context)
    }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            viewModel.galleryHandler!!.selectedItems = emptyList()
            viewModel.galleryHandler!!.listPath = emptyList()
            viewModel.stateTextField = StateTextField.EMPTY
        }
    }

    SideEffect {
        Log.e("MODAL BOTTOM ВЫШЕ", "Утечка")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color(0xFFF8F8FF))
            .background(Color(0xFFFFFFFF))
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
                        reverseLayout = true,
                        modifier = Modifier
                            .fillMaxWidth()
//                        .height(kotlin.run {
//
//                            return@run viewModel.heightChat
//                        })
                            .align(BottomCenter)
                    ) {
                        stickyHeader {
                            var isActive = remember {
                                mutableStateOf(true)
                            }
                            LaunchedEffect(key1 = Unit) {
                                delay(2000)
                                isActive.value = false
                            }
//                                val composition = currentRecomposeScope
                            Text(text = "nowVisible: ${lazy_state.firstVisibleItemIndex}, max: ${lazy_state.layoutInfo.totalItemsCount}")
                            if (lazy_state.layoutInfo.visibleItemsInfo.isNotEmpty())
                                if (lazy_state.layoutInfo.totalItemsCount > 99)
                                    if (lazy_state.layoutInfo.visibleItemsInfo.last().index in state.messages.lastIndex - 70..state.messages.lastIndex) {
//                                    LaunchedEffect(key1 = Unit){
                                        if (!isActive.value) {
                                            Log.e("LOADING Messages", "START")
                                            isActive.value = true
                                            viewModel.loadNextMessages(
                                                state.messages.last().id,
                                                isActive
                                            )
                                        }
//                                    }
                                    } else if (lazy_state.layoutInfo.totalItemsCount > 120 && lazy_state.firstVisibleItemIndex < 5) {
                                        viewModel.clearMessages()
                                    }
                        }
//                            ClosedComposedFun {
//
//                            }

                        items(state.messages) { item ->

                            Message(
                                message = item,
                                viewModel,
                                msViewModel = msViewModel,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                                    .fillMaxWidth()
                                    .wrapContentWidth(if (item.idUser == IDUSER) End else Start)
                            )

                        }
                    }
                }
            }
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
//        }
        ClosedComposedFun {
            val animated =
                animateDpAsState(targetValue = derivedStateOf { if (sheetState.targetValue == ModalBottomSheetValue.Expanded) 0.dp else 15.dp }.value)

            ModalBottomSheetLayout(
                sheetShape = RoundedCornerShape(topStart = animated.value, topEnd = animated.value),
                sheetContent = {
                    SideEffect {
                        Log.e("MODAL BOTTOM", "Утечка")
                    }
                    if (viewModel.bottomSheetState)
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
                            bottomSheetState = sheetState,
                        )
                    else
                        ContentFilesBottomSheet(
                            viewModel = viewModel,
                            bottomSheetState = sheetState
                        )
                },
                sheetState = sheetState,
            ) {
            }
        }

        AnimatedVisibility(
            visible = viewModel.menuHelper.getMenuVisibleValue(MENUS.ATTACH).value,
            modifier = Modifier
//                .padding(end = 10.dp)
                .offset(x = (-10).dp, y = (-80).dp)
                .align(BottomEnd)
        ) {
            AttachMenuChat(viewModel, sheetState)
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
                sheetState,
            )
        }
    }
}