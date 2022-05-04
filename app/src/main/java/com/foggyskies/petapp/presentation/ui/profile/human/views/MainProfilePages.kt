package com.foggyskies.petapp.presentation.ui.profile.human.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.profile.human.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.profile.human.StateProfile
import com.foggyskies.petapp.presentation.ui.profile.human.UserMode

//@Composable
fun LazyListScope.MainProfilePages(viewModel: ProfileViewModel, msViewModel: MainSocketViewModel) {

    item {

        AnimatedVisibility(visible = viewModel.stateProfile == StateProfile.HUMAN) {
            Column() {
                if (viewModel.listPagesProfile.isEmpty() && viewModel.userMode == UserMode.OWNER)
                    Box(
                        modifier = Modifier
                            .height(300.dp)
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.isAddingNewCard = !viewModel.isAddingNewCard
                            },
                            modifier = Modifier
                                .padding(5.dp)
                                .align(Alignment.Center)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                            )
                        }
                    }
                LazyRow {
                    itemsIndexed(viewModel.listPagesProfile) { index, item ->
//                        Row {
                            PetsWidget(
                                onClickPetCard = { name, image ->
                                    viewModel.selectedPage = item
                                    viewModel.changeStateProfile(StateProfile.PET)
//                                viewModel.imageProfile = image
//                                viewModel.nameProfile = name
//                                viewModel.a
                                },
                                index,
                                item,
                                viewModel
                            )
                            Spacer(modifier = Modifier.height(30.dp))
//                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                if (viewModel.userMode == UserMode.OWNER)
                    DashBoardComposition(
                        viewModel,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    )
            }
        }
    }
}