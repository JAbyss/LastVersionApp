//package com.foggyskies.petapp.presentation.ui.globalviews
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
//import com.foggyskies.petapp.presentation.ui.home.widgets.post.SelectedPostWithIdPageProfile
//import com.foggyskies.petapp.presentation.ui.profile.ContentPreviewDC
//import com.foggyskies.petapp.presentation.ui.profile.MENUS
//
////@Preview
////@Composable
////fun PreviewHomeScreen() {
////    NewHomeScreen()
////}
//
//@Composable
//fun NewHomeScreen() {
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//
//        LazyColumn() {
//
//            item {
//                Spacer(modifier = Modifier.height(20.dp))
//            }
//
//            items(10) {
//                var a = PostScreenHandler()
//
//                var b = SelectedPostWithIdPageProfile(
//                    idPageProfile = "",
//                    item = ContentPreviewDC(
//                        id = "",
//                        address = ""
//                    ),
//                    author = "JAbyss",
//                    image = "images/avatars/avatar_6276beb27a983c4bd6c57fe1.jpg",
//                    countComets = "",
//                    countLikes = ""
//                )
//
//                a.selectPost(
//                    b,
//                    action = {
//                    }
//                )
//
//                a.PostScreen(onLongPress = {})
//                Spacer(modifier = Modifier.height(20.dp))
//            }
//        }
//
//    }
//
//}
//
//@Composable
//fun OnePostView() {
//
//    var a = PostScreenHandler()
//
//    a.PostScreen(onLongPress = {})
//
////    Box(
////        modifier = Modifier
////            .fillMaxSize(0.8f)
////            .border(2.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
////    ) {
////
////
////
////    }
//
//}