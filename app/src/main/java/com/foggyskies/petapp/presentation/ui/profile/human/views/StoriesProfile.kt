package com.foggyskies.petapp.presentation.ui.profile.human.views

import android.app.Activity
import android.os.Build
import android.view.WindowMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.R

@Composable
fun StoriesProfile(
    index: Int,
//    item: Int,
    lastIndex: Int,
    modifier: Modifier
) {


    val display_metrics = LocalContext.current.resources.displayMetrics
    val context = LocalContext.current


    val width: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics =
                (context as Activity).windowManager.currentWindowMetrics
//            val insets: Insets = windowMetrics.windowInsets
//                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width()
        } else {
            display_metrics.widthPixels
        }

    val width_config = (width / display_metrics.density).toInt()

    Spacer(modifier = Modifier.width(15.dp))

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFFAFDED9))
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_dog),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(2.dp)
                .clip(CircleShape)
                .size(
                    ((width_config - 15 * 6) / 4).dp
//                            55.dp
                )
        )
    }
    if (index == lastIndex)
        Spacer(modifier = Modifier.width(15.dp))
}