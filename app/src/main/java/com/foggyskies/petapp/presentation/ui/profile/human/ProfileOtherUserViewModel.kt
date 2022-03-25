package com.foggyskies.petapp.presentation.ui.profile.human

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.SwipableMenu

class ProfileOtherUserViewModel: ViewModel() {

    val swipableMenu = SwipableMenu()

    val circularSelector = CircularSelector()

    var isMyContactClicked by mutableStateOf(false)

    var isStatusClicked by mutableStateOf(false)

    var nowSelectedStatus by mutableStateOf("Сплю")

    var stateProfile by mutableStateOf(StateProfile.HUMAN)
    var imageProfile by mutableStateOf("http://194.67.93.244/media/petap/test_avatar.jpg")
    var nameProfile by mutableStateOf("JAbyss")

    val a by derivedStateOf {
        if (stateProfile == StateProfile.HUMAN){
            imageProfile = "http://194.67.93.244/media/petap/test_avatar.jpg"
            nameProfile = "JAbyss"
            isVisibleInfoUser = true
        }
    }

    var isVisibleInfoUser by mutableStateOf(true)

}