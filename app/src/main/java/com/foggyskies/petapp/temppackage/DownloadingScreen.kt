package com.foggyskies.petapp.temppackage

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.entity.FileDC
import com.foggyskies.petapp.presentation.ui.globalviews.FullInvisibleBack
import com.foggyskies.petapp.workers.FirstState
import com.foggyskies.petapp.workers.SecondState
import com.foggyskies.petapp.workers.UploadFileViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview
@Composable
fun Preview() {
    val viewModel = UploadFileViewModel()

    viewModel.nowPercentage.value = 47

    OneDownloadingItem(
        item = FileDC(
            name = "Arcane",
            size = "10mb",
            type = "mkv",
            path = ""
        ),
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

@Preview
@Composable
fun DownloadScreenMiniPreview() {
    val viewModel = UploadFileViewModel()
    viewModel.nowPercentage.value = 47

    Box {
        DownloadingScreenMini(viewModel = viewModel)
    }
}

@Composable
fun OneDownloadingItem(
    item: FileDC,
    idUpload: String? = null,
    viewModel: UploadFileViewModel,
    nowUploading: Boolean = false,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray)
                .align(CenterVertically)
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_file
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .size(55.dp)
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
        Column(
            modifier = Modifier
                .padding(start = 7.dp)
        ) {
            Spacer(modifier = Modifier.height(7.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
//                        .weight(2f)
                        .wrapContentWidth(Start)
                        .alignByBaseline()
                )
                Text(
                    text = item.size,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(start = 7.dp)
                        .alignByBaseline()
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .weight(1f)
                        .wrapContentWidth(End)
                        .clickable {
                            if (nowUploading)
                                viewModel.cancelNowLoadAndStartNext()
                            else
                                viewModel.removeFileInQueue(idUpload!!)

                        }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (nowUploading)
                viewModel.let {
                    it.nowPercentage.value.let {
                        TimeLine(it, 100, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(7.dp))
                        Row() {
                            Text(
                                text = "$it%",
                                fontSize = 14.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TimeLine(now_count: Int, max_count: Int, modifier: Modifier = Modifier) {

    val width = remember {
        Animatable(0f)
    }

    val now_status: Float = (1f / max_count) * now_count

    val scope = rememberCoroutineScope()

    scope.launch {
        width.animateTo(now_status)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(CircleShape)
            .background(Color(0xFFDDDDDD))
    )
    {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF47BDFF))
                .fillMaxWidth(
                    width.value
                )
                .height(5.dp)
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.DownloadingScreen(viewModel: UploadFileViewModel) {

    val modifier = Modifier
        .padding(7.dp)
        .fillMaxWidth(0.8f)
        .align(Center)
    val height = LocalConfiguration.current.screenHeightDp
    val fixedHeight = remember {
        derivedStateOf {
            (height * 0.7f).dp
        }
    }
    FullInvisibleBack(onBackClick = {
        viewModel.closeFullDownload()
    }) {
        LazyColumn(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(0.85f)
                .requiredHeightIn(max = fixedHeight.value)
                .align(Center)
                .background(Color.White)
        ) {
            stickyHeader {
                viewModel.nowUploadingFile.value?.let {
                    OneDownloadingItem(
                        item = it, viewModel = viewModel, nowUploading = true, modifier = modifier
                    )
                }
            }

            items(viewModel.listQueueFile.value) { file ->
                OneDownloadingItem(file.toFileDC(), file.idUpload, viewModel, modifier = modifier)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BoxScope.DownloadingScreenMini(
    viewModel: UploadFileViewModel
) {
    val width = remember {
        Animatable(0f)
    }
    viewModel.nowPercentage.value.let { it ->

        val now_status: Float = (360f / 100) * it

        val scope = rememberCoroutineScope()

        scope.launch {
            width.animateTo(now_status)
        }

        Box(
            Modifier
//                .clip(CutCornerShape(20.dp))
                .clip(CircleShape)
                .size(55.dp)
                .background(Color.White)
                .clickable {
                    scope.launch {
                        viewModel.stateDownload.value.let {
                            when (it) {
                                is FirstState -> {
                                    it.action()
                                }
                                is SecondState -> {
                                    it.action()
                                }
                            }
                        }
                    }

                }
        ) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                drawArc(
                    color = Color(0xFF47BDFF),
                    startAngle = 0f,
                    sweepAngle = width.value,
                    useCenter = false,
                    size = size,
                    style = Stroke(width = 10f, cap = StrokeCap.Round)
                )
            }
            AnimatedContent(
                targetState = it,
                transitionSpec = {
                    if (targetState > initialState) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInVertically { height -> -height } + fadeIn() with
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                },
                modifier = Modifier
                    .padding(10.dp)
                    .align(Center)
            ) { animValue ->
                Text(text = "$animValue%")
            }
        }
    }
}