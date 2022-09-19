package com.foggyskies.petapp.presentation.ui.home.widgets.post.bottom_bar.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.presentation.ui.home.widgets.post.requests.commentToPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
    fun WriteComment(postScreenHandler: PostScreenHandler) {
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
                value = postScreenHandler.commentValue.value,
                onValueChange = {
                    postScreenHandler.iconUsersReply = !it.text.contains('@')
                    if (postScreenHandler.commentValue.value.text.contains("@ ")) {
                        postScreenHandler.isTagMenuOpen = false
                    }
                    if (postScreenHandler.commentValue.value.text.count { char -> char == '@' } < it.text.count { char -> char == '@' }) {
                        postScreenHandler.isTagMenuOpen = true
                    }
                    if (postScreenHandler.commentValue.value.text.count { char -> char == '@' } > it.text.count { char -> char == '@' }) {
                        postScreenHandler.isTagMenuOpen = false
                    }
                    postScreenHandler.commentValue.value = it
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            IconButton(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    postScreenHandler.commentToPost()
                }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = null
                )
            }
        }
    }