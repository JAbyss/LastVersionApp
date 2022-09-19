package com.foggyskies.petapp.presentation.ui.chat.customui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.profile.MENUS
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys
import kotlinx.coroutines.launch
import java.io.File


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