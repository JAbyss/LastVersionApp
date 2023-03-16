package com.foggyskies.petapp.presentation.ui.profile.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.R
import com.foggyskies.testingscrollcompose.presentation.ui.registation.customui.animation.animateDpOffsetAsState

@ExperimentalAnimationApi
@Composable
fun BoxScope.PetBottomMenu() {

    var isExpandMenu by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(targetValue = if (isExpandMenu) 45f else 0f)

    val animationOffsetImage by animateDpOffsetAsState(
        targetValue = if (isExpandMenu) DpOffset(
            x = (-50).dp,
            y = (-60).dp
        ) else DpOffset.Zero
    )
    val animationOffsetVideo by animateDpOffsetAsState(
        targetValue = if (isExpandMenu) DpOffset(
            x = 50.dp,
            y = (-60).dp
        ) else DpOffset.Zero
    )

    if (isExpandMenu)
        Button(
            onClick = { /*TODO*/ },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFDAE0E4)
            ),
            contentPadding = PaddingValues(),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
            modifier = Modifier
                .offset(x = animationOffsetVideo.x, y = animationOffsetVideo.y)
                .size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_video),
                contentDescription = null,
                modifier = Modifier
                    .align(CenterVertically)
                    .size(30.dp)
            )
        }
    if (isExpandMenu)

        Button(
            onClick = { /*TODO*/ },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFDAE0E4)
            ),
            contentPadding = PaddingValues(),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
            modifier = Modifier
                .offset(x = animationOffsetImage.x, y = animationOffsetImage.y)
                .size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo_svgrepo_com),
                contentDescription = null,
                modifier = Modifier
                    .align(CenterVertically)
                    .size(30.dp)
            )
        }
    Button(
        onClick = {
            isExpandMenu = !isExpandMenu
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0x3100C853)
        ),
        contentPadding = PaddingValues(),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .size(50.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentDescription = null,
            modifier = Modifier
                .rotate(rotation)
                .align(CenterVertically)
                .size(30.dp)
        )
    }
}