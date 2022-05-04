package com.foggyskies.petapp.presentation.ui.profile.human

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
import com.foggyskies.petapp.presentation.ui.profile.entity.PetCardEntity
import com.foggyskies.petapp.presentation.ui.profile.human.views.CircularStatuses
import com.foggyskies.petapp.presentation.ui.profile.human.views.MyLinkCard
import com.foggyskies.petapp.presentation.ui.profile.human.views.PetsWidget
import com.foggyskies.petapp.presentation.ui.profile.human.views.StoriesProfile
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys
import com.foggyskies.testingscrollcompose.presentation.ui.registation.customui.animation.animateDpOffsetAsState
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun ProfileOtherUserScreen(
    nav_controller: NavHostController,
    viewModel: ProfileOtherUserViewModel
) {


    LaunchedEffect(key1 = Unit){
        viewModel.getPagesProfileByIdUser()
    }

    val context = LocalContext.current

    val state = rememberLazyListState()

    BackHandler {
        if (viewModel.stateProfile == StateProfile.PET) {
            viewModel.changeStateProfile(StateProfile.HUMAN)
        } else
            nav_controller.navigate(nav_controller.backQueue[1].destination.route!!)
    }

    val density = LocalContext.current.resources.displayMetrics

    viewModel.swipableMenu.density = density.density
    viewModel.swipableMenu.sizeScreen =
        Size(width = density.widthPixels.toFloat(), height = density.heightPixels.toFloat())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var offset = Offset.Zero

                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        offset = it
                        viewModel.swipableMenu.onDragStart(it)
                        viewModel.swipableMenu.startOffsetCS = viewModel.swipableMenu.offsetStartDp
                        viewModel.swipableMenu.radius = viewModel.swipableMenu.radiusMenu
                    },
                    onDragEnd = {

                        val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
                            when (listDistance.indexOf(minDistance)) {
                                0 -> {
                                    nav_controller?.navigate("Profile")
                                }
                                1 -> {
                                    nav_controller?.navigate("AdsHomeless")
                                }
                                2 -> {
                                    nav_controller?.navigate("Chat")
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
                        viewModel.swipableMenu.selectedTarget = StateCS.IDLE
                    },
                    onDrag = { change, dragAmount ->
                        offset = change.position

                        val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
                            (it - offset).getDistance()
                        }
                        val minDistance = listDistance.minOrNull()

                        if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
                            viewModel.swipableMenu.sizeCS =
                                viewModel.swipableMenu.radiusCircle
                            viewModel.swipableMenu.selectedTargetOffset =
                                viewModel.swipableMenu.listOffsetsForCircle[listDistance.indexOf(
                                    minDistance
                                )]
                            viewModel.swipableMenu.selectedTarget = StateCS.SELECTED
                        } else {
                            viewModel.swipableMenu.selectedTarget = StateCS.IDLE
                            viewModel.swipableMenu.startOffsetCS =
                                viewModel.swipableMenu.offsetStartDp
                            viewModel.swipableMenu.sizeCS =
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
                                                Spacer(modifier = Modifier.width(15.dp))
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_baseline_add_24),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .size(40.dp)
                                                        .background(Color(0xFFDAE0E4))
                                                        .clickable {  }
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
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xFF23262F))
                                    ) {

                                        Row(
                                            Modifier
                                                .padding(vertical = 7.dp, horizontal = 10.dp)
                                                .align(Alignment.Center)
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_sleep),
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(7.dp))
                                            Text(
                                                "Сплю",
                                                fontSize = 16.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
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

//            item {
//                AnimatedVisibility(visible = viewModel.stateProfile == StateProfile.HUMAN) {
//                    Column() {
//                        LazyRow {
//                            itemsIndexed(viewModel.listPostImages) { index, item ->
//                                Row {
//                                    PetsWidget(
//                                        onClickPetCard = { name, image ->
//                                            viewModel.stateProfile = StateProfile.PET
//                                            viewModel.imageProfile = image
//                                            viewModel.nameProfile = name
//                                        },
//                                        index,
//                                        item,
//                                        viewModel
//                                    )
//                                    Spacer(modifier = Modifier.height(30.dp))
//                                }
//                            }
//                        }
//                    }
//                }
//            }
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

        if (viewModel.swipableMenu.isTappedScreen)
            viewModel.swipableMenu.CircularTouchMenu(param = viewModel.swipableMenu)
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

@ExperimentalCoilApi
@Composable
fun PetCard(
    item: PetCardEntity,
    onClickPetCard: (String, String) -> Unit,
    viewModel: ProfileOtherUserViewModel
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
    viewModel: ProfileViewModel,
    modifier: Modifier
) {

    val list_dashboard = mapOf(
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