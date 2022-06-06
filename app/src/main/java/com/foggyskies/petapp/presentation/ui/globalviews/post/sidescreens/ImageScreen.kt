package com.foggyskies.petapp.presentation.ui.globalviews.post.sidescreens

import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.ViewSizeResolver
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.MainActivity.Companion.loaderForPost
import com.foggyskies.petapp.routs.Routes
import kotlin.reflect.KFunction0

@Composable
fun ImageScreen(
    image: String,
    description: String,
    onDoubleTap: KFunction0<Unit>,
//    onDoubleTap: Unit,
) {
    val scrollState = rememberScrollState()

    var textVisibility by remember {
        mutableStateOf(description.isNotEmpty())
    }
    val context = LocalContext.current

//    val view = ComposeView(context).apply {
//        Box() {
//
//        }
//    }
//    val cah = loaderForPost.memoryCache?.get(MemoryCache.Key())
//    val a =

    Box() {
        AsyncImage(
            model ="${Routes.SERVER.REQUESTS.BASE_URL}/$image",
            imageLoader = MainActivity.loaderForPost,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .pointerInput(true) {

                    detectTapGestures(
                        onDoubleTap = {
                            onDoubleTap()
                        }, onTap = if (description.isNotEmpty()) {
                            { textVisibility = true }
                        } else null
                    )

                }
        )
        AnimatedVisibility(
            visible = textVisibility,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Box() {

                Box(
                    modifier = Modifier
                        .padding(bottom = 55.dp, top = 15.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth(0.9f)
//                .heightIn(max = 300.dp)
                        .background(Color(0x88FFFFFF))
                        .align(Alignment.BottomCenter)
                        .verticalScroll(scrollState)
                ) {
                    Text(
//                text = "ASCII (англ. American standard code for information interchange, [ˈæs.ki][1]) — название таблицы (кодировки, набора), в которой некоторым распространённым печатным и непечатным символам сопоставлены числовые коды. Таблица была разработана и стандартизирована в США, в 1963 году.",
                        text = description,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(0.9f)
                            .align(Alignment.Center)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(end = 7.dp, bottom = 60.dp)
                        .align(Alignment.BottomEnd)
                        .clickable {
                            textVisibility = false
                        }
                ) {

                    Divider(
                        thickness = 3.dp,
                        color = Color.Black,
                        modifier = Modifier
//                    .padding(horizontal = 7.dp, vertical = 5.dp)
                            .padding(7.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .height(3.5.dp)
                            .width(12.dp)
                            .align(Alignment.Center)

                    )
                }
            }
        }
    }
}