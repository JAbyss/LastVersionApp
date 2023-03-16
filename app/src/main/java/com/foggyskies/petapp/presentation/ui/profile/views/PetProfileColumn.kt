package com.foggyskies.petapp.presentation.ui.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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