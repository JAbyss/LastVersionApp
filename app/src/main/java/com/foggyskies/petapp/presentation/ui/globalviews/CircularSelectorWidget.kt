package com.foggyskies.petapp.presentation.ui.globalviews

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import kotlinx.coroutines.delay

//@SuppressLint("CoroutineCreationDuringComposition")
