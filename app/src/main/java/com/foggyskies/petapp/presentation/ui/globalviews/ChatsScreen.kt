package com.foggyskies.petapp.presentation.ui.globalviews

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.get
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.data.Chat
import com.foggyskies.petapp.presentation.ui.home.HomeMVIModel
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.profile.human.MENUS
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
data class FormattedChatDC(
    var id: String,
    var nameChat: String,
    var idCompanion: String,
    var image: String,
    var lastMessage: String = ""
) : Parcelable{
    fun toChat(): Chat {
        return Chat(
            idChat = id,
            companionId = idCompanion,
            companionName = nameChat,
            imageCompanion = image,
            lastMessage = lastMessage,
        )
    }
}

@Composable
fun OneItemChat(item: FormattedChatDC, nav_controller: NavHostController?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
               val string = Json.encodeToString(item)
                val b = bundleOf("itemChat" to string)
                nav_controller?.navigate(nav_controller.graph[NavTree.ChatSec.name].id, b)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
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

            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = item.nameChat, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = if (item.lastMessage.isNotEmpty()) item.lastMessage else "Пусто, напишите первым.", maxLines = 1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.LightGray, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun BoxScope.ChatsScreen(nav_controller: NavHostController?, viewModel: HomeMVIModel, msViewModel: MainSocketViewModel) {

    Box() {

        Box(
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ){
                    viewModel.menuHelper.changeVisibilityMenu(MENUS.CHATS)
//                    viewModel.chatsMenuSwitch()
                }
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .widthIn(max = 300.dp)
                .align(Alignment.Center)
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(5.dp)
            ) {
                items(msViewModel.listChats) { item ->
                    OneItemChat(item, nav_controller)
                }
            }
        }
    }
}