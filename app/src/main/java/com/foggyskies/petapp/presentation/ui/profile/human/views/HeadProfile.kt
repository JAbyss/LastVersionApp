package com.foggyskies.petapp.presentation.ui.profile.human.views

import android.content.Context
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.*
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.human.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.human.StateProfile
import com.foggyskies.petapp.presentation.ui.profile.human.UserMode
import com.foggyskies.petapp.routs.Routes
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
fun LazyListScope.HeadProfile(
    viewModel: ProfileViewModel,
    state: LazyListState,
    context: Context,
    stateSheet: ModalBottomSheetState
) {
//    val context = LocalContext.current

    stickyHeader {
        SideEffect {
            Log.e("HEAD_PROFILE", "УТЕЧКА")
        }
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedVisibility(visible = viewModel.isVisibleInfoUser || state.firstVisibleItemIndex == 0) {
            val scope = rememberCoroutineScope()
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

                        val request = ImageRequest.Builder(context)
                            .data("${Routes.SERVER.REQUESTS.BASE_URL}/$targetImage")
                            .crossfade(true)
                            .build()
//                        loader.enqueue(request)
                        // TODO Добавить загрузку аватарки для профиля.
                        AsyncImage(
                            model = request,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                                .clickable {
                                    if (viewModel.userMode == UserMode.OWNER)
                                        scope.launch {
                                            viewModel.profileHandler.getCameraImages(context)
                                            stateSheet.show()
                                        }
                                }
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
                                    if (viewModel.userMode == UserMode.OWNER)
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