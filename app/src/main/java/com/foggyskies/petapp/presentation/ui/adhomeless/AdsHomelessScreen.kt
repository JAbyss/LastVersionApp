//package com.foggyskies.petapp.presentation.ui.adhomeless
//
//import android.content.Context
//import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.slideInHorizontally
//import androidx.compose.animation.slideOutHorizontally
//import androidx.compose.foundation.*
//import androidx.compose.foundation.gestures.Orientation
//import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
//import androidx.compose.foundation.gestures.rememberTransformableState
//import androidx.compose.foundation.gestures.transformable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.selection.toggleable
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment.Companion.BottomCenter
//import androidx.compose.ui.Alignment.Companion.BottomEnd
//import androidx.compose.ui.Alignment.Companion.BottomStart
//import androidx.compose.ui.Alignment.Companion.Center
//import androidx.compose.ui.Alignment.Companion.CenterHorizontally
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.drawscope.Fill
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewModelScope
//import androidx.navigation.NavHostController
//import coil.compose.rememberImagePainter
//import com.foggyskies.petapp.R
//import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity
//import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
//import com.foggyskies.petapp.presentation.ui.home.entity.StateCS
//import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlin.math.roundToInt
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun AdsHomelessScreen(
//    nav_controller: NavHostController,
//    viewModel: AdsHomelessViewModel
//) {
//
//    val context = LocalContext.current
//    val swipeableState = rememberSwipeableState(1)
//    val scope = rememberCoroutineScope()
//
//    val backHolder = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
//
//    viewModel.swipableMenu.listIcon = listOf(
//        ItemSwappableMenu(
//            Image = R.drawable.ic_menu_back, onValueSelected = {
//                backHolder?.onBackPressed()
//            },
//            offset = Offset(x = 10f, y = -70f)
//        ),
//        ItemSwappableMenu(
//            Image = R.drawable.ic_menu_profile, onValueSelected = {
//                nav_controller.navigate(NavTree.Profile.name)
//            },
//            offset = Offset(x = -50f, y = -45f)
//        ),
//        ItemSwappableMenu(
//            Image = R.drawable.ic_menu_home_1,
//            animationImages = listOf(
//                R.drawable.ic_menu_home_1,
//                R.drawable.ic_menu_home_2,
//                R.drawable.ic_menu_home_3,
//            ),
//            isAnimate = true,
//            onValueSelected = {
//                nav_controller.navigate(NavTree.Home.name)
//            },
//            offset = Offset(x = -70f, y = 10f),
//        ),
//    )
//
//    val displayMetrics = LocalContext.current.resources.displayMetrics
//
//    viewModel.swipableMenu.density = displayMetrics.density
//    viewModel.swipableMenu.sizeScreen =
//        Size(
//            width = displayMetrics.widthPixels.toFloat(),
//            height = displayMetrics.heightPixels.toFloat()
//        )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                var offset = Offset.Zero
//
//                detectDragGesturesAfterLongPress(
//                    onDragStart = {
//                        offset = it
//                        viewModel.swipableMenu.onDragStart(it, {})
//                        viewModel.circularSelector.startOffsetCS =
//                            viewModel.swipableMenu.offsetStartDp
//                        viewModel.circularSelector.radius = viewModel.swipableMenu.radiusMenu
//                    },
//                    onDragEnd = {
//                        if (viewModel.swipableMenu.isMenuOpen) {
//                            val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
//                                (it - offset).getDistance()
//                            }
//                            val minDistance = listDistance.minOrNull()
//
//                            if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
//                                //TODO
////                                viewModel.swipableMenu.listIcon[listDistance.indexOf(minDistance)].onValueSelected(nav_controller)
//                            }
//                            viewModel.swipableMenu.isTappedScreen = false
//                            viewModel.viewModelScope.launch {
//                                viewModel.swipableMenu.menuClosing()
//                            }
//                            viewModel.circularSelector.selectedTarget = StateCS.IDLE
//                        }
//                    },
//                    onDrag = { change, dragAmount ->
//                        offset = change.position
//
//                        val listDistance = viewModel.swipableMenu.listOffsetGlobal.map {
//                            (it - offset).getDistance()
//                        }
//                        val minDistance = listDistance.minOrNull()
//
//                        if (minDistance!! < viewModel.swipableMenu.radiusCircle) {
//                            viewModel.circularSelector.sizeCS =
//                                viewModel.swipableMenu.radiusCircle
//                            viewModel.circularSelector.selectedTargetOffset =
//                                viewModel.swipableMenu.listOffsetsForCircle[listDistance.indexOf(
//                                    minDistance
//                                )]
//                            viewModel.circularSelector.selectedTarget = StateCS.SELECTED
//                        } else {
//                            viewModel.circularSelector.selectedTarget = StateCS.IDLE
//                            viewModel.circularSelector.startOffsetCS =
//                                viewModel.swipableMenu.offsetStartDp
//                            viewModel.circularSelector.sizeCS =
//                                viewModel.swipableMenu.radiusMenu
//                        }
//                    }
//                )
//            }
//    ) {
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//
//            LazyColumn(
//                modifier = Modifier
////                    .fillMaxWidth()
//                    .align(CenterHorizontally)
//            ) {
//
//                itemsIndexed(viewModel.petAdsList) { index, pet ->
//
//                    var animation by remember {
//                        mutableStateOf(false)
//                    }
//
//                    LaunchedEffect(key1 = Unit) {
//                        delay(200 + (150 * index).toLong())
//                        animation = true
//                    }
//
//                    AnimatedVisibility(
//                        visible = animation,
//                        enter = slideInHorizontally(),
//                        exit = slideOutHorizontally()
//                    ) {
//
//                        OneLinearAdHomelessView(
//                            viewModel,
//                            pet = pet,
//                            context = context,
//                            modifier = Modifier
//                                .padding(5.dp)
//                                .clip(RoundedCornerShape(8.dp))
//                                .fillMaxWidth(0.95f)
//                                .background(Color(0xFFDAE0E4))
//                                .toggleable(
//                                    value = true,
//                                    enabled = !viewModel.swipableMenu.isMenuOpen,
//                                    onValueChange = {
//                                        viewModel.petAd = pet
//                                        scope.launch {
//                                            swipeableState.animateTo(0)
//                                        }
//                                    })
//                        )
//                    }
//                }
//            }
//        }
//        if (viewModel.isFullSizeImage)
//            FullSizeImage(
//                viewModel = viewModel,
//                Modifier
//                    .align(Center)
//            )
//        ExpandedPetCard(
//            item = viewModel.petAd,
//            swipeableState,
//            nav_controller,
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .align(BottomCenter),
//            toggleable = {
//                toggleable(
//                    value = true,
//                    enabled = !viewModel.swipableMenu.isMenuOpen,
//                    onValueChange = {})
//            }
//        )
//        if (viewModel.swipableMenu.isTappedScreen && !viewModel.isFullSizeImage)
//            viewModel.swipableMenu.CircularTouchMenu(param = viewModel.swipableMenu)
//    }
//}
//
//@Composable
//fun OneLinearAdHomelessView(
//    viewModel: AdsHomelessViewModel,
//    pet: AdHomelessEntity,
//    context: Context,
//    modifier: Modifier
//) {
//
//    Box() {
//
//        Column(
//            modifier = modifier
//        ) {
//
//            SwipeableImages(
//                viewModel,
//                pet,
//                modifier = Modifier
//                    .align(CenterHorizontally),
//                toggleable = { image ->
//                    toggleable(
//                        value = true,
//                        enabled = !viewModel.swipableMenu.isMenuOpen,
//                        onValueChange = {
//                            viewModel.changePhotoSize(
//                                image,
//                                AdsHomelessViewModel.StatePhotoSize.OPEN
//                            )
//                        })
//                }
//            )
//            Box(
//                modifier = Modifier
//                    .padding(
//                        horizontal = 20.dp,
//                        vertical = 10.dp
//                    )
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(Color.Transparent)
//                    .align(CenterHorizontally)
//            ) {
//
//                Row(
//                    modifier = Modifier
//                        .align(Center)
//                ) {
//
//                    Box(
//                        contentAlignment = Center,
//                        modifier = Modifier
//                            .padding(5.dp)
//                            .clip(CircleShape)
//                            .size(60.dp)
//                            .background(Color.White)
//                    ) {
//                        Text(
//                            text = pet.name!!,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Black,
//                            color = Color(0xFF22292E),
//                        )
//                    }
//                    Box(
//                        contentAlignment = Center,
//                        modifier = Modifier
//                            .padding(5.dp)
//                            .clip(CircleShape)
//                            .size(60.dp)
//                            .background(Color.White)
//                    ) {
//                        Icon(
//                            painter = painterResource(
//                                id = if (pet.gender == "Самец")
//                                    R.drawable.ic_male
//                                else
//                                    R.drawable.ic_female
//                            ),
//                            contentDescription = null,
//                            modifier = Modifier.size(30.dp),
//                            Color(0xFF001A72)
//                        )
//                    }
//                    if (pet.neuter == true)
//                        Box(
//                            contentAlignment = Center,
//                            modifier = Modifier
//                                .padding(5.dp)
//                                .clip(CircleShape)
//                                .size(60.dp)
//                                .background(Color.White)
//                        ) {
//                            Icon(
//                                painter = painterResource(
//                                    id = R.drawable.ic_neuter
//                                ),
//                                contentDescription = null,
//                                modifier = Modifier.size(30.dp),
//                                Color(0xFF001A72)
//                            )
//                        }
//                    Box(
//                        contentAlignment = Center,
//                        modifier = Modifier
//                            .padding(5.dp)
//                            .clip(CircleShape)
//                            .size(60.dp)
//                            .background(Color.White)
//                    ) {
//                        Text(
//                            text = pet.old,
//                            color = Color(0xFF54B175),
//                            fontSize = 16.sp,
//                            textAlign = TextAlign.Center,
//                            fontWeight = FontWeight.SemiBold,
//                        )
//                    }
//                }
//            }
//        }
//        Canvas(
//            modifier = Modifier
//                .padding(5.dp)
//        ) {
//
//            val path = Path()
//            path.moveTo(0.0f, 200.0f)
//            path.lineTo(200.0f, 0.0f)
//            path.lineTo(300.0f, 0.0f)
//            path.lineTo(0.0f, 300.0f)
//            path.lineTo(0.0f, 200.0f)
//
//            drawPath(
//                path = path,
//                color = Color.Red,
//                style = Fill,
//            )
//        }
//        Text(
//            text = pet.previewLabel,
//            fontSize = 16.sp,
//            color = Color.White,
//            modifier = Modifier
//                .padding(top = 40.dp, start = 5.dp)
//                .rotate(-45f)
//        )
//        Text(
//            text = pet.breed,
//            fontSize = 16.sp,
//            color = Color.White,
//            modifier = Modifier
//                .offset(x = (-20).dp)
//                .padding(bottom = 35.dp)
//                .rotate(-90f)
//                .align(BottomStart)
//        )
//    }
//}
//
//@Composable
//fun PetFields(
//    text: String = "",
//    icon: Int = 0,
//    modifier: Modifier
//) {
//
//    Box(
//        contentAlignment = Center,
//        modifier = modifier
//    ) {
//        if (text.isEmpty())
//            Image(
//                painter = painterResource(id = icon),
//                contentDescription = null
//            )
//        else
//            Text(
//                text = text,
//                color = Color(0xFF54B175),
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center,
//                fontWeight = FontWeight.SemiBold,
//            )
//    }
//
//}
//
//@Composable
//fun FullSizeImage(
//    viewModel: AdsHomelessViewModel,
//    modifier: Modifier
//) {
//
//    var scale by remember { mutableStateOf(1f) }
//    var rotation by remember { mutableStateOf(0f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
//        scale *= zoomChange
//        rotation += rotationChange
//        offset += offsetChange
//    }
//
//    Box(
//        modifier = modifier
//    ) {
//        Image(
//            painter = viewModel.selectedImage!!,
//            contentDescription = null,
//            modifier = Modifier
//                .graphicsLayer(
//                    scaleX = scale,
//                    scaleY = scale,
//                    rotationZ = rotation,
//                    translationX = offset.x,
//                    translationY = offset.y
//                )
//                .transformable(state = state)
////                .fillMaxWidth()
//                .clip(RoundedCornerShape(20.dp))
//                .border(2.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
//                .clickable {
//                    viewModel.changePhotoSize(state = AdsHomelessViewModel.StatePhotoSize.CLOSE)
//                }
//        )
//    }
//}
//
//@ExperimentalMaterialApi
//@Composable
//fun ExpandedPetCard(
//    item: AdHomelessEntity,
//    swipeableState: SwipeableState<Int>,
//    nav_controller: NavHostController,
//    modifier: Modifier,
//    toggleable: Modifier.() -> Modifier
//) {
//
//    val squareSize = 400.dp
//
//    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
//    val anchors = mapOf(
//        0f to 0,
//        sizePx to 1
//    ) // Maps anchor points (in px) to states
//
//    Box(
//        modifier = modifier
//            .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
//            .swipeable(
//                state = swipeableState,
//                anchors = anchors,
//                thresholds = { _, _ -> FractionalThreshold(0.3f) },
//                orientation = Orientation.Vertical
//            )
//    ) {
//        Column() {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//
//                Image(
//                    painter = rememberImagePainter(data = item.author.image),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
////                            .offset(y = 30.dp)
//                        .padding(bottom = 10.dp)
//                        .clip(CircleShape)
//                        .size(50.dp)
//                        .align(BottomEnd)
//                        .toggleable()
//                )
//            }
//            Box(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(Color.White)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .padding(15.dp)
//                ) {
//                    Text(
//                        text = item.name,
//                        fontSize = 20.sp,
//                        color = Color.Black,
//                        fontWeight = FontWeight.Black,
//                        modifier = Modifier
//                            .align(CenterHorizontally)
//                    )
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ) {
//
//                        Box(
//                            contentAlignment = Center,
//                            modifier = Modifier
//                                .padding(5.dp)
//                                .clip(CircleShape)
//                                .size(60.dp)
//                                .weight(1f)
//                                .background(Color(0xFFDAE0E4))
//                        ) {
//                            Icon(
//                                painter = painterResource(
//                                    id = if (item.gender == "Самец")
//                                        R.drawable.ic_male
//                                    else
//                                        R.drawable.ic_female
//                                ),
//                                contentDescription = null,
//                                modifier = Modifier.size(30.dp),
//                                Color(0xFF001A72)
//                            )
//                        }
//                        if (item.neuter)
//                            Box(
//                                contentAlignment = Center,
//                                modifier = Modifier
//                                    .padding(5.dp)
//                                    .clip(CircleShape)
//                                    .size(60.dp)
//                                    .weight(1f)
//                                    .background(Color(0xFFDAE0E4))
//                            ) {
//                                Icon(
//                                    painter = painterResource(
//                                        id = R.drawable.ic_neuter
//                                    ),
//                                    contentDescription = null,
//                                    modifier = Modifier.size(30.dp),
//                                    Color(0xFF001A72)
//                                )
//                            }
//                        Box(
//                            contentAlignment = Center,
//                            modifier = Modifier
//                                .padding(5.dp)
//                                .clip(CircleShape)
//                                .size(60.dp)
//                                .weight(1f)
//                                .background(Color(0xFFDAE0E4))
//                        ) {
//                            Text(
//                                text = item.old,
//                                color = Color(0xFF54B175),
//                                fontSize = 16.sp,
//                                textAlign = TextAlign.Center,
//                                fontWeight = FontWeight.SemiBold,
//                            )
//                        }
//                    }
//
//                    Text(
//                        text = "История",
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 18.sp,
//                        color = Color.Black
//                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Text(
//                        text = item.description,
//                        fontWeight = FontWeight.Light,
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    )
//                }
//            }
//        }
//    }
//}