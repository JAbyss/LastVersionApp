package com.foggyskies.petapp.presentation.ui.globalviews.post.sidescreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import kotlin.reflect.KFunction0

@Composable
fun ImageScreen(
    image: String,
    onLongPress: (Offset) -> Unit,
    onDoubleTap: KFunction0<Unit>,
//    onDoubleTap: Unit,
) {

    var scale by remember { mutableStateOf(1f) }

    var offset by remember { mutableStateOf(Offset.Zero) }
    AsyncImage(
        model =
        "http://$MAINENDPOINT/$image",
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .graphicsLayer(
                scaleX = if (scale < 1f) 1f else scale,
                scaleY = if (scale < 1f) 1f else scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .fillMaxSize()
//            .pointerInput(1) {
//                detectTransformGestures { centroid, pan, zoom, rotation ->
//                    if (scale != 1.3f)
//                        offset += pan
//                    scale *= zoom
//                }
//            }
            .pointerInput(true) {

                detectTapGestures(
                    onDoubleTap = {
                        onDoubleTap()
                    }
//                        isLiked = !isLiked
                    ,
                    onTap = {
                        scale = 1f
                        offset = Offset.Zero
                    },
                    onLongPress = null
//                        {
//                            viewModel.swipableMenu.isReadyMenu = false
//                            viewModel.isVisiblePhotoWindow = false
//                            viewModel.photoScreenClosed()
//                        }

                )

            }
    )
}