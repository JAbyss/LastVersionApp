package com.foggyskies.petapp.temppackage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Insets
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.foggyskies.petapp.MainActivity.Companion.loaderForGallery
import com.foggyskies.petapp.R
import com.foggyskies.petapp.routs.Routes
import java.io.File

class GalleryHandler {

    var listPath by mutableStateOf(emptyList<String>())

    fun getCameraImages(context: Context) {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null

        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = context.contentResolver.query(
            uri, projection, null,
            null, null
        );

        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }
        val downloads = File(Routes.FILE.ANDROID_DIR + Routes.FILE.DOWNLOAD_DIR).list()?.toList()
            ?.map { Routes.FILE.ANDROID_DIR + Routes.FILE.DOWNLOAD_DIR + "/" + it } ?: emptyList()
        listOfAllImages.addAll(0, downloads)
        listPath = listOfAllImages.reversed()
//        return listOfAllImages
    }

    val imageBitmap =
        mutableStateOf<Bitmap?>(null)

    var launcher: ManagedActivityResultLauncher<String, Uri?>? = null

    @Composable
    fun InitGallery() {

        val context = LocalContext.current as Activity

        val image_url = remember {
            mutableStateOf<Uri?>(null)
        }

        launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            image_url.value = uri
        }

        image_url.value.let {

            if (Build.VERSION.SDK_INT < 28) {
                imageBitmap.value =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = it?.let { it1 ->
                    ImageDecoder.createSource(
                        context.contentResolver,
                        it1
                    )
                }
                imageBitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
            }
//            }
        }
    }

    fun openGallery() {
        launcher?.launch("image/*")
    }

    @OptIn(
        ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
        ExperimentalFoundationApi::class
    )

    var selectedItems by mutableStateOf(emptyList<String>())


    @SuppressLint("UnrememberedMutableState")
    @OptIn(
        ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
        ExperimentalFoundationApi::class
    )
    @Composable
    fun GalleryImageSelector(
        listItems: List<String> = emptyList(),
        stateSheet: ModalBottomSheetState,
        onSelectedImage: (String) -> Unit = {},
        isManySelect: Boolean = false,
        chatMode: Boolean = false,
        bottomBar: @Composable () -> Unit = {},
        bottomSheetState: ModalBottomSheetState,
    ) {
        DisposableEffect(key1 = stateSheet.isVisible) {
            onDispose {
                Log.e("DISPOSE EFFECT", "TESTING DISPOSE")
                if (!stateSheet.isVisible)
                    listPath = emptyList()
            }
        }
//        var selectedItems by remember {
//            mutableStateOf(emptyList<String>())
//        }

        SideEffect {
            Log.e("GALLERY HANDLER", "Утечка")
        }
//
        val selectMode: (String) -> Unit = {
            selectedItems = if (selectedItems.contains(it))
                selectedItems - it
            else
                selectedItems + it
            onSelectedImage(it)
        }
        val oneSelectMode: (String) -> Unit = {
            onSelectedImage(it)
        }
        val mode = if (isManySelect)
            selectMode
        else
            oneSelectMode

        val context = LocalContext.current

        val values = listItems.windowed(3, 3, true)

        val display_metrics = LocalContext.current.resources.displayMetrics

        val height: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics =
                    (context as Activity).windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                windowMetrics.bounds.height() - insets.bottom - insets.top
            } else {
                val resourceId =
                    context.resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    display_metrics.heightPixels - context.resources.getDimensionPixelSize(
                        resourceId
                    )
                } else
                    0
            }

        val height_config = (height / display_metrics.density).toInt()
//        val  density = LocalDensity.current.density

        val state = rememberLazyListState()

        val scope = rememberCoroutineScope()

        LazyColumn(
            state = state,
//            flingBehavior = ScrollableDefaults.flingBehavior(),
            modifier =
//            if (!chatMode)
            Modifier
//                .apply {
//                    wrapContentSize()
////                    if (chatMode)
//                        padding(bottom = 60.dp)
//                }
//                    .wrapContentSize()
                .fillMaxWidth()
                .height(height_config.dp)
//                    .wrapContentHeight()
//                    .height(400.dp)
//                    .fillMaxHeight()
//            else
//                Modifier
//                    .fillMaxWidth()
//                    .height(400.dp)
////                    .fillMaxHeight()
//                    .padding(bottom = 60.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            stickyHeader {
                Column() {
                    var listDirs by remember {
                        mutableStateOf(emptyList<String>())
                    }
                    var visibleFileTree by remember {
                        mutableStateOf(false)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {

                        Text(
                            text = "Галерея",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .padding(vertical = 7.dp)
//                                .clickable {
//                                    if (listDirs.isEmpty()) {
//                                        listDirs = File("/storage/emulated/0")
//                                            .list()
//                                            ?.toList()
//                                            ?: emptyList()
//                                    }
//                                    visibleFileTree = !visibleFileTree
//                                }
                        )
                    }
                    AnimatedVisibility(
                        visible = visibleFileTree,
                        modifier = Modifier
                            .padding(start = 30.dp)
                    ) {

                        LazyColumn(
                            modifier = Modifier
                                .requiredHeightIn(max = 300.dp)
                                .background(Color.White)
                        ) {
                            items(listDirs) { item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                ) {
//                                    Divider(
//                                        thickness = 1.5.dp,
//                                        color = Color.LightGray,
//                                        modifier = Modifier
//                                            .padding(vertical = 5.dp)
//                                            .fillMaxWidth(0.7f)
//                                            .align(CenterHorizontally)
//                                    )
                                    Text(
                                        text = item,
                                        fontSize = 20.sp,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .padding(horizontal = 7.dp)
                                            .align(CenterHorizontally)
                                    )
                                    Divider(
                                        thickness = 1.dp,
                                        color = Color.LightGray,
                                        modifier = Modifier
                                            .padding(vertical = 5.dp)
                                            .fillMaxWidth(0.8f)
                                            .align(CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }
                }
            }
//            item {
//                Text(text = bottomSheetState.progress.toString())
//            }
//            if (bottomSheetState.progress.)
            itemsIndexed(values) { index, list ->
                CustomPhotos(list, selectedItems, mode, isManySelect)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CustomPhotos(
    list: List<String>,
    selectedItems: List<String>,
    mode: (String) -> Unit,
    isManySelect: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {

        val context = LocalContext.current

        Box(
            modifier = Modifier
                .padding(end = 5.dp)
                .scale(if (selectedItems.contains(list[0])) 0.85f else 1f)
                .weight(1f)
                .height(150.dp)
                .background(Color.LightGray)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(File(list[0]))
                    .crossfade(true)
                    .size(Size(200, 200))
//                    .size(ViewSizeResolver(LocalView.current))
                    .build(),
                contentDescription = null,
                imageLoader = loaderForGallery,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = {
                        mode(list[0])
                    })
            )
            if (isManySelect)
                Image(
                    painter = painterResource(id = if (selectedItems.contains(list[0])) R.drawable.ic_check else R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 7.dp, end = 7.dp)
                        .align(Alignment.TopEnd)
                        .size(24.dp),
                )
        }
        if (list.size > 1)
            Box(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .scale(if (selectedItems.contains(list[1])) 0.85f else 1f)
                    .weight(1f)
                    .height(150.dp)
                    .background(Color.LightGray)
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(list[1]))
                        .crossfade(true)
                        .size(Size(200, 200))
                        .build(),
                    contentDescription = null,
                    imageLoader = loaderForGallery,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = {
                            mode(list[1])
                        })
                )
                if (isManySelect)

                    Image(
                        painter = painterResource(id = if (selectedItems.contains(list[1])) R.drawable.ic_check else R.drawable.ic_add),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 7.dp, end = 7.dp)
                            .align(Alignment.TopEnd)
                            .size(24.dp),
                    )
            }
        if (list.size > 2)
            Box(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .scale(if (selectedItems.contains(list[2])) 0.85f else 1f)
                    .weight(1f)
                    .height(150.dp)
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(list[2]))
                        .crossfade(true)
                        .size(Size(200, 200))
//                        .size(ViewSizeResolver(LocalView.current))
//                        .size(Size(150, 150))
                        .build(),
                    contentDescription = null,
                    imageLoader = loaderForGallery,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = {
                            mode(list[2])
                        })
                )
                if (isManySelect)

                    Image(
                        painter = painterResource(id = if (selectedItems.contains(list[2])) R.drawable.ic_check else R.drawable.ic_add),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 7.dp, end = 7.dp)
                            .align(Alignment.TopEnd)
                            .size(24.dp),
                    )
            }
    }
}