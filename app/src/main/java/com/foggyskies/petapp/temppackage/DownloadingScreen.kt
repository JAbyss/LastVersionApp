package com.foggyskies.petapp.temppackage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foggyskies.petapp.R

@Preview
@Composable
fun Preview() {
    OneDownloadingItem()
}

@Composable
fun OneDownloadingItem() {


        Row() {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
            ) {
                Icon(painter = painterResource(
                    id = R.drawable.ic_file),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(65.dp)
                )
            }
            Spacer(modifier = Modifier.width(7.dp))
            Column() {
                Text(
                    text = "Status",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Row{
                    Spacer(modifier = Modifier
                        .height(7.dp)
                        .fillMaxWidth(0.7f)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                    )
                }
                Row() {
                    Text(
                        text = "54% | 30 files",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
}

@Composable
fun DownloadingScreen() {

    LazyColumn{

    }

}