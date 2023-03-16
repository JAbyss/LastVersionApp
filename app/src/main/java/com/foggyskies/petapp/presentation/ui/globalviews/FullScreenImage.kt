package com.foggyskies.petapp.presentation.ui.globalviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.SelectedImageMessage
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import com.foggyskies.petapp.routs.Routes

@Composable
fun FullScreenImage(
    selectedMessage: SelectedImageMessage?,
//    message: ChatMessage,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var scale by remember { mutableStateOf(1f) }

    var offset by remember { mutableStateOf(Offset.Zero) }

    var isVisibleHUD by remember {
        mutableStateOf(true)
    }

    val imageLink = "${Routes.SERVER.REQUESTS.BASE_URL}/${selectedMessage?.imageRequest}"

    val cached = selectedMessage?.imageRequest?.let { MainActivity.loader.diskCache?.get(it)?.data }

    val selectedRequest = remember {
        mutableStateOf(
            if (cached != null) {
                ImageRequest.Builder(context)
                    .data(cached.toFile())
                    .diskCachePolicy(CachePolicy.READ_ONLY)
//                                .diskCacheKey(it[0])
                    .crossfade(true)
                    .build()
            } else {
                ImageRequest.Builder(context)
                    .data(imageLink)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(selectedMessage?.imageRequest)
                    .crossfade(true)
                    .build()
            }
        )
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        AsyncImage(
            model = selectedRequest.value,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = if (scale < 1f) 1f else scale,
                    scaleY = if (scale < 1f) 1f else scale,
                    translationX = if (scale != 1f || offset.x > 0f) offset.x else 0f,
                    translationY = offset.y
                )
//                .pointerInput(1){
//                    detectDragGestures { change, dragAmount ->
//                        Log.e("TEST TRANSFORM", "change $change \n dragAmount $dragAmount \n ")
//
//                    }
//                }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
//                        Log.e("TEST TRANSFORM", "pan $pan ")
                        if (scale != 1f)
                            offset += (pan * scale)
                        if (scale < 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        }
                        if (scale == 1f) {
                            isVisibleHUD = false
                            offset += pan
                            if (offset.y > 500 || offset.y < -500)
                                onBackClick()
                        }
                        scale *= zoom
                    }

                }
                .pointerInput(true) {
                    detectTapGestures(onTap = {
                        isVisibleHUD = !isVisibleHUD
                    })
                }

                .align(Alignment.Center)
        )
        AnimatedVisibility(
            visible = isVisibleHUD,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            TopBar(onBackClick)
        }
        AnimatedVisibility(
            visible = isVisibleHUD,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            selectedMessage?.message?.let { BottomBar(it, selectedRequest) }
        }
    }
}

@Composable
private fun BoxScope.TopBar(
    onBackClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x77000000))
            .align(Alignment.TopCenter)
    ) {

        Row(
            Modifier
                .align(Alignment.CenterStart)
        ) {
            Spacer(modifier = Modifier.width(5.dp))
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    Color.White
                )
            }
//            Spacer(modifier = Modifier.width(20.dp))
        }

//        Row(
//            modifier = Modifier
//                .padding(vertical = 5.dp)
//                .align(Alignment.CenterEnd)
//        ) {
//            IconButton(onClick = { /*TODO*/ }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
//                    contentDescription = null,
//                    modifier = Modifier.size(30.dp),
//                    Color.White
//                )
//            }
//            Spacer(modifier = Modifier.width(5.dp))
//            IconButton(onClick = { /*TODO*/ }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
//                    contentDescription = null,
//                    modifier = Modifier.size(30.dp),
//                    Color.White
//                )
//            }
//            Spacer(modifier = Modifier.width(5.dp))
//        }
    }
}

@Composable
private fun BoxScope.BottomBar(
    message: ChatMessageDC,
    selectedRequest: MutableState<ImageRequest>
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(Color(0x77000000))
    ) {
        if (message.message.isNotEmpty())
            Text(
                text = message.message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 7.dp)
            )
        if (message.listImages.size > 1)
            LazyRow(
                modifier = Modifier
                    .padding(top = 7.dp)
                    .align(CenterHorizontally)
            ) {
                itemsIndexed(message.listImages) { index, item ->

                    val imageLink = "${Routes.SERVER.REQUESTS.BASE_URL}/$item"

                    val cached = MainActivity.loader.diskCache?.get(item)?.data
                    val request = if (cached != null) {
                        ImageRequest.Builder(context)
                            .data(cached.toFile())
                            .diskCachePolicy(CachePolicy.READ_ONLY)
                            .diskCacheKey(item)
                            .crossfade(true)
                            .build()
                    } else {
                        ImageRequest.Builder(context)
                            .data(imageLink)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(item)
                            .crossfade(true)
                            .build()
                    }
                    MainActivity.loader.enqueue(request)

                    AsyncImage(
                        model = request,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(0.07f)
                            .clickable {
                                selectedRequest.value = request
                            }
                    )
                    if (index != message.listImages.lastIndex)
                        Spacer(modifier = Modifier.width(10.dp))
                }
            }
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .padding(vertical = 7.dp)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = message.author,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = message.date,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
//            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
//                IconButton(onClick = { /*TODO*/ }) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
//                        contentDescription = null,
//                        modifier = Modifier.size(30.dp),
//                        Color.White
//                    )
//                }
//                Spacer(modifier = Modifier.width(5.dp))
//                IconButton(onClick = { /*TODO*/ }) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
//                        contentDescription = null,
//                        modifier = Modifier.size(30.dp),
//                        Color.White
//                    )
//                }
//                Spacer(modifier = Modifier.width(5.dp))
//            }
        }
    }
}