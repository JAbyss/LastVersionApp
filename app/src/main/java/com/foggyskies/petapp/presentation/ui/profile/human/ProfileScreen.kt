package com.foggyskies.petapp.presentation.ui.profile.human

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.globalviews.FullInvisibleBack
import com.foggyskies.petapp.presentation.ui.profile.human.views.*
import com.foggyskies.petapp.routs.Routes
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun ProfileScreen(
    nav_controller: NavHostController, viewModel: ProfileViewModel, msViewModel: MainSocketViewModel
) {
    LaunchedEffect(key1 = isNetworkAvailable.value) {
        if (viewModel.userMode == UserMode.OWNER) {
            viewModel.checkInternet(viewModel::getAvatar)
            msViewModel.sendAction("getPagesProfile|")
        }
//        if (launcher == null)
    }

    val context = LocalContext.current


    val state = rememberLazyListState()

    BackHandler {
        if (viewModel.stateProfile == StateProfile.PET) {
            viewModel.changeStateProfile(StateProfile.HUMAN)
        } else
            nav_controller.navigate(nav_controller.backQueue[nav_controller.backQueue.lastIndex - 1].destination.route!!)
    }

    val density = LocalContext.current.resources.displayMetrics

    LaunchedEffect(key1 = Unit) {
        viewModel.density = density.density
        viewModel.swipableMenu.density = density.density
        viewModel.swipableMenu.sizeScreen =
            Size(width = density.widthPixels.toFloat(), height = density.heightPixels.toFloat())
        viewModel.swipableMenu.navController = nav_controller
    }


    val stateSheet = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    SideEffect {
        Log.e("SIDE EFFECT FOR TEST", "TESTING")
    }

    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        sheetContent = {
            Box(modifier = Modifier.fillMaxSize()) {

                viewModel.profileHandler.GalleryImageSelector(
                    listItems = viewModel.profileHandler.listPath,
                    stateSheet = stateSheet,
                    onSelectedImage = {
                        scope.launch {
                            stateSheet.hide()
                        }
                        val bm = BitmapFactory.decodeFile(it)
                        val string64 = encodeToBase64(bm)
                        if (viewModel.stateProfile == StateProfile.HUMAN) viewModel.changeAvatar(
                            string64
                        )
                        else viewModel.changeAvatarPageProfile(string64)
                    },
                    bottomSheetState = stateSheet
                )
            }
        },
        sheetState = stateSheet,
    ) {

        Box(
            modifier = viewModel.swipableMenu.Modifier(
                Modifier.fillMaxSize()
            )
        ) {

            LazyColumn(
                state = state,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                HeadProfile(viewModel = viewModel, state = state, context, stateSheet)

                MainProfilePages(viewModel, msViewModel)

                item {
                    AnimatedVisibility(visible = viewModel.stateProfile == StateProfile.PET) {
                        Column() {
                            LazyRow {
                                val list = listOf(
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
                                        index, list.lastIndex, modifier = Modifier
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                if (viewModel.stateProfile == StateProfile.PET) items(
                    viewModel.listPostImages.windowed(
                        3,
                        3,
                        true
                    )
                ) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        if (item.isNotEmpty()) AsyncImage(model = "${Routes.SERVER.REQUESTS.BASE_URL}/${item[0].address}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                                .clickable {
//                                            viewModel.viewModelScope.launch {
//                                                viewModel.postScreenHandler.selectPost(
//                                                    item[0], viewModel.selectedPage,
//                                                    action = {
//                                                        viewModel.swipableMenu.isReadyMenu = false
//                                                        viewModel.isVisiblePostWindow = true
//                                                    }
//                                                )
//                                            }
                                })
                        if (item.size > 1) AsyncImage(model = "${Routes.SERVER.REQUESTS.BASE_URL}/${item[1].address}",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
//                                contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                                .clickable {
//                                            viewModel.viewModelScope.launch {
//                                                viewModel.postScreenHandler.selectPost(
//                                                    item[1], viewModel.selectedPage,
//                                                    action = {
//                                                        viewModel.swipableMenu.isReadyMenu = false
//                                                        viewModel.isVisiblePostWindow = true
//                                                    }
//                                                )
//                                            }
                                })

                        if (item.size > 2)

                            AsyncImage(model = "${Routes.SERVER.REQUESTS.BASE_URL}/${item[2].address}",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
//                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(2.5.dp)
                                    .weight(1f)
                                    .clickable {
//                                            viewModel.viewModelScope.launch {
//                                                viewModel.postScreenHandler.selectPost(
//                                                    item[2], viewModel.selectedPage,
//                                                    action = {
//                                                        viewModel.swipableMenu.isReadyMenu = false
//                                                        viewModel.isVisiblePostWindow = true
//                                                    }
//                                                )
//                                            }
                                    })
                    }
                }
            }
            AnimatedVisibility(
                visible = viewModel.isMyContactClicked, modifier = Modifier.align(Center)
            ) {
                MyLinkCard(onClickClose = { viewModel.isMyContactClicked = false })
            }
            AnimatedVisibility(
                visible = viewModel.isStatusClicked,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Center)
            ) {
                CircularStatuses(onClickClose = { viewModel.isStatusClicked = false },
                    onClickAdd = {
                        viewModel.nowSelectedStatus = it
                        viewModel.isStatusClicked = false
                    },
                    onClickStatus = {
                        viewModel.nowSelectedStatus = it
                        viewModel.isStatusClicked = false
                    })
            }
            AnimatedVisibility(
                visible = viewModel.isAddingNewCard,
                modifier = Modifier.align(Center),
            ) {

                FullInvisibleBack(onBackClick = { viewModel.isAddingNewCard = false }) {

                    PetCard(
                        item = PageProfileFormattedDC(
                            id = "",
                            image = "",
                            title = "Заголовок",
                            description = "Описание",
                            countContents = "",
                            countSubscribers = ""
                        ),
                        onClickPetCard = { _, _ ->
//                            viewModel.profileHandler.openGallery()
                        },
                        viewModel = viewModel,
                        creatingModifier = Modifier
                            .border(
                                3.dp, Color.Gray, RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .height(345.dp)
                            .width(287.5.dp)
                            .align(Center)
                    )
                }
            }
            AnimatedVisibility(
                visible = viewModel.menuHelper.getMenuVisibleValue(MENUS.NEWCONTENT).value,
                modifier = Modifier.align(Center)
            ) {
                FullInvisibleBack(onBackClick = { viewModel.menuHelper.changeVisibilityMenu(MENUS.NEWCONTENT) }) {
                    AddNewImage(viewModel)
                }
            }
            //FIXME ХУЙ ЗНАЕТ ЧТО ТУТ
//            AnimatedVisibility(
//                visible = viewModel.isVisiblePostWindow,
//                enter = fadeIn(),
//                exit = fadeOut(),
//                modifier = Modifier
//                    .align(Center)
//                    .testTag("Photos")
//            ) {
//                viewModel.postScreenHandler.PostScreen(onLongPress = {
//
//                    viewModel.swipableMenu.isReadyMenu = false
//                    viewModel.isVisiblePostWindow = false
//                    viewModel.photoScreenClosed()
//
//                }, statePost = statePost)
//            }
            if (viewModel.swipableMenu.isTappedScreen) viewModel.swipableMenu.CircularTouchMenu(
                param = viewModel.swipableMenu
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.AddNewImage(
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    BackHandler(onBack = {
        focusManager.clearFocus()
        viewModel.menuHelper.changeVisibilityMenu(
            MENUS.NEWCONTENT
        )
    })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .align(Center)
            .fillMaxWidth(0.8f)
    ) {
        val image_url = remember {
            mutableStateOf<Uri?>(null)
        }
        Box(
            Modifier
                .border(3.dp, Color.LightGray, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {

            Column(modifier = Modifier.padding(7.dp)) {


                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    image_url.value = uri
                }
                if (image_url.value != null) image_url.let {
                    val image_test = mutableStateOf<Bitmap?>(null)

                    if (Build.VERSION.SDK_INT < 28) {
                        image_test.value =
                            MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
                        image_test.value = ImageDecoder.decodeBitmap(source)
                    }
                    image_test.value?.let { bit ->
                        Image(bitmap = bit.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
//                                .height(150.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f)
                                .clickable {
//                                    launcher.
                                    launcher.launch("image/*")
                                }
//                                .size(100.dp, 70.dp)
                        )
                    }
                }
                else Box(modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
//                        .fillMaxWidth(0.8f)
                    .background(Color.White)
                    .clickable {
                        launcher.launch("image/*")
                        focusManager.clearFocus()
                    })
                Spacer(modifier = Modifier.height(20.dp))
//            var value by remember { mutableStateOf(TextFieldValue()) }
                ClosedComposedFun {
//                    var text by remember {
//                        mutableStateOf("")
//                    }
                    BasicTextField(value = viewModel.descriptionMenuNewContentHandler,
                        onValueChange = {
                            viewModel.descriptionMenuNewContent = it
                            viewModel.descriptionMenuNewContentHandler
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp
                        ),

                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                            .onFocusEvent {
                                viewModel.focusState = it.isFocused
                            })
                }
                Spacer(modifier = Modifier.height(7.dp))
            }
        }
        IconButton(
            onClick = {
                image_url.value?.let {
                    val image_test = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(
                            context.contentResolver, it
                        )
                    } else {
                        val source = ImageDecoder.createSource(
                            context.contentResolver, it
                        )
                        ImageDecoder.decodeBitmap(source)
                    }
                    viewModel.addNewImagePost(
                        item = ContentRequestDC(
                            idPageProfile = viewModel.selectedPage.id, item = NewContentDC(
                                type = "image",
                                value = encodeToBase64(image_test),
                                description = viewModel.descriptionMenuNewContent
                            )
                        )
                    )
                    viewModel.descriptionMenuNewContent = ""
                }
            }, modifier = Modifier.padding(top = 22.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}