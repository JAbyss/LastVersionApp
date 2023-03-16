package com.foggyskies.petapp.presentation.ui.chat.customui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ChatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {

//    val requester = remember { FocusRequester() }
//
//    val inputService = LocalTextInputService.current?.startInput().

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
    ) {

//        val focus = remember { mutableStateOf(false) }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 20.sp
            ),
            enabled = true,
            interactionSource = MutableInteractionSource(),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {  }
        )
        if (value == "")
            Text(
                text = "Сообщение",
                fontSize = 20.sp
            )
    }
}