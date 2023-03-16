package com.foggyskies.petapp.temppackage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.chat.entity.FileDC

//@Preview
//@Composable
//fun One() {
//    OneCloudItem(
//        nameFile = "Новая папка",
//        extension = "ZIP",
//        size = "50 MB | 30 items",
//        selectedItem = remember {
//            mutableStateOf("")
//        }
//    )
//}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OneCloudItem(
    nameFile: String,
    extension: String,
    size: String,
    index: Int,
    selectedItem: MutableState<Int?>,
    viewModel: CloudViewModel,
    path: String,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable {
                if (selectedItem.value != index)
                    selectedItem.value = index
                else {
                    viewModel.connectCloudSocket()
                    viewModel.sendAction(path)
//                    selectedItem.value = null
//                    msV
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Spacer(modifier = Modifier.height(7.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
            ) {
                AnimatedContent(
                    targetState = selectedItem,
                    modifier = Modifier
                        .align(Alignment.Center)
                ) { targetState ->

                    Box() {

                        Icon(
                            painter = painterResource(id = if (selectedItem.value != index) R.drawable.ic_file else R.drawable.ic_download),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(5.dp)
                                .size(60.dp)
//                            .align(Alignment.Center)
                        )
                        if (selectedItem.value != index)
                            Text(
                                text = extension,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                    }
                }

            }

            Spacer(modifier = Modifier.height(5.dp))


            Text(
                text = nameFile,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = size,
                color = Color.Black,
                fontSize = 10.sp
            )
        }
        AnimatedVisibility(
            visible = selectedItem.value == index,
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            ActionsMenu(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
//                    .align(Alignment.CenterEnd)
            )
        }

    }
}

@Composable
fun ActionsMenu(modifier: Modifier) {

    Column(
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null
        )
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null
        )
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null
        )
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null
        )
    }

}

//@Preview
//@Composable
//fun CloudScreenP() {
//
//    CloudScreen(msViewModel.listFiles)
//
//}

@Composable
fun CloudScreen(
    listFiles: List<FileDC>,
    viewModel: CloudViewModel
) {
    val selectedItem = remember {
        mutableStateOf<Int?>(null)
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.background(Color.White),
        reverseLayout = true
    ) {
//        val listFiles = listOf(
//            "Новая папка",
//            "Новая папка",
//            "Новая папка",
//            "Новая папка",
//            "Новая папка",
//            "Новая папка",
//            "Старая папка",
//            )
        itemsIndexed(listFiles) { index, item ->
            OneCloudItem(
                nameFile = item.name,
                extension = item.type,
                size = "${item.size} | 30 items",
                index,
                selectedItem,
                viewModel,
                path = item.path
                )
        }
    }
}