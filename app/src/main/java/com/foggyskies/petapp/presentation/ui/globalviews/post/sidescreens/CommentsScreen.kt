package com.foggyskies.petapp.presentation.ui.globalviews.post.sidescreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.chat.entity.CommentDC
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.routs.Routes

@Composable
fun CommentsScreen(
    state: LazyListState,
    postScreenHandler: PostScreenHandler
) {

    @Composable
    fun OneItemComment(users: HashMap<String, UserIUSI>, item: CommentDC) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Center)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box {
                        AsyncImage(
                            model = "${Routes.SERVER.REQUESTS.BASE_URL}/${users[item.idUser]?.image}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(25.dp)
                        )
                        Canvas(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                        ) {
                            drawCircle(
                                color = Color.White,
                                radius = 25f,
                            )
                            drawCircle(
                                color = if (users[item.idUser]?.status!! == "В сети") Color.Green else Color.Gray,
                                radius = 12f,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = users[item.idUser]?.username!!,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = item.message,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    fontSize = 13.sp,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = item.date,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    TextButton(
                        onClick = {
                            if (!postScreenHandler.commentValue.value.text.startsWith('@')) {
                                val user = "@${users[item.idUser]?.username!!} "
                                val selection = postScreenHandler.commentValue.value.selection
                                val textRange = TextRange(
                                    selection.start + user.length,
                                    selection.end + user.length
                                )
                                postScreenHandler.commentValue.value = TextFieldValue(
                                    user + postScreenHandler.commentValue.value.text,
                                    selection = textRange
                                )
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 15.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Text(text = "Ответить", fontSize = 12.sp, letterSpacing = 0.5.sp)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(postScreenHandler.listComments.comments) { index, item ->
                if (index == 0)
                    Spacer(modifier = Modifier.height(10.dp))
                OneItemComment(postScreenHandler.listComments.users, item)
                Spacer(modifier = Modifier.height(10.dp))
                if (index != 0 && index != postScreenHandler.listComments.comments.lastIndex)
                    Divider(
                        color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .fillMaxWidth(0.8f)
                    )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        AnimatedVisibility(
            visible = postScreenHandler.listComments.comments.isEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Комментарии отсутствуют",
                fontSize = 20.sp
            )
        }
        AnimatedVisibility(
            visible = postScreenHandler.isTagMenuOpen,
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 20.dp))
                    .background(Color.White)
                    .requiredHeightIn(max = 80.dp)
                    .fillMaxWidth(0.4f)
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    items(postScreenHandler.listComments.users.values.toList()) { user ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .clickable {
                                    if (postScreenHandler.commentValue.value.annotatedString.contains('@')) {
                                        val value = postScreenHandler.commentValue.value.text
                                        val selection = postScreenHandler.commentValue.value.selection
                                        val user = user.username
                                        val text = value.toMutableList()
                                        text.addAll(
                                            postScreenHandler.commentValue.value.selection.end,
                                            user.toList()
                                        )
                                        var string = ""
                                        text.map {
                                            string += it
                                        }
                                        postScreenHandler.commentValue.value = TextFieldValue(
                                            string,
                                            selection = TextRange(
                                                selection.start + user.length,
                                                selection.end + user.length
                                            )
                                        )
                                    } else if (postScreenHandler.commentValue.value.annotatedString.isEmpty()){
                                        val selection = postScreenHandler.commentValue.value.selection
                                        postScreenHandler.commentValue.value = TextFieldValue(
                                            "@${user.username} ",
                                            selection = TextRange(
                                                selection.start + user.username.length,
                                                selection.end + user.username.length
                                            )
                                        )
                                    }
                                    postScreenHandler.isTagMenuOpen = false
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Box() {
                                    AsyncImage(
                                        model = "${Routes.SERVER.REQUESTS.BASE_URL}/${user.image}",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(25.dp)
                                    )
                                    Canvas(
                                        modifier = Modifier
                                            .offset(x = (-3).dp, y = (-3).dp)
                                            .align(Alignment.BottomEnd)
                                    ) {
                                        drawCircle(
                                            color = Color.White,
                                            radius = 20f,
                                        )
                                        drawCircle(
                                            color = if (user.status == "Не в сети") Color.Gray else Color.Green,
                                            radius = 10f,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = user.username,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}