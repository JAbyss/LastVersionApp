package com.foggyskies.petapp.presentation.ui.chat.customui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.chat.requests.deleteMessage


@Composable
fun ActionMessage(viewModel: ChatViewModel) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFDAE0E4))
    ) {
        Row() {
            IconButton(onClick = { viewModel.deleteMessage() }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
            IconButton(onClick = { viewModel.editMessageMode() }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
    }
}