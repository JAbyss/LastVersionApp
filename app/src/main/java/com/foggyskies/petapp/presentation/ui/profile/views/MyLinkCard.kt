package com.foggyskies.petapp.presentation.ui.profile.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R

data class MyLinkCardDC(
    var name: String,
    var link: String
)

@Composable
fun BoxScope.MyLinkCard(onClickClose: () -> Unit) {

    var scoll = rememberScrollState()

    Box(
        modifier = Modifier
    ) {

        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(0.8f)
                .background(Color(0xFFF5F8FD))
                .align(Alignment.Center)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scoll)
            ) {
//            Spacer(modifier = Modifier.height(7.dp))
                MyLinkCardItem(
                    MyLinkCardDC(
                        name = "ВК",
                        link = "https//ffwfwafawfwfaklfwkfkwjf"
                    )
                )
                MyLinkCardItem(
                    MyLinkCardDC(
                        name = "YouTube",
                        link = "https//ffsdsrbtntfhfjyjtyyktyktyktuktkytktykytkty"
                    )
                )
                MyLinkCardItem(
                    MyLinkCardDC(
                        name = "Telegramm",
                        link = "https//ffw224343534534545353fwafawfwfhrhasfeseggaklfwkfkwjf"
                    )
                )
                MyLinkCardItem(
                    MyLinkCardDC(
                        name = "Одноклассники",
                        link = "https//fsff"
                    )
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = 5.dp)
//                .padding(end = 20.dp)
                .clip(CircleShape)
                .size(30.dp)
                .background(color = Color(0xFFF5F8FD))
                .align(BottomCenter)
//                .clickable(onClick = onClickClose)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 20.dp, y = 10.dp)
                .clip(CircleShape)
                .size(30.dp)
                .background(color = Color(0xFFF5F8FD))
                .align(BottomEnd)
                .clickable(onClick = onClickClose)
        )
    }
}

@Composable
fun ColumnScope.MyLinkCardItem(item: MyLinkCardDC) {

    Box(
        modifier = Modifier
            .padding(
                vertical = 10.dp,
                horizontal = 10.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth(0.95f)
            .background(Color(0xFFDAE0E4))
            .align(CenterHorizontally)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(0.95f)
                .align(Alignment.Center)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .background(Color(0xFFC4E9FB))
            ) {
                Text(
                    text = item.name[0].toString(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF20B6F6)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column() {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF52596B)
                )
                Text(
                    text = item.link,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF52596B)
                )
            }
        }
    }
}