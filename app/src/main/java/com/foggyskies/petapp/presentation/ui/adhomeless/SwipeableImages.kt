//package com.foggyskies.petapp.presentation.ui.adhomeless
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.graphics.Insets
//import android.os.Build
//import android.util.DisplayMetrics
//import android.util.Log
//import android.view.WindowInsets
//import android.view.WindowMetrics
//import androidx.compose.animation.animateContentSize
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Alignment.Companion.CenterHorizontally
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import coil.annotation.ExperimentalCoilApi
//import coil.compose.ImagePainter
//import coil.compose.rememberImagePainter
//import com.foggyskies.petapp.R
//import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity
//import kotlinx.coroutines.launch
////
////@ExperimentalFoundationApi
////@ExperimentalMaterialApi
////@Preview
////@Composable
////fun PreviewImageSwipe() {
////
////    Box(
////        modifier = Modifier
////            .fillMaxSize()
////    ) {
////        SwipeableImages()
////    }
////}
//
////@SuppressLint("CoroutineCreationDuringComposition")
////@Composable
////fun SwipeableImages(
////    viewModel: AdsHomelessViewModel,
////    pet: AdHomelessEntity,
////    modifier: Modifier,
////    toggleable: Modifier.(ImagePainter) -> Modifier
////) {
////
////    val display_metrics = LocalContext.current.resources.displayMetrics
////    val context = LocalContext.current
////
////    val width: Int =
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
////            val windowMetrics: WindowMetrics =
////                (context as Activity).windowManager.currentWindowMetrics
////            val insets: Insets = windowMetrics.windowInsets
////                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
////            windowMetrics.bounds.width() - insets.left - insets.right
////        } else {
////            val displayMetrics = DisplayMetrics()
////            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
////            displayMetrics.widthPixels
////        }
////
////    val width_config = (width / display_metrics.density).toInt()
////
////    val sizeOneBox = (width_config * 0.95 / 2.5f).dp
////    val halfBoxSize = with(LocalDensity.current) { (sizeOneBox.toPx() / 2) }
////    val halfBoxForSecondItem = with(LocalDensity.current) { ((sizeOneBox.toPx() + 35.dp.toPx())) }
////
////    val images = pet.image
////
////    val state = rememberLazyListState()
////
////    stateWorker(state, halfBoxSize, halfBoxForSecondItem)
////
////    Column(
////        modifier = modifier
////    ) {
////
////        val selectedItem = remember {
////            mutableStateOf(images.size / 2)
////        }
////        Spacer(modifier = Modifier.height(7.dp))
////        Row(
////            verticalAlignment = Alignment.CenterVertically,
////            modifier = Modifier
////                .height(7.dp)
////                .align(CenterHorizontally)
////        ) {
////            repeat(images.size) { index ->
////                Box(
////                    modifier = Modifier
////                        .clip(CircleShape)
////                        .animateContentSize(
////                            animationSpec = tween(
////                                durationMillis = 100,
////                                delayMillis = 0
////                            )
////                        )
////                        .size(
////                            if (selectedItem.value == index)
////                                7.dp
////                            else
////                                4.dp
////                        )
////                        .background(
////                            if (selectedItem.value == index)
////                                Color(0xFF54B175)
////                            else
////                                Color(0xFFC5C5C7)
////                        )
////                )
////                if (index != images.lastIndex)
////                    Spacer(modifier = Modifier.width(3.dp))
////            }
////        }
////
////        Spacer(modifier = Modifier.height(7.dp))
////
////        LazyRow(
////            verticalAlignment = Alignment.CenterVertically,
////            state = state,
////            modifier = Modifier
////                .height(150.dp)
////                .fillMaxWidth()
////        ) {
////
////            itemsIndexed(images) { index, value ->
////
////                val image = rememberImagePainter(data = value)
////
////                if (state.firstVisibleItemIndex == index && state.firstVisibleItemScrollOffset == 0) selectedItem.value =
////                    index
////                else if (state.firstVisibleItemIndex + 1 == index && state.firstVisibleItemScrollOffset != 0) selectedItem.value =
////                    index
////
////                if (index == 0)
////                    Spacer(modifier = Modifier.width(sizeOneBox / 2 + 35.dp))
////                Box(
////                    modifier = Modifier
////                        .clip(RoundedCornerShape(20.dp))
////                        .animateContentSize(
////                            animationSpec = tween(
////                                durationMillis = 500,
////                                delayMillis = 0
////                            )
////                        )
////                        .size(
////                            width = sizeOneBox,
////                            height =
////                            if (selectedItem.value == index)
////                                150.dp
////                            else
////                                100.dp
////                        )
////                ) {
////                    Image(
////                        painter = image,
////                        contentDescription = null,
////                        contentScale = ContentScale.Crop,
////                        modifier = Modifier
////                            .fillMaxSize()
////                            .toggleable(image)
//////                            .clickable {
//////                                viewModel.changePhotoSize(image, AdsHomelessViewModel.StatePhotoSize.OPEN)
//////                            }
////                    )
////                }
////                if (index == images.lastIndex)
////                    Spacer(modifier = Modifier.width(sizeOneBox / 2 + 35.dp))
////                else
////                    Spacer(modifier = Modifier.width(35.dp))
////            }
////
////        }
////
////    }
////}
//
//data class LastItemDC(
//    var last_item: Int = 0
//)
//
//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//fun stateWorker(
//    state: LazyListState,
//    halfBoxSize: Float,
//    halfBoxForSecondItem: Float
//) {
//
//    val scope = rememberCoroutineScope()
//    val last_item = remember {
//        mutableStateOf(LastItemDC())
//    }
//
//    if (state.isScrollInProgress) {
//        last_item.value.last_item = state.firstVisibleItemIndex
//    }
//
//    if (state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0) {
//        last_item.value.last_item = state.firstVisibleItemIndex
//    } else if (state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset != 0) {
//        scope.launch {
//            state.scrollToItem(last_item.value.last_item, halfBoxForSecondItem.toInt())
//        }
//    } else if (
//        state.firstVisibleItemScrollOffset < halfBoxSize.toInt()
//    ) {
//        scope.launch {
//            state.scrollToItem(last_item.value.last_item, halfBoxSize.toInt())
//            last_item.value.last_item = state.firstVisibleItemIndex
//        }
//    } else if (
//        state.firstVisibleItemScrollOffset > halfBoxSize.toInt()
//    ) {
//        scope.launch {
//            state.scrollToItem(last_item.value.last_item, halfBoxSize.toInt())
//            last_item.value.last_item = state.firstVisibleItemIndex
//        }
//    }
//}