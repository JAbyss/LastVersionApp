package com.foggyskies.petapp.presentation.ui.profile.human.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.human.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.human.StateProfile

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
fun LazyListScope.HeadProfile(viewModel: ProfileViewModel, state: LazyListState) {

    stickyHeader {
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedVisibility(visible = viewModel.isVisibleInfoUser || state.firstVisibleItemIndex == 0) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(0.9f)
                ) {

                    // Аватарка
                    AnimatedContent(targetState = viewModel.imageProfile) { targetImage ->
                        Image(
                            painter = rememberImagePainter(data = "http://${MainActivity.MAINENDPOINT}/images/$targetImage"),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        AnimatedContent(
                            targetState = viewModel.nameProfile
                        ) { value ->

                            if (viewModel.stateProfile == StateProfile.HUMAN)
                                Text(
                                    text = value,
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                )
                            else
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xFFDAE0E4))
                                            .fillMaxWidth(1f)
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text = value,
                                            fontSize = 18.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 5.dp
                                                )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row() {
                                        PetProfileColumn(
                                            count = viewModel.selectedPage.countContents,
                                            textValue = "публикации"
                                        )
                                        Spacer(modifier = Modifier.width(15.dp))
                                        PetProfileColumn(
                                            count = viewModel.selectedPage.countSubscribers,
                                            textValue = "подписчиков"
                                        )
                                    }
                                }

                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        // Статус виджет
                        AnimatedVisibility(
                            visible = !viewModel.isStatusClicked && viewModel.nameProfile == "JAbyss",
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            StatusWidget(
                                value = viewModel.nowSelectedStatus,
                                icon = R.drawable.ic_sleep,
                                onClick = {
                                    viewModel.isStatusClicked = !viewModel.isStatusClicked
                                },
                                viewModel
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.firstVisibleItemIndex >= 3,
            modifier = Modifier
//                .align(Alignment.BottomCenter)
        ) {

            val rotation by animateFloatAsState(targetValue = if (viewModel.isVisibleInfoUser) 0f else 180f)

            Icon(
                painter = painterResource(id = R.drawable.ic_round_expand_less_24),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = 0.dp, y = (-15).dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .size(30.dp)
                    .background(Color(0xFFE5ECF0))
                    .toggleable(
                        value = true,
                        enabled = !viewModel.swipableMenu.isMenuOpen,
                        onValueChange = {
                            viewModel.isVisibleInfoUser = !viewModel.isVisibleInfoUser
                        })
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

}