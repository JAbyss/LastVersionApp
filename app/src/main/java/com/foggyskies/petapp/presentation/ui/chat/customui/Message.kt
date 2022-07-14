package com.foggyskies.petapp.presentation.ui.chat.customui

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.chat.SelectedImageMessage
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.routs.Routes
import java.io.File

@OptIn(ExperimentalCoilApi::class, ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Message(
    message: ChatMessageDC,
    viewModel: ChatViewModel,
    msViewModel: MainSocketViewModel,
    modifier: Modifier
) {

    val context = LocalContext.current
    BoxWithConstraints(
        modifier = modifier
            .clickable {
                if (viewModel.messageSelected?.id == message.id)
                    viewModel.messageSelected = null
                else
                    viewModel.messageSelected = message
            }
    ) {
        AnimatedContent(
            targetState = viewModel.messageSelected?.id == message.id,
            transitionSpec = {
//                fadeIn(animationSpec = tween(500, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.7f,
                    animationSpec = tween(500, delayMillis = 0)
                ) with
                        scaleOut(
                            targetScale = 0.7f,
                            animationSpec = tween(500, delayMillis = 0)
                        )
            }
        ) { targerState ->
            if (!targerState || targerState == null)

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
//                        .background(Color(0xFFDAE0E4))
//                        .background(Color(0x3366CDAA))
                        .background(Color(0x22708090))
                        .requiredWidthIn(max = maxWidth * 0.75f)
                ) {

                    if (message.listFiles.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {

                            val isFileExist =
                                File("${Routes.FILE.ANDROID_DIR + Routes.FILE.DOWNLOAD_DIR}/${message.listFiles.last().fullName}").exists()

                            IconButton(
                                onClick = {
                                    if (!isFileExist)
                                        msViewModel.sendAction("loadFile|${message.listFiles.last().path}|${message.listFiles.last().name}|")
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
//                                .size(100.dp)
                                    .background(Color.LightGray)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (!isFileExist) R.drawable.ic_download else R.drawable.ic_file),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp),
                                    Color(0xFFEBEDEF)
                                )
                            }
                            Spacer(modifier = Modifier.width(7.dp))
                            Column(
//                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = message.listFiles.last().name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Gray,
                                )
//                                Row(
//                                    modifier = Modifier.align(Start)
//                                ) {

                                Text(
                                    text = "${message.listFiles.last().size} ${message.listFiles.last().type.uppercase()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFFD0D0D0),
                                )
//                                    Text(
//                                        text = " ${message.listFiles.last().type}".uppercase(),
//                                        fontSize = 13.sp,
//                                        fontWeight = FontWeight.Normal,
//                                        color = Color.White,
//                                    )
//                                }
                            }
                        }
                    }

                    if (message.listImages.size == 1) {
                        val imageLink =
                            "${Routes.SERVER.REQUESTS.BASE_URL}/${message.listImages[0]}"

                        val cached = loader.diskCache?.get(message.listImages[0])?.data

                        AsyncImage(
                            model =
                            if (cached != null) {
                                ImageRequest.Builder(context)
                                    .data(cached.toFile())
                                    .size(100, 100)
                                    .diskCachePolicy(CachePolicy.READ_ONLY)
                                    .crossfade(true)
                                    .build()
                            } else {
                                ImageRequest.Builder(context)
                                    .data(imageLink)
                                    .size(100, 100)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .diskCacheKey(message.listImages[0])
                                    .crossfade(true)
                                    .build()
                            },
                            imageLoader = loader,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(bottom = if (message.message == "") 0.dp else 7.dp)
                                .requiredHeightIn(max = 300.dp)
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectedImage = SelectedImageMessage(
                                        imageRequest = message.listImages[0],
                                        message = message
                                    )
                                }
                        )
                    } else if (message.listImages.size > 1) {

                        message.listImages.windowed(2, 2, false).forEach { list ->
                            Row(Modifier.padding(7.dp)) {
                                val imageLink = "${Routes.SERVER.REQUESTS.BASE_URL}/${list[0]}"

                                val cached = loader.diskCache?.get(list[0])?.data

                                AsyncImage(
                                    model =
                                    if (cached != null) {
                                        ImageRequest.Builder(context)
                                            .data(cached.toFile())
                                            .size(100, 100)
                                            .diskCachePolicy(CachePolicy.READ_ONLY)
                                            .crossfade(true)
                                            .build()
                                    } else {
                                        ImageRequest.Builder(context)
                                            .data(imageLink)
                                            .size(100, 100)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .diskCacheKey(list[0])
                                            .crossfade(true)
                                            .build()
                                    },
                                    imageLoader = loader,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .requiredHeightIn(max = 100.dp)
                                        .weight(1f)
                                        .clickable {
                                            viewModel.selectedImage = SelectedImageMessage(
                                                imageRequest = list[0],
                                                message = message
                                            )
                                        }
                                )

                                if (list.size > 1) {
                                    Spacer(modifier = Modifier.width(10.dp))

                                    val imageLink = "${Routes.SERVER.REQUESTS.BASE_URL}/${list[1]}"

                                    val cached = MainActivity.loader.diskCache?.get(list[1])?.data

                                    AsyncImage(
                                        model =
                                        if (cached != null) {
                                            ImageRequest.Builder(context)
                                                .data(cached.toFile())
                                                .diskCachePolicy(CachePolicy.READ_ONLY)
                                                .size(100, 100)
                                                .crossfade(true)
                                                .build()
                                        } else {
                                            ImageRequest.Builder(context)
                                                .data(imageLink)
                                                .size(100, 100)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .diskCacheKey(list[1])
                                                .crossfade(true)
                                                .build()
                                        },
                                        imageLoader = loader,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .requiredHeightIn(max = 100.dp)
                                            .weight(1f)
                                            .clickable {
                                                viewModel.selectedImage = SelectedImageMessage(
                                                    imageRequest = list[1],
                                                    message = message
                                                )
                                            }
                                    )
                                }
                            }
                        }
                    }
                    if (message.message != "") {
                        Text(
                            text = message.message,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                        )
                        val regexTime = "(?<=г. ).+(?=:)".toRegex()
                        val result = regexTime.find(message.date)?.value!!
                        Text(
                            text = result,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD0D0D0),
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .align(End)
                        )
                    }
                }
            else {
                ActionMessage(viewModel)
            }
        }

        if (message.message == "") {
            val regexTime = "(?<=г. ).+(?=:)".toRegex()
            val result = regexTime.find(message.date)?.value!!
            Text(
                text = result,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD0D0D0),
                modifier = Modifier
                    .padding(end = 10.dp, bottom = if (message.listFiles.isEmpty()) 7.dp else 2.dp)
                    .align(BottomEnd)
            )
        }
    }
}