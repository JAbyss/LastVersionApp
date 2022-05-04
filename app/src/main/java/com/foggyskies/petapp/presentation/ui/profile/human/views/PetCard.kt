package com.foggyskies.petapp.presentation.ui.profile.human.views

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.human.PageProfileDC
import com.foggyskies.petapp.presentation.ui.profile.human.PageProfileFormattedDC
import com.foggyskies.petapp.presentation.ui.profile.human.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.human.encodeToBase64
import com.foggyskies.testingscrollcompose.extendfun.forEachKeys

@Composable
fun PetCard(
    item: PageProfileFormattedDC,
    onClickPetCard: (String, String) -> Unit,
    viewModel: ProfileViewModel,
    creatingModifier: Modifier? = null
) {
    val context = LocalContext.current

    viewModel.profileHandler.InitGallery(context = context)

    Box(
        modifier = creatingModifier?.clickable {
            viewModel.profileHandler.openGallery()
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

        when {
            item.image != "" -> AsyncImage(
                model = "http://$MAINENDPOINT/${item.image}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            viewModel.profileHandler.imageBitmap.value != null -> {
                Image(
                    bitmap = viewModel.profileHandler.imageBitmap.value!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }
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
                    val recomposition = currentRecomposeScope
                    BasicTextField(
                        value = item.title,
                        onValueChange = {
                            item.title = it
                            recomposition.invalidate()
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
                    BasicTextField(
                        value = item.description,
                        onValueChange = {
                            item.description = it
                            recomposition.invalidate()
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
            if (creatingModifier != null)
                Box(
                    contentAlignment = Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(CenterVertically)
                ) {

                    IconButton(
                        onClick = {
                            val string64 = viewModel.profileHandler.imageBitmap.value?.let {
                                encodeToBase64(
                                    it
                                )
                            }
                                ?: ""
//                            }
                            viewModel.createNewPage(
                                item = PageProfileDC(
                                    id = "",
                                    title = item.title,
                                    description = item.description,
                                    image = string64
                                )
                            )
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