package com.foggyskies.petapp.presentation.ui.profile.human.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.human.PageProfileFormattedDC
import com.foggyskies.petapp.presentation.ui.profile.human.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.human.UserMode

@Composable
fun PetsWidget(
    onClickPetCard: (String, String) -> Unit,
    index: Int,
    item: PageProfileFormattedDC,
    viewModel: ProfileViewModel
) {

    if (index == 0 && viewModel.userMode == UserMode.OWNER) {
        Box(
            modifier = Modifier
                .height(300.dp)
        ) {

            IconButton(
                onClick = { viewModel.isAddingNewCard = !viewModel.isAddingNewCard },
                modifier = Modifier
                    .padding(5.dp)
                    .align(Center)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(15.dp))
    }
    PetCard(
        item = item,
        onClickPetCard,
        viewModel
    )
    Spacer(modifier = Modifier.width(20.dp))
}