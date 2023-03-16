//package com.foggyskies.petapp.presentation.ui.onboard
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//
//import android.content.Context
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectHorizontalDragGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonDefaults
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Alignment.Companion.BottomCenter
//import androidx.compose.ui.Alignment.Companion.CenterHorizontally
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.foggyskies.petapp.R
//import com.google.accompanist.pager.ExperimentalPagerApi
//import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.HorizontalPagerIndicator
//import com.google.accompanist.pager.rememberPagerState
//
//data class PagesDC(
//    var header: String,
//    var description: String,
//    var image: Int
//)
//
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun OnBoardScreen() {
//
//    val current_page = remember {
//        mutableStateOf(0)
//    }
//
//    val list_pages = listOf(
//        PagesDC(
//            header = "Joyfulness",
//            description = "Enjoy happy moments with family",
//            image = R.drawable.image_dog
//        ),
//        PagesDC(
//            header = "Convenience",
//            description = "All your favorite foods in one place with online reservation feature",
//            image = R.drawable.image_dog
//        ),
//        PagesDC(
//            header = "Enjoy & Reviews",
//            description = "Enjoy all your great food, review and share your experience.",
//            image = R.drawable.image_dog
//        )
//    )
//
//    val context = LocalContext.current
//    var isScrolling by remember {
//        mutableStateOf(true)
//    }
//    val pagerState = rememberPagerState()
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .pointerInput(Unit) {
//                detectHorizontalDragGestures { change, dragAmount ->
//                    if (dragAmount > 50)
//                        current_page.value++
//
//                }
//            }
//    ) {
//
//        HorizontalPager(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(1f),
//            count = 3,
//            state = pagerState,
//            userScrollEnabled = isScrolling,
//            verticalAlignment = Alignment.Top
//        ) { position ->
//            if (pagerState.currentPage == 2){
//                isScrolling = false
//            }
//            PagerScreen(onBoardingPage = list_pages[position])
//        }
//        HorizontalPagerIndicator(
//            modifier = Modifier
//                .padding(bottom = 50.dp)
//                .align(BottomCenter)
//                ,
//            pagerState = pagerState
//        )
//    }
//}
//
//@Composable
//fun PagerScreen(onBoardingPage: PagesDC) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
//    ) {
//        Image(
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .fillMaxHeight(0.76f),
//            painter = painterResource(id = onBoardingPage.image),
//            contentDescription = "Image"
//        )
//        Text(
//            modifier = Modifier
//                .fillMaxWidth(),
//            text = onBoardingPage.header,
//            fontSize = 28.sp,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center
//        )
//        Text(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 40.dp)
//                .padding(top = 20.dp),
//            text = onBoardingPage.description,
//            fontSize = MaterialTheme.typography.subtitle1.fontSize,
//            fontWeight = FontWeight.Medium,
//            textAlign = TextAlign.Center,
//            lineHeight = 24.sp
//        )
//    }
//}