package com.foggyskies.petapp.presentation.ui.chat.customui

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.chat.MessageDC
import com.foggyskies.petapp.presentation.ui.chat.StateTextField
import com.foggyskies.petapp.presentation.ui.chat.requests.editMessage
import com.foggyskies.petapp.presentation.ui.chat.requests.messageWithContent
import com.foggyskies.petapp.presentation.ui.profile.MENUS
import com.foggyskies.petapp.workers.TypeLoadFile
import com.foggyskies.petapp.workers.UploadFileViewModel
import com.foggyskies.petapp.workers.uploadFile
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent


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

//    var value by remember {
//        if (view)
//            mutableStateOf("")
//    }


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
        val modelProvider by KoinJavaComponent.inject<ViewModelProvider>(ViewModelProvider::class.java)
        val uploadModel = modelProvider["UploadFileViewModel", UploadFileViewModel::class.java]
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
                value = viewModel.bottomBarValue,
                onValueChange = {
                    viewModel.bottomBarValue = it
                    if (viewModel.bottomBarValue == "")
                        viewModel.stateTextField = StateTextField.EMPTY
                    else if (viewModel.stateTextField != StateTextField.EDIT)
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
                    when (viewModel.stateTextField) {
                        StateTextField.WRITING -> {
                            if (viewModel.galleryHandler!!.listPath.isNotEmpty()) {
                                scope.launch {
                                    state.hide()
                                }
                                val listImageAddress = mutableListOf<String>()
//                                viewModel.backgroundScope.launch {


                                uploadModel.uploadImagesToChat(
                                    message = viewModel.bottomBarValue,
                                    idChat = viewModel.chatEntity?.id!!,
                                    dirFile = viewModel.galleryHandler!!.selectedItems.toTypedArray(),
                                    typeLoad = TypeLoadFile.CHAT,
                                    infoData = viewModel.chatEntity?.id!!,
                                )
//                                        listImageAddress.add(
//                                            uploadFile(
//                                                it,
//                                                typeLoad = TypeLoadFile.CHAT,
//                                                infoData = viewModel.chatEntity?.id!!,
//                                            )
//                                        )
//                                    }
//                                    viewModel.messageWithContent(
//                                        message = MessageDC(
//                                            message = viewModel.bottomBarValue,
//                                            listImages = listImageAddress
//                                        )
//                                    )
//                                viewModel.galleryHandler!!.listPath.forEach {
//                                    uploadFile(
//                                        it,
//                                        typeLoad = TypeLoadFile.CHAT,
//                                        infoData = viewModel.chatEntity?.id!!,
//                                    ) { path ->
//                                        listImageAddress.add(path)
//                                    }
//                                }

//                                viewModel.addImageToMessage(viewModel.bottomBarValue)
                            } else {
                                if (!viewModel.bottomBarValue.isBlank()) {
                                    val formattedString =
                                        viewModel.bottomBarValue.replace(
                                            regex = "(^\\s+)|(\\s+\$)".toRegex(),
                                            ""
                                        )
                                    viewModel.sendMessage(formattedString)
                                }
                            }
                            viewModel.bottomBarValue = ""
                            viewModel.stateTextField = StateTextField.EMPTY
                        }
                        StateTextField.EMPTY -> {
                            viewModel.menuHelper.changeVisibilityMenu(MENUS.ATTACH)
                        }
                        StateTextField.EDIT -> {
                            viewModel.editMessage()
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
                ) { targetValue ->
                    Image(
                        painter = painterResource(
                            id = if (targetValue == StateTextField.EMPTY)
                                R.drawable.ic_round_attach_file_24
                            else
                                R.drawable.ic_send
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(if (targetValue == StateTextField.EMPTY) -135f else if (targetValue == StateTextField.WRITING) 0f else -180f)
                            .size(34.dp)
                    )
                }
            }
        }
    }
}
