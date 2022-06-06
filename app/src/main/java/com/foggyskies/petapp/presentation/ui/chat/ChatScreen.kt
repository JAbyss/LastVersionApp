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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.customui.ChatTextField
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.globalviews.FullScreenImage
import com.foggyskies.petapp.presentation.ui.profile.human.MENUS
import com.foggyskies.petapp.presentation.ui.profile.human.views.ClosedComposedFun
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.workers.UploadWorker
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


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
//        if (messages.messages.isNotEmpty()) {
//            if (!messages.isLoading) {
//                scope.launch {
//
//                    lazy_state.animateScrollToItem(
//                        messages.messages.size - 1,
////                        lazy_state.layoutInfo.visibleItemsInfo.last().offset
//                    )
//                }
//                messages.isLoading = true
//            }
//            if (messages.messages.first().idUser == USERNAME) {
//                scope.launch {
//                    lazy_state.animateScrollToItem(
//                        messages.messages.size - 1,
////                        lazy_state.layoutInfo.visibleItemsInfo.last().offset
//                    )
//                }
//            }
//            if (lazy_state.layoutInfo.visibleItemsInfo.isNotEmpty())
//                if (lazy_state.layoutInfo.visibleItemsInfo.last().index == messages.messages.lastIndex - 1) {
//                    scope.launch {
//                        lazy_state.animateScrollToItem(
//                            messages.messages.size - 1,
////                            lazy_state.layoutInfo.visibleItemsInfo.last().offset
//                        )
//                    }
//                } else {
//                    lazy_state.layoutInfo.visibleItemsInfo.last().index
//
//                    val countUnreadMessage =
//                        messages.messages.size - lazy_state.layoutInfo.visibleItemsInfo.last().index - 1
//                    viewModel.countUnreadMessage = countUnreadMessage
//                    viewModel.visibleButtonDown = true
//                }
//        }
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun AttachMenuChat(viewModel: ChatViewModel, sheetState: ModalBottomSheetState) {

    val mapPoints = mapOf(
        "Галерея" to R.drawable.ic_photo_svgrepo_com,
        "Файл" to R.drawable.ic_file
    )

    @Composable
    fun OneItem(name: String, icon: Int) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val keyboard = LocalSoftwareKeyboardController.current

        Column {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0FFFF))
                    .align(CenterHorizontally)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            when (name) {
                                "Галерея" -> {
                                    viewModel.bottomSheetState = true
                                    keyboard?.hide()
                                    viewModel.menuHelper.setVisibilityMenu(MENUS.ATTACH, false)
                                    viewModel.galleryHandler!!.getCameraImages(context)
                                    scope.launch {
//                            state.animateTo(ModalBottomSheetValue.Expanded)
                                        sheetState.show()
                                    }
                                }
                                "Файл" -> {
                                    viewModel.bottomSheetState = false
                                    viewModel.menuHelper.setVisibilityMenu(MENUS.ATTACH, false)
                                    viewModel.listFiles =
                                        File(viewModel.selectedPath)
                                            .list()
                                            .toList()
//                                            .reversed()
                                    scope.launch {
//                            state.animateTo(ModalBottomSheetValue.Expanded)
                                        sheetState.show()
                                    }
                                }
                            }

                        }
                    )
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.padding(7.dp),
                    Color(0xFFC0C6CA)
                )
            }
            Text(
                text = name,
                modifier = Modifier
                    .align(CenterHorizontally)
            )
        }
    }

    Box(
        Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFDAE0E4))
    ) {
        Row() {
            Spacer(
                modifier = Modifier
                    .width(15.dp)
            )
            mapPoints.forEachKeys { key, item, _ ->
                OneItem(key, item)
                Spacer(
                    modifier = Modifier
                        .width(15.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentFilesBottomSheet(viewModel: ChatViewModel, bottomSheetState: ModalBottomSheetState) {

    fun getSizeFile(size: Long): String {
        if (size > 8) {
            val bytes = size / 8f
            if (bytes > 1024) {
                val kBytes = bytes / 1024f
                if (kBytes > 1024) {
                    val mBytes = kBytes / 1024f
                    if (mBytes > 1024)
                        return "${String.format("%.1f", mBytes / 1024f)} GB"
                    else
                        return "${String.format("%.1f", mBytes)} MB"
                } else
                    return "${String.format("%.1f", kBytes)} KB"
            } else
                return "${String.format("%.1f", bytes)} B"
        } else if (size == 0L)
            return "0 Bit"
        else
            return "${String.format("%.1f", size)} Bit"
    }

    @Composable
    fun OneItemFile(item: String) {
        val file = File("${viewModel.selectedPath}/$item")

        val context = LocalContext.current.applicationContext

        val scope = rememberCoroutineScope()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(0.9f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (file.isDirectory) {
                            viewModel.selectedPath += "/$item"
                            viewModel.listFiles =
                                File(viewModel.selectedPath)
                                    .list()
                                    .toList()
//                                    .reversed()
                        }
                        if (item == "...") {
                            viewModel.selectedPath =
                                viewModel.selectedPath.replace("\\/\\w+\$".toRegex(), "")
                            viewModel.listFiles =
                                File(viewModel.selectedPath)
                                    .list()
                                    .toList()
                        }
                        if (file.isFile) {

                            val taskData = Data
                                .Builder()
                                .putString("nameFile", item)
                                .putString("dirFile", "${viewModel.selectedPath}/")
                                .putString("idChat", viewModel.chatEntity?.id!!)
                                .build()
                            val uploadWorkRequest: WorkRequest =
                                OneTimeWorkRequestBuilder<UploadWorker>()
                                    .setInputData(taskData)
                                    .build()
                            WorkManager
                                .getInstance(context.applicationContext)
                                .enqueue(uploadWorkRequest)
                            scope.launch {
                                bottomSheetState.hide()
                                viewModel.listFiles = emptyList()
                            }
                        }
                    }
                )
        ) {
            Icon(
                painter = painterResource(id = if (file.isFile) R.drawable.ic_file else R.drawable.ic_dir),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp),
                Color(0xFFDAE0E4)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Column {
                Text(
                    text = item,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
//                val sizeFile = File("${Routes.FILE.ANDROID_DIR + DOWNLOAD_DIR}/$item").length()

                Text(
                    text = if (item == "...") "Назад" else getSizeFile(file.length()),
                    fontSize = 14.sp,
                    maxLines = 1,
                )
            }
        }
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeightIn(min = 1.dp)
            .wrapContentWidth(unbounded = false)
    ) {
        if (viewModel.selectedPath != "/storage/emulated/0")
            item {
                OneItemFile("...")
            }
        if (viewModel.listFiles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Файлы не найдены",
                        fontSize = 22.sp,
                        modifier = Modifier
                            .padding(vertical = 30.dp)
                            .align(Center)
                    )
                }
            }
        }
//        if (viewModel.selectedPath != "${Routes.FILE.ANDROID_DIR + Routes.FILE.DOWNLOAD_DIR}"){

//        }
        items(viewModel.listFiles) { item ->
            OneItemFile(item)
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
//            .background(Color(0xFFDAE0E4))
            .background(Color(0x33E6E6FA))
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
                            scope.launch {
                                state.hide()
                            }
                            viewModel.addImageToMessage(value)
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
                        viewModel.menuHelper.changeVisibilityMenu(MENUS.ATTACH)
//                        keyboard?.hide()
//                        viewModel.galleryHandler!!.getCameraImages(context)
//                        scope.launch {
////                            state.animateTo(ModalBottomSheetValue.Expanded)
//                            state.show()
//                        }
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

@OptIn(ExperimentalCoilApi::class, ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Message(
    message: ChatMessageDC,
    viewModel: ChatViewModel,
    msViewModel: MainSocketViewModel,
    modifier: Modifier
) {
//    var messageSelected by remember {
//        mutableStateOf(false)
//    }
//
//    val images by remember {
//        mutableStateOf(message.listImages.windowed(2, 2, true))
//    }

    val context = LocalContext.current
    BoxWithConstraints(
        modifier = modifier
            .clickable {
                if (viewModel.messageSelected?.id == message.id)
                    viewModel.messageSelected = null
                else
                    viewModel.messageSelected = message
            }
    ) {
        AnimatedContent(
            targetState = viewModel.messageSelected?.id == message.id,
            transitionSpec = {
//                fadeIn(animationSpec = tween(500, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.7f,
                    animationSpec = tween(500, delayMillis = 0)
                ) with
                        scaleOut(
                            targetScale = 0.7f,
                            animationSpec = tween(500, delayMillis = 0)
                        )
            }
        ) { targerState ->
            if (!targerState || targerState == null)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
//                        .background(Color(0xFFDAE0E4))
//                        .background(Color(0x3366CDAA))
                        .background(Color(0x22708090))
                        .requiredWidthIn(max = maxWidth * 0.75f)
                ) {

                    if (message.listFiles.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {

                            IconButton(
                                onClick = {
                                    msViewModel.sendAction("loadFile|${message.listFiles.last().path}|${message.listFiles.last().name}|")
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
//                                .size(100.dp)
                                    .background(Color.LightGray)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_download),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp),
                                    Color(0xFFEBEDEF)
                                )
                            }
                            Spacer(modifier = Modifier.width(7.dp))
                            Column(
//                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = message.listFiles.last().name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Gray,
                                )
//                                Row(
//                                    modifier = Modifier.align(Start)
//                                ) {

                                    Text(
                                        text = "${message.listFiles.last().size} ${message.listFiles.last().type.uppercase()}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFFD0D0D0),
                                    )
//                                    Text(
//                                        text = " ${message.listFiles.last().type}".uppercase(),
//                                        fontSize = 13.sp,
//                                        fontWeight = FontWeight.Normal,
//                                        color = Color.White,
//                                    )
//                                }
                            }
                        }
                    }

                    if (message.listImages.size == 1) {
                        val imageLink =
                            "${Routes.SERVER.REQUESTS.BASE_URL}/${message.listImages[0]}"

                        val cached = loader.diskCache?.get(message.listImages[0])?.data

                        AsyncImage(
                            model =
                            if (cached != null) {
                                ImageRequest.Builder(context)
                                    .data(cached.toFile())
                                    .size(100, 100)
                                    .diskCachePolicy(CachePolicy.READ_ONLY)
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
                            },
                            imageLoader = loader,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(bottom = if (message.message == "") 0.dp else 7.dp)
                                .requiredHeightIn(max = 300.dp)
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectedImage = SelectedImageMessage(
                                        imageRequest = message.listImages[0],
                                        message = message
                                    )
                                }
                        )
                    } else if (message.listImages.size > 1) {

                        message.listImages.windowed(2, 2, false).forEach { list ->
                            Row(Modifier.padding(7.dp)) {
                                val imageLink = "${Routes.SERVER.REQUESTS.BASE_URL}/${list[0]}"

                                val cached = loader.diskCache?.get(list[0])?.data

                                AsyncImage(
                                    model =
                                    if (cached != null) {
                                        ImageRequest.Builder(context)
                                            .data(cached.toFile())
                                            .size(100, 100)
                                            .diskCachePolicy(CachePolicy.READ_ONLY)
                                            .crossfade(true)
                                            .build()
                                    } else {
                                        ImageRequest.Builder(context)
                                            .data(imageLink)
                                            .size(100, 100)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .diskCacheKey(list[0])
                                            .crossfade(true)
                                            .build()
                                    },
                                    imageLoader = loader,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .requiredHeightIn(max = 100.dp)
                                        .weight(1f)
                                        .clickable {
                                            viewModel.selectedImage = SelectedImageMessage(
                                                imageRequest = list[0],
                                                message = message
                                            )
                                        }
                                )

                                if (list.size > 1) {
                                    Spacer(modifier = Modifier.width(10.dp))

                                    val imageLink = "${Routes.SERVER.REQUESTS.BASE_URL}/${list[1]}"

                                    val cached = MainActivity.loader.diskCache?.get(list[1])?.data

                                    AsyncImage(
                                        model =
                                        if (cached != null) {
                                            ImageRequest.Builder(context)
                                                .data(cached.toFile())
                                                .diskCachePolicy(CachePolicy.READ_ONLY)
                                                .size(100, 100)
                                                .crossfade(true)
                                                .build()
                                        } else {
                                            ImageRequest.Builder(context)
                                                .data(imageLink)
                                                .size(100, 100)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .diskCacheKey(list[1])
                                                .crossfade(true)
                                                .build()
                                        },
                                        imageLoader = loader,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .requiredHeightIn(max = 100.dp)
                                            .weight(1f)
                                            .clickable {
                                                viewModel.selectedImage = SelectedImageMessage(
                                                    imageRequest = list[1],
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
                            color = Color(0xFFD0D0D0),
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .align(End)
                        )
                    }
                }
            else {
                ActionMessage(viewModel)
            }
        }

        if (message.message == "") {
            val regexTime = "(?<=г. ).+(?=:)".toRegex()
            val result = regexTime.find(message.date)?.value!!
            Text(
                text = result,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD0D0D0),
                modifier = Modifier
                    .padding(end = 10.dp, bottom = if (message.listFiles.isEmpty()) 7.dp else 2.dp)
                    .align(BottomEnd)
            )
        }
    }
}

@Composable
fun ActionMessage(viewModel: ChatViewModel) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
//            .padding(horizontal = 10.dp, vertical = 7.dp)
//            .fillMaxWidth()
//            .height(40.dp)
            .background(Color(0xFFDAE0E4))
    ) {
        Row() {
            IconButton(onClick = { viewModel.deleteMessage() }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
    }
}