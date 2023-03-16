package com.foggyskies.petapp.presentation.ui.profile

import android.graphics.*
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.foggyskies.petapp.R
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.globalviews.FullInvisibleBack
import com.foggyskies.petapp.presentation.ui.home.HomeMVIModel
import com.foggyskies.petapp.presentation.ui.profile.requests.*
import com.foggyskies.petapp.presentation.ui.profile.views.*
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.workers.*
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.io.ByteArrayOutputStream
import java.io.File


@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun ProfileScreen(
    nav_controller: NavHostController,
    viewModel: ProfileViewModel,
    uploadingViewModel: UploadFileViewModel
) {
    LaunchedEffect(key1 = Unit) {
        if (viewModel.userMode == UserMode.OWNER) {
            viewModel.myAvatar()
            viewModel.myPagesProfile()
        }
    }

    val context = LocalContext.current

    val state = rememberLazyListState()

    BackHandler {
        if (viewModel.isVisiblePostWindow) {
            viewModel.isVisiblePostWindow = false
        } else if (viewModel.stateProfile == StateProfile.PET) {
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
                        viewModel.backgroundScope.launch {
                            val bodyFile = BodyFile.generate(
                                it,
                                if (viewModel.stateProfile == StateProfile.HUMAN) TypeLoadFile.AVATAR else TypeLoadFile.PROFILE,
                                MainPreference.IdUser
                            )

                            val readyPath = uploadingViewModel.uploadFile(
                                File(it),
                                bodyFile,
                                isCompressed = true
                            )
                            readyPath.onSuccess { path ->
                                if (viewModel.stateProfile == StateProfile.HUMAN) {

                                    viewModel.changeAvatar(path)
                                } else
                                    viewModel.changeAvatarProfile(
                                        path,
                                        viewModel.selectedPage.id
                                    )
                            }
//                            if (viewModel.stateProfile == StateProfile.HUMAN) {
//                                //fixme надо переделать
//                                viewModel.changeAvatar(readyPath!!)
//                            } else
//                                viewModel.changeAvatarProfile(
//                                    readyPath!!,
//                                    viewModel.selectedPage.id
//                                )
                        }
                    },
                    bottomSheetState = stateSheet
                )
            }
        },
        sheetState = stateSheet,
    ) {
        val densityScale = LocalDensity.current.density
        val height = (densityScale * 150).toInt()
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    viewModel.swipableMenu.touchMenuListener(this) {}
                }
                .fillMaxSize()

        ) {

            LazyColumn(
                state = state,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                HeadProfile(viewModel = viewModel, state = state, context, stateSheet)

                MainProfilePages(viewModel, uploadingViewModel)

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

                        if (item.isNotEmpty())
                            AsyncImage(model = ImageRequest.Builder(context)
                                .data("${Routes.SERVER.REQUESTS.BASE_URL}/${item[0].address}")
                                .crossfade(true)
                                .size(coil.size.Size(600, height))
                                .build()
//                            "${Routes.SERVER.REQUESTS.BASE_URL}/${item[0].address}"
                                ,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(2.5.dp)
                                    .weight(1f)
                                    .toggleable(
                                        value = true,
                                        enabled = !viewModel.swipableMenu.isMenuOpen
                                    ) {
                                        viewModel.infoAboutOnePost(item[0].id)
                                    })
                        if (item.size > 1) AsyncImage(model = ImageRequest.Builder(context)
                            .data("${Routes.SERVER.REQUESTS.BASE_URL}/${item[1].address}")
                            .crossfade(true)
                            .size(coil.size.Size(600, height))
//                    .size(ViewSizeResolver(LocalView.current))
                            .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(2.5.dp)
                                .weight(1f)
                                .toggleable(
                                    value = true,
                                    enabled = !viewModel.swipableMenu.isMenuOpen
                                ) {
                                    viewModel.infoAboutOnePost(item[1].id)
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

                            AsyncImage(model = ImageRequest.Builder(context)
                                .data("${Routes.SERVER.REQUESTS.BASE_URL}/${item[2].address}")
                                .crossfade(true)
                                .size(coil.size.Size(600, height))
                                .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
//                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(2.5.dp)
                                    .weight(1f)
                                    .toggleable(
                                        value = true,
                                        enabled = !viewModel.swipableMenu.isMenuOpen
                                    ) {
                                        viewModel.infoAboutOnePost(item[2].id)
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
                            .align(Center),
                        uploadFileViewModel = uploadingViewModel
                    )
                }
            }
            AnimatedVisibility(
                visible = viewModel.menuHelper.getMenuVisibleValue(MENUS.NEWCONTENT).value,
                modifier = Modifier.align(Center)
            ) {
                FullInvisibleBack(onBackClick = { viewModel.menuHelper.changeVisibilityMenu(MENUS.NEWCONTENT) }) {
                    AddNewImage(viewModel, uploadingViewModel)
                }
            }
            //FIXME ХУЙ ЗНАЕТ ЧТО ТУТ
            AnimatedVisibility(
                visible = viewModel.isVisiblePostWindow,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Center)
            ) {
                DisposableEffect(key1 = Unit) {
                    onDispose {
                        viewModel.swipableMenu.isReadyMenu = true
                    }
                }
                viewModel.postScreenHandler.PostScreen(
                    onLongPress = {

                        viewModel.swipableMenu.isReadyMenu = false
                        viewModel.isVisiblePostWindow = false
                        viewModel.photoScreenClosed()

                    },
                    isScrollable = mutableStateOf(true),
                    modifier = Modifier
//                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxSize()
                        .background(Color.White)
                )
            }

            if (viewModel.swipableMenu.isTappedScreen)
                viewModel.swipableMenu.CircularTouchMenu()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.AddNewImage(
    viewModel: ProfileViewModel,
    uploadingViewModel: UploadFileViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current.density

    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

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
            .fillMaxWidth(0.7f)
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
                        val heightForScale = (screenHeight.value * density * 0.7f).toInt()
                        val scale = if (bit.height > heightForScale)
                            bit.height / heightForScale
                        else
                            1
                        val widthPx = bit.width / scale
                        val heightPx = bit.height / scale
                        val scaledBitmap = Bitmap.createScaledBitmap(bit, widthPx, heightPx, true)
                        Image(bitmap = scaledBitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f)
                                .clickable { launcher.launch("image/*") }
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
                    val path = PathUtil.getPath(context, it)!!
                    viewModel.newPost(
                        path,
                        idPage = viewModel.selectedPage.id,
                        description = viewModel.descriptionMenuNewContent,
                        uploadViewModel = uploadingViewModel
                    )
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

// untested function
fun getNV21(inputWidth: Int, inputHeight: Int, scaled: Bitmap): ByteArray? {
    val argb = IntArray(inputWidth * inputHeight)
    scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)
    val yuv = ByteArray(inputWidth * inputHeight * 3 / 2)
    encodeYUV420SP(yuv, argb, inputWidth, inputHeight)
    scaled.recycle()
    return yuv
}

fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
    val frameSize = width * height
    var yIndex = 0
    var uvIndex = frameSize
    var a: Int
    var R: Int
    var G: Int
    var B: Int
    var Y: Int
    var U: Int
    var V: Int
    var index = 0
    for (j in 0 until height) {
        for (i in 0 until width) {
            a = argb[index] and -0x1000000 shr 24 // a is not used obviously
            R = argb[index] and 0xff0000 shr 16
            G = argb[index] and 0xff00 shr 8
            B = argb[index] and 0xff shr 0

            // well known RGB to YUV algorithm
            Y = (66 * R + 129 * G + 25 * B + 128 shr 8) + 16
            U = (-38 * R - 74 * G + 112 * B + 128 shr 8) + 128
            V = (112 * R - 94 * G - 18 * B + 128 shr 8) + 128

            // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
            //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
            //    pixel AND every other scanline.
            yuv420sp[yIndex++] = (if (Y < 0) 0 else if (Y > 255) 255 else Y).toByte()
            if (j % 2 == 0 && index % 2 == 0) {
                yuv420sp[uvIndex++] = (if (V < 0) 0 else if (V > 255) 255 else V).toByte()
                yuv420sp[uvIndex++] = (if (U < 0) 0 else if (U > 255) 255 else U).toByte()
            }
            index++
        }
    }
}