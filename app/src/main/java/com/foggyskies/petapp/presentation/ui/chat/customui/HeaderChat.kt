package com.foggyskies.petapp.presentation.ui.chat.customui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.routs.Routes

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
                backgroundColor = Color(0x33E6E6FA)
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