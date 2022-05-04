package com.foggyskies.petapp.presentation.ui.globalviews.post

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import com.foggyskies.petapp.presentation.ui.home.PostScreenHandler
import com.foggyskies.petapp.presentation.ui.home.StatePost
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomCommentBar(
//    viewModel: HomeViewModel,
    postScreenHandler: PostScreenHandler
) {

    val pagerState = rememberPagerState()

    val scope = rememberCoroutineScope()

    @Composable
    fun WriteComment() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(visible = postScreenHandler.iconUsersReply) {
                IconButton(
                    onClick = {
                        postScreenHandler.isTagMenuOpen = !postScreenHandler.isTagMenuOpen
                    },
                    modifier = Modifier
                        .size(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_reply_user),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(7.dp))
            BasicTextField(
                value = postScreenHandler.commentValue,
                onValueChange = {
                    postScreenHandler.iconUsersReply = !it.text.contains('@')
                    if (postScreenHandler.commentValue.text.contains("@ ")) {
                        postScreenHandler.isTagMenuOpen = false
                    }
                    if (postScreenHandler.commentValue.text.count { char -> char == '@' } < it.text.count { char -> char == '@' }) {
                        postScreenHandler.isTagMenuOpen = true
                    }
                    if (postScreenHandler.commentValue.text.count { char -> char == '@' } > it.text.count { char -> char == '@' }) {
                        postScreenHandler.isTagMenuOpen = false
                    }
                    postScreenHandler.commentValue = it
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            IconButton(onClick = {
                scope.launch {
                    postScreenHandler.sendNewComment()
                }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = null
                )
            }
        }
    }

    @Composable
    fun Likes() {
        Row(modifier = Modifier.fillMaxWidth()) {
        }
    }

    @Composable
    fun ImageBar() {
//        val isLiked = postScreenHandler.isLiked

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(33.dp)
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = "http://$MAINENDPOINT/${postScreenHandler.selectedPost?.image}",
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = postScreenHandler.selectedPost?.author!!,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(0.35f)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {

                    Button(
                        onClick = {
                            postScreenHandler.statePost = StatePost.IMAGE
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        ),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier
                            .padding(end = 7.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = null,
                            modifier = Modifier,
                            Color.Black
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        ),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier
                            .padding(end = 7.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chat_idle),
                            contentDescription = null,
                            modifier = Modifier,
                            Color.Black
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                postScreenHandler.likePost()
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        ),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = if (!postScreenHandler.isLiked) R.drawable.ic_like_not_clicked else R.drawable.ic_like),
                            contentDescription = null,
                            modifier = Modifier,
                            if (postScreenHandler.isLiked) Color.Red else Color.Black
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun BottomBar(position: Int) {
        when (position) {
            0 -> ImageBar()
            1 -> WriteComment()
            2 -> Likes()
        }
    }

    HorizontalPager(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f)
//            .background(Color.Blue)
        ,
        count = 3,
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { position ->
        when (currentPage) {
            0 -> postScreenHandler.statePost = StatePost.IMAGE
            1 -> {
                postScreenHandler.statePost = StatePost.COMMENTS
                scope.launch {
                    postScreenHandler.getComments()
                }
            }
            2 -> {
                postScreenHandler.statePost = StatePost.LIKES
                scope.launch {
                    postScreenHandler.getLikedUsers()
                }
            }
        }
        BottomBar(position)
    }
}