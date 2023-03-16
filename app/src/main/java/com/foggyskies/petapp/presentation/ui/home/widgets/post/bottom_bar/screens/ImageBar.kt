package com.foggyskies.petapp.presentation.ui.home.widgets.post.bottom_bar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.presentation.ui.home.widgets.post.StatePost
import com.foggyskies.petapp.presentation.ui.home.widgets.post.requests.likePost
import com.foggyskies.petapp.routs.Routes
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageBar(postScreenHandler: PostScreenHandler, pagerState: PagerState) {

    val scope = rememberCoroutineScope()

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
                model = "${Routes.SERVER.REQUESTS.BASE_URL}/${postScreenHandler.selectedPost?.image}",
                imageLoader = MainActivity.loaderForPost,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(30.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x88ffffff))
        ) {
            Text(
                text = postScreenHandler.selectedPost?.author!!,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 7.dp, vertical = 5.dp)
                    .fillMaxWidth(0.35f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {

                IconButton(
                    onClick = {
                        postScreenHandler.statePost = StatePost.IMAGE
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .background(Color(0x88ffffff))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = null,
                        modifier = Modifier,
                        Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(7.dp))
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .background(Color(0x88ffffff))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chat_idle),
                        contentDescription = null,
                        modifier = Modifier,
                        Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(7.dp))
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            postScreenHandler.likePost()
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .background(Color(0x88ffffff))
                ) {
                    Icon(
                        painter = painterResource(id = if (!postScreenHandler.isLiked) R.drawable.ic_like_not_clicked else R.drawable.ic_like),
                        contentDescription = null,
                        modifier = Modifier,
                        if (postScreenHandler.isLiked) Color.Red else Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
            }
        }
    }
}