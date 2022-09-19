package com.foggyskies.petapp.presentation.ui.profile.views

import android.R.attr.data
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.foggyskies.petapp.R
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.profile.*
import com.foggyskies.petapp.presentation.ui.profile.requests.changeAvatar
import com.foggyskies.petapp.presentation.ui.profile.requests.changeAvatarProfile
import com.foggyskies.petapp.presentation.ui.profile.requests.newPageProfile
import com.foggyskies.petapp.routs.Routes
import com.foggyskies.petapp.workers.PathUtil
import com.foggyskies.petapp.workers.TypeLoadFile
import com.foggyskies.petapp.workers.uploadFile
import java.io.File


@Composable
fun PetCard(
    item: PageProfileFormattedDC,
    onClickPetCard: (String, String) -> Unit,
    viewModel: ProfileViewModel,
    creatingModifier: Modifier? = null
) {
    val context = LocalContext.current.applicationContext
    val density = LocalDensity.current.density
    val image_url = remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        image_url.value = uri
    }
    Box(
        modifier = creatingModifier?.clickable {
            launcher.launch("image/*")
        }
            ?: Modifier
                .clip(RoundedCornerShape(20.dp))
                .height(300.dp)
                .width(250.dp)
                .toggleable(
                    value = true,
                    enabled = !viewModel.swipableMenu.isMenuOpen,
                    onValueChange = {
                        onClickPetCard(item.title, item.image)
                    })

    ) {

        if (image_url.value != null)
            image_url.let {
                val image_test =
                    mutableStateOf<Bitmap?>(null)

                if (Build.VERSION.SDK_INT < 28) {
                    image_test.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                } else {
                    val source =
                        ImageDecoder.createSource(context.contentResolver, it.value!!)
                    image_test.value = ImageDecoder.decodeBitmap(source)
                }
                image_test.value?.let { bit ->
                    val heightForScale = (300 * density).toInt()
                    val scale = if (bit.height > heightForScale)
                        bit.height / heightForScale
                    else
                        1
                    val widthPx =
                        bit.width / scale
                    val heightPx = bit.height / scale
                    val scaledBitmap = Bitmap.createScaledBitmap(bit, widthPx, heightPx, true)
                    Image(
                        bitmap = scaledBitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .fillMaxSize()
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                }
            }
        else if (item.image != "")
            AsyncImage(
                model = "${Routes.SERVER.REQUESTS.BASE_URL}/${item.image}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(BottomStart)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 15.dp, bottom = 15.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .fillMaxWidth(0.7f)
            ) {
                if (creatingModifier == null) {
                    Text(
                        text = item.title,
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(start = 15.dp, top = 10.dp)
                    )
                    Text(
                        text = item.description,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 15.dp, bottom = 10.dp)
                    )
                } else {
                    ClosedComposedFun {
                        var text by remember {
                            mutableStateOf(item.title)
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = {
                                text = it
                                item.title = text
//                            recomposition.invalidate()
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                            ),
                            modifier = Modifier
                                .padding(start = 15.dp, top = 10.dp)
                        )
                    }
                    ClosedComposedFun {
                        var text by remember {
                            mutableStateOf(item.description)
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = {
                                text = it
                                item.description = text
                            },
                            maxLines = 3,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                            ),
                            modifier = Modifier
                                .padding(start = 15.dp, bottom = 10.dp)
                        )
                    }
                }
            }
            if (creatingModifier != null)
                Box(
                    contentAlignment = Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(CenterVertically)
                ) {

                    IconButton(
                        onClick = {
                            image_url.let {
                                val path = PathUtil.getPath(context, it.value!!)!!
                                viewModel.newPageProfile(
                                    PageProfileDC(
                                        id = "",
                                        title = item.title,
                                        description = item.description,
                                        image = ""
                                    ),
                                    path
                                )

//                                uploadFile(
//                                    path,
//                                    TypeLoadFile.PROFILE,
//                                    infoData = "new",
//                                    fileUploaded = { pathToFile ->
//
//                                    }
//                                )
//                                var image_test = if (Build.VERSION.SDK_INT < 28) {
//
//                                    MediaStore.Images.Media.getBitmap(
//                                        context.contentResolver,
//                                        it.value
//                                    )
//                                } else {
//                                    val source =
//                                        ImageDecoder.createSource(
//                                            context.contentResolver,
//                                            it.value!!
//                                        )
//                                    ImageDecoder.decodeBitmap(source)
//                                }
//                                val string64 = image_test?.let {
//                                    encodeToBase64(
//                                        it
//                                    )
//                                }
//                                    ?: ""

                            }
                        },
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                }
        }
    }
}

@Composable
fun ClosedComposedFun(body: @Composable () -> Unit) {
    body()
}