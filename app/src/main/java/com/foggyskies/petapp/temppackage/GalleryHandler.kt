package com.foggyskies.petapp.temppackage

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.loader
import com.foggyskies.petapp.R
import java.io.File

class GalleryHandler {

    var listPath by mutableStateOf(emptyList<String>())

    fun getCameraImages(context: Context){
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
        listPath = listOfAllImages
//        return listOfAllImages
    }

    val imageBitmap =
        mutableStateOf<Bitmap?>(null)

    var launcher: ManagedActivityResultLauncher<String, Uri?>? = null

    @Composable
    fun InitGallery(context: Context) {
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


    @OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
    @Composable
    fun GalleryImageSelector(
        listItems: List<String> = emptyList(),
        stateSheet: ModalBottomSheetState,
        onSelectedImage: (String) -> Unit = {},
        isManySelect: Boolean = false,
        chatMode: Boolean = false,
        bottomBar: @Composable () -> Unit = {},
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

        LazyColumn(
            modifier = if (!chatMode)
                Modifier
//                .apply {
//                    wrapContentSize()
////                    if (chatMode)
//                        padding(bottom = 60.dp)
//                }
                    .wrapContentSize()
            else
                Modifier
                    .wrapContentSize()
                    .padding(bottom = 60.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
//            val lists =
//            windowed(1, 1, false))
            itemsIndexed(listItems.windowed(3, 3, false)) { index, list ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 2.5.dp)
                        .clip(
                            if (index == 0) RoundedCornerShape(
                                topStart = 15.dp,
                                topEnd = 15.dp
                            ) else RoundedCornerShape(0.dp)
                        )
                        .requiredSizeIn(maxHeight = 100.dp)
                ) {
                    val animatables = listOf(
                        remember { androidx.compose.animation.core.Animatable(1f) },
                        remember { androidx.compose.animation.core.Animatable(1f) },
                        remember { androidx.compose.animation.core.Animatable(1f) }
                    )

                    if (list.isNotEmpty())
                    //TODO() OPTIMIZE THIS!!!
                        Box(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .weight(1f)
                                .scale(animatables[0].value)
                        ) {
                            LaunchedEffect(
                                key1 = selectedItems.contains(list[0])
                            ) {
                                animatables[0].animateTo(
                                    if (selectedItems.contains(list[0]))
                                        0.85f
                                    else
                                        1f
                                )
                            }
                            var request by remember {
                                mutableStateOf<ImageRequest?>(null)
                            }
                            LaunchedEffect(key1 = Unit ){
                                request = ImageRequest.Builder(context)
                                    .data(File(list[0]))
                                    .crossfade(true)
                                    .build()
                                request?.let { loader.enqueue(it) }
                            }

                            AsyncImage(
                                model = request,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(onClick = { mode(list[0]) })
                            )
                            androidx.compose.animation.AnimatedContent(
                                targetState = selectedItems.contains(
                                    list[0]
                                ),
                                modifier = Modifier
                                    .padding(top = 7.dp, end = 7.dp)
                                    .align(Alignment.TopEnd)
                            ) { targerState ->
                                Image(
                                    painter = painterResource(id = if (targerState) R.drawable.ic_check else R.drawable.ic_add),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }
                        }
                    if (list.size > 1)
                        Box(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .weight(1f)
                                .scale(animatables[1].value)
                        ) {
                            LaunchedEffect(
                                key1 = selectedItems.contains(list[1])
                            ) {
                                animatables[1].animateTo(
                                    if (selectedItems.contains(list[1]))
                                        0.9f
                                    else
                                        1f
                                )
                            }
                            Image(
                                rememberAsyncImagePainter(File(list[1])),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(onClick = { mode(list[1]) })
                            )
                            androidx.compose.animation.AnimatedContent(
                                targetState = selectedItems.contains(
                                    list[1]
                                ),
                                modifier = Modifier
                                    .padding(top = 7.dp, end = 7.dp)
                                    .align(Alignment.TopEnd)
                            ) { targerState ->
                                Image(
                                    painter = painterResource(id = if (targerState) R.drawable.ic_check else R.drawable.ic_add),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }
                        }
                    if (list.size > 2)
                        Box(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .weight(1f)
                                .scale(animatables[2].value)
                        ) {
                            LaunchedEffect(
                                key1 = selectedItems.contains(list[2])
                            ) {
                                animatables[2].animateTo(
                                    if (selectedItems.contains(list[2]))
                                        0.85f
                                    else
                                        1f
                                )
                            }
                            Image(
                                rememberAsyncImagePainter(File(list[2])),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(onClick = { mode(list[2]) })
                            )
                            androidx.compose.animation.AnimatedContent(
                                targetState = selectedItems.contains(
                                    list[2]
                                ),
                                modifier = Modifier
                                    .padding(top = 7.dp, end = 7.dp)
                                    .align(Alignment.TopEnd)
                            ) { targerState ->
                                Image(
                                    painter = painterResource(id = if (targerState) R.drawable.ic_check else R.drawable.ic_add),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }
                        }
                }
//                if (index == lists.lastIndex){
//                    Spacer(modifier = Modifier.padding(bottom = 60.dp))
//                }
            }
        }
    }
}