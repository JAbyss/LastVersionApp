package com.foggyskies.petapp.presentation.ui.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.CircularTouchMenu
import com.foggyskies.petapp.presentation.ui.home.HomeViewModel
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.profile.entity.PetCardEntity
import com.foggyskies.petapp.presentation.ui.profile.human.HumanProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.human.StateProfile
import com.foggyskies.petapp.presentation.ui.profile.human.views.CircularStatuses
import com.foggyskies.petapp.presentation.ui.profile.human.views.MyLinkCard
import com.foggyskies.petapp.presentation.ui.profile.human.views.StatusWidget
import com.foggyskies.petapp.presentation.ui.profile.human.views.StoriesProfile
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys
import com.foggyskies.testingscrollcompose.presentation.ui.registation.customui.animation.animateDpOffsetAsState
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun ProfileScreen(
    nav_controller: NavHostController,
    viewModel: HumanProfileViewModel,
    viewModelHome: HomeViewModel
) {

    val context = LocalContext.current

    val state = rememberLazyListState()

    val backHandler = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

//    viewModel.swipableMenu.listIcon = listOf(
//        R.drawable.ic_menu_vack,
//        R.drawable.ic_walk,
//        R.drawable.ic_gamepad
//    )

    BackHandler {
        if (viewModel.stateProfile == StateProfile.PET) {
            viewModel.stateProfile = StateProfile.HUMAN
            viewModel.a
        } else
            nav_controller.navigate(nav_controller.backQueue[1].destination.route!!)
    }

    val density = LocalContext.current.resources.displayMetrics

    viewModel.density = density.density
    viewModel.swipableMenu.density = density.density
    viewModel.swipableMenu.sizeScreen =
        Size(width = density.widthPixels.toFloat(), height = density.heightPixels.toFloat())
    viewModel.a
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var offset = Offset.Zero

                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        offset = it
                        viewModel.swipableMenu.onDragStart(it)
                        viewModel.circularSelector.offset = viewModel.swipableMenu.offsetStartDp
                        viewModel.circularSelector.radius = viewModel.swipableMenu.radiusMenu
                    },
                    onDragEnd = {

                        val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
                            when (listDistance.indexOf(minDistance)) {
                                0 -> {
                                    backHandler?.onBackPressed()
                                }
                                1 -> {
                                    nav_controller?.navigate("AdsHomeless")
                                }
                                2 -> {
                                    nav_controller?.navigate("Chat")
                                }
                                3 -> {
                                    viewModelHome.sendAction("logOut")
                                    viewModelHome.viewModelScope.launch {
                                        viewModelHome.mainSocket?.close()
                                    }
                                    TOKEN = ""
                                    USERNAME = ""
                                    context.getSharedPreferences(
                                        "Token",
                                        Context.MODE_PRIVATE
                                    ).edit().clear().apply()
                                    context.getSharedPreferences(
                                        "User",
                                        Context.MODE_PRIVATE
                                    ).edit().clear().apply()
                                    nav_controller.navigate("Authorization") {
                                        popUpTo("Home") {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                            Toast
                                .makeText(
                                    context,
                                    "SELECTED ${listDistance.indexOf(minDistance)}",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            Log.e("SELECTOR", "SELECTED ${listDistance.indexOf(minDistance)}")
                        }
                        viewModel.swipableMenu.isTappedScreen = false
                        viewModel.viewModelScope.launch {
                            viewModel.swipableMenu.menuClosing()
                        }
                        viewModel.circularSelector.selectedTarget = StateCS.IDLE
                    },
                    onDrag = { change, dragAmount ->
                        offset = change.position

                        val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
                            viewModel.circularSelector.size =
                                viewModel.swipableMenu.radiusCircle
                            viewModel.circularSelector.selectedTargetOffset =
                                viewModel.swipableMenu.listOffsetsForCircle[listDistance.indexOf(
                                    minDistance
                                )]
                            viewModel.circularSelector.selectedTarget = StateCS.SELECTED
                        } else {
                            viewModel.circularSelector.selectedTarget = StateCS.IDLE
                            viewModel.circularSelector.offset =
                                viewModel.swipableMenu.offsetStartDp
                            viewModel.circularSelector.size =
                                viewModel.swipableMenu.radiusMenu
                        }
                    }
                )
            }
    ) {

        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

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
//                        .align(Center)
                        ) {

                            // Аватарка
                            AnimatedContent(targetState = viewModel.imageProfile) { targetImage ->
                                Image(
                                    painter = rememberImagePainter(data = targetImage),
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
                                                contentAlignment = Center,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(Color(0xFFDAE0E4))
                                                    .fillMaxWidth(1f)
                                                    .align(CenterHorizontally)
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
                                                    count = "324",
                                                    textValue = "публикации"
                                                )
                                                Spacer(modifier = Modifier.width(15.dp))
                                                PetProfileColumn(
                                                    count = "323.4k",
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
                        .align(BottomCenter)
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
            // Профиль Человека

            var items_list = listOf(
                PetCardEntity(
                    image = "http://194.67.93.244/media/petap/image_dog_for_preview.jpg",
                    name = "Лаки",
                    breed = "Дворняжка"
                ),
                PetCardEntity(
                    image = "http://194.67.93.244/media/petap/image_dog_for_preview.jpg",
                    name = "Азазела",
                    breed = "Дворняжка"
                ),
                PetCardEntity(
                    image = "http://194.67.93.244/media/petap/image_dog_for_preview.jpg",
                    name = "Яна",
                    breed = "Дворняжка"
                ),
                PetCardEntity(
                    image = "http://194.67.93.244/media/petap/image_dog_for_preview.jpg",
                    name = "Элька",
                    breed = "Дворняжка"
                ),
            )


            item {
                AnimatedVisibility(visible = viewModel.stateProfile == StateProfile.HUMAN) {
                    Column() {
                        LazyRow {
                            itemsIndexed(items_list) { index, item ->
                                Row {
//                    Spacer(modifier = Modifier.height(40.dp))
                                    PetsWidget(
                                        onClickPetCard = { name, image ->
                                            viewModel.stateProfile = StateProfile.PET
                                            viewModel.imageProfile = image
                                            viewModel.nameProfile = name
                                            viewModel.a
                                        },
                                        index,
                                        item,
                                        viewModel
                                    )
                                    Spacer(modifier = Modifier.height(30.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        DashBoardComposition(
                            viewModel,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(CenterHorizontally)
                        )
                    }
                }
            }
            item {
                AnimatedVisibility(visible = viewModel.stateProfile == StateProfile.PET) {
                    Column() {
                        LazyRow {
                            var list = listOf(
                                "123",
                                "321",
                                "123",
                                "123",
                                "321",
                                "123",
                                "123",
                                "321",
                                "123",
                            )

                            itemsIndexed(list) { index, item ->
                                StoriesProfile(
                                    index,
                                    list.lastIndex,
                                    modifier = Modifier
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            val test_list = listOf(
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
                "123",
                "321",
                "123",
            )
            if (viewModel.stateProfile == StateProfile.PET)
                items(test_list.windowed(3, 3, true)) { item ->

                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.image_dog),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.image_dog),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.image_dog),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                        )
                    }
                }
        }


        AnimatedVisibility(
            visible = viewModel.isMyContactClicked,
            modifier = Modifier
                .align(Center)
        ) {
            MyLinkCard(onClickClose = { viewModel.isMyContactClicked = false })
        }
        AnimatedVisibility(
            visible = viewModel.isStatusClicked,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Center)
        ) {
            CircularStatuses(
                onClickClose = { viewModel.isStatusClicked = false },
                onClickAdd = {
                    viewModel.nowSelectedStatus = it
                    viewModel.isStatusClicked = false
                },
                onClickStatus = {
                    viewModel.nowSelectedStatus = it
                    viewModel.isStatusClicked = false
                }
            )
        }

        AnimatedVisibility(
            visible = viewModel.stateProfile == StateProfile.PET,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .align(BottomCenter)
        ) {
            PetBottomMenu()
        }
        if (viewModel.swipableMenu.isTappedScreen)
            CircularTouchMenu(param = viewModel.swipableMenu, viewModel.circularSelector)
    }
}


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

//    AnimatedVisibility(visible = isExpandMenu) {
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
//    }
//    AnimatedVisibility(visible = isExpandMenu) {
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
//    }
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

@Composable
fun PetProfileColumn(
    count: String,
    textValue: String,
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        Box(
            contentAlignment = Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFFDAE0E4))
                .size(40.dp)
        ) {
            Text(
                text = count,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text = textValue,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PetsWidget(
    onClickPetCard: (String, String) -> Unit,
    index: Int,
    item: PetCardEntity,
    viewModel: HumanProfileViewModel
) {

    if (index == 0) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .size(width = 50.dp, height = 100.dp)
        ) {

            Text(
                text = "Добавить нового питомца",
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier
                    .rotate(-90f)
                    .align(Center)
                    .size(150.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
    }
    PetCard(
        item = item,
        onClickPetCard,
        viewModel
    )
    Spacer(modifier = Modifier.width(20.dp))
//        }
//    }
}

@ExperimentalCoilApi
@Composable
fun PetCard(
    item: PetCardEntity,
    onClickPetCard: (String, String) -> Unit,
    viewModel: HumanProfileViewModel
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .height(300.dp)
            .width(250.dp)
            .toggleable(
                value = true,
                enabled = !viewModel.swipableMenu.isMenuOpen,
                onValueChange = {
                    onClickPetCard(item.name, item.image)
                })

    ) {

        Image(
            painter = rememberImagePainter(data = item.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        Column(
            modifier = Modifier
                .padding(start = 15.dp, bottom = 15.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .fillMaxWidth(0.7f)
                .align(BottomStart)
        ) {
            Text(
                text = item.name,
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 15.dp, top = 10.dp)
            )
//            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = item.breed,
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(start = 15.dp, bottom = 10.dp)
            )
        }
    }
}

@Composable
fun DashBoardComposition(
    viewModel: HumanProfileViewModel,
    modifier: Modifier
) {

    val list_dashboard = mapOf(
//        "Способ оплаты" to R.drawable.ic_wallet_svgrepo_com,
        "Настройки" to R.drawable.ic_privacy,
        "Мои контакты" to R.drawable.ic_link
    )


    Column(modifier = modifier) {
        list_dashboard.forEachKeys { key, value, _ ->

            Button(
                onClick = {
                    if (key == "Мои контакты")
                        viewModel.isMyContactClicked = !viewModel.isMyContactClicked
                },
                enabled = !viewModel.swipableMenu.isMenuOpen,
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (key == "Настройки")
                                    Color(0xFF00C853)
                                else
                                    Color(0xFFFFD600)
                            )
                            .size(50.dp)
                    ) {

                        Icon(
                            painter = painterResource(id = value),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Center)
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = key,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4F4F4F),
                        modifier = Modifier
                            .align(CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}