package com.foggyskies.petapp.presentation.ui.globalviews.post

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.presentation.ui.home.widgets.post.StatePost
import com.foggyskies.petapp.presentation.ui.home.widgets.post.bottom_bar.screens.ImageBar
import com.foggyskies.petapp.presentation.ui.home.widgets.post.bottom_bar.screens.Likes
import com.foggyskies.petapp.presentation.ui.home.widgets.post.bottom_bar.screens.WriteComment
import com.foggyskies.petapp.presentation.ui.home.widgets.post.requests.comments
import com.foggyskies.petapp.presentation.ui.home.widgets.post.requests.likedUsers
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BoxScope.BottomCommentBar(
    postScreenHandler: PostScreenHandler,
    isScrollable: MutableState<Boolean>
) {

    val pagerState = rememberPagerState()

    val scope = rememberCoroutineScope()

    @Composable
    fun BottomBar(position: Int) {
        when (position) {
            0 -> ImageBar(postScreenHandler, pagerState)
            1 -> WriteComment(postScreenHandler)
            2 -> Likes(postScreenHandler)
        }
    }

    HorizontalPager(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        count = 3,
        state = pagerState,
        verticalAlignment = Alignment.Bottom
    ) { position ->
        when (currentPage) {
            0 -> {
                isScrollable.value = true
                postScreenHandler.statePost = StatePost.IMAGE
            }
            1 -> {
                isScrollable.value = false
                postScreenHandler.statePost = StatePost.COMMENTS
                postScreenHandler.comments()
            }
            2 -> {
                isScrollable.value = false
                postScreenHandler.statePost = StatePost.LIKES
                postScreenHandler.likedUsers()
            }
        }
        BottomBar(position)
    }
}