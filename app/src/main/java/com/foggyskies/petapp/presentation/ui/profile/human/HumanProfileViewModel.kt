package com.foggyskies.petapp.presentation.ui.profile.human

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwipableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwipableMenu

enum class StateProfile {
    HUMAN, PET
}

class HumanProfileViewModel : ViewModel() {

    val swipableMenu = SwipableMenu()

    val circularSelector = CircularSelector()

    var density by mutableStateOf(0f)

    var isMyContactClicked by mutableStateOf(false)

    var isStatusClicked by mutableStateOf(false)

    var nowSelectedStatus by mutableStateOf("Сплю")

    var stateProfile by mutableStateOf(StateProfile.HUMAN)
    var imageProfile by mutableStateOf("http://194.67.93.244/media/petap/test_avatar.jpg")
    var nameProfile by mutableStateOf(USERNAME)

    val a by derivedStateOf {
        if (stateProfile == StateProfile.HUMAN) {
            imageProfile = "http://194.67.93.244/media/petap/test_avatar.jpg"
            nameProfile = USERNAME
            isVisibleInfoUser = true
            swipableMenu.listIcon = listOf(
                ItemSwipableMenu(Image = R.drawable.ic_menu_vack, onValueSelected = {
//                    backHolder?.onBackPressed()
                }),
                ItemSwipableMenu(Image = R.drawable.ic_menu_ads),

                ItemSwipableMenu(Image = R.drawable.ic_menu_home_1, animationImages = listOf(
                    R.drawable.ic_menu_home_1,
                    R.drawable.ic_menu_home_2,
                    R.drawable.ic_menu_home_3,
                ), isAnimatable = true, onValueSelected = {
//                    nav_controller.navigate("Home")
                }),
                ItemSwipableMenu(Image = R.drawable.ic_menu_logout),
                )
            swipableMenu.itemsOffset = listOf(
                Offset(x = 10f, y = -70f),
                Offset(x = -50f, y = -45f),
                Offset(x = -70f, y = 10f),
                Offset(x = 70f, y = -10f),
                )
        } else {

            swipableMenu.listIcon = listOf(
                ItemSwipableMenu(Image = R.drawable.ic_menu_vack),
                ItemSwipableMenu(Image = R.drawable.ic_menu_ads),

                ItemSwipableMenu(Image = R.drawable.ic_menu_home_1, animationImages = listOf(
                    R.drawable.ic_menu_home_1,
                    R.drawable.ic_menu_home_2,
                    R.drawable.ic_menu_home_3,
                ), isAnimatable = true, onValueSelected = {
//                    nav_controller.navigate("Home")
                }),
                ItemSwipableMenu(Image = R.drawable.ic_photo_svgrepo_com),
                ItemSwipableMenu(Image = R.drawable.ic_video),
            )
            swipableMenu.itemsOffset = listOf(
                Offset(x = 10f, y = -70f),
                Offset(x = -50f, y = -45f),
                Offset(x = -70f, y = 10f),
                Offset(x = 50f, y = 45f),
                Offset(x = 70f, y = -10f),
            )
        }
    }

    var isVisibleInfoUser by mutableStateOf(true)
}