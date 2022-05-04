package com.foggyskies.petapp.presentation.ui.profile.human

import androidx.annotation.IntRange
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

class ProfileOtherUserViewModel: ViewModel() {

    val swipableMenu = SwappableMenu()

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
    val listIconStateHuman = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_vack,
            offset = Offset(x = 10f, y = -70f),
            onValueSelected = {
                it.navigate(it.backQueue[1].destination.route!!)
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
            offset = Offset(x = -50f, y = -45f),
            onValueSelected = {
                it.navigate("AdsHomeless")
            }
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_home_1,
            animationImages = listOf(
                R.drawable.ic_menu_home_1,
                R.drawable.ic_menu_home_2,
                R.drawable.ic_menu_home_3,
            ),
            isAnimate = true,
            offset = Offset(x = -70f, y = 10f),
            onValueSelected = {
                it.navigate("Home")
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_logout,
            offset = Offset(x = 70f, y = -10f)
        )
    )
    var isVisibleInfoUser by mutableStateOf(true)
    var humanPhoto by mutableStateOf("")

    var listPostImages by mutableStateOf(emptyList<PageProfileFormattedDC>())

    fun getAvatar(){
        viewModelScope.launch {
            HttpClient(Android) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {

                val response =
                    it.get<HttpResponse>("http://${MainActivity.MAINENDPOINT}/avatar") {
                        headers["Auth"] = MainActivity.TOKEN
                    }
                if (response.status.isSuccess()) {
                    humanPhoto = response.readText()
                    imageProfile = humanPhoto
                }
            }
        }
    }

    private fun getContentPage(idPageProfile: String) {
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listPostImages =
                    it.get("http://${MainActivity.MAINENDPOINT}/content/getContentPreview") {
                        headers["Auth"] = MainActivity.TOKEN
                        parameter("idPageProfile", idPageProfile)
                    }
            }
        }
    }

    var selectedPage by mutableStateOf(PageProfileFormattedDC())

    val menuHelper = MenuVisibilityHelper(action = { swipableMenu.isReadyMenu = it })


    val listIconStatePage = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_vack,
            offset = Offset(x = 10f, y = -70f),
            onValueSelected = {
                changeStateProfile(StateProfile.HUMAN)
            }
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
            offset = Offset(x = -50f, y = -45f),
            onValueSelected = {
//                it.navigate("AdsHomeless")
            }
        ),
        ItemSwappableMenu(Image = R.drawable.ic_menu_home_1,
            animationImages = listOf(
                R.drawable.ic_menu_home_1,
                R.drawable.ic_menu_home_2,
                R.drawable.ic_menu_home_3,
            ),
            offset = Offset(x = -70f, y = 10f),
            isAnimate = true,
            onValueSelected = {
//                it.navigate("Home")
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_photo_svgrepo_com,
            offset = Offset(x = 50f, y = 45f)
        ) {
            menuHelper.changeVisibilityMenu(
                MENUS.NEWCONTENT
            )
//                isNewContentWindowVisibility = true
//                val image_url = remember {
//                    mutableStateOf<Uri?>(null)
//                }
//
//                val launcher = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.GetContent()
//                ) { uri: Uri? ->
//                    image_url.value = uri
//                }

//                launcher.launch("image/*")
        },
        ItemSwappableMenu(
            Image = R.drawable.ic_video,
            offset = Offset(x = 70f, y = -10f),
            onValueSelected = {

            }
        ),
    )

    var staticNameProfile by mutableStateOf("")

    fun changeStateProfile(state: StateProfile) {
        stateProfile = state
        when (stateProfile) {
            StateProfile.HUMAN -> {
                imageProfile = humanPhoto
                nameProfile = staticNameProfile
                isVisibleInfoUser = true
                swipableMenu.listIcon = listIconStateHuman
            }
            StateProfile.PET -> {
                getContentPage(selectedPage.id)
                imageProfile = selectedPage.image
                nameProfile = selectedPage.title
                swipableMenu.listIcon = listIconStatePage
            }
        }
    }

    var idUser by mutableStateOf("")

    fun getPagesProfileByIdUser(){
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listPostImages =
                    it.get("http://${MainActivity.MAINENDPOINT}/getPagesProfileByIdUser") {
                        headers["Auth"] = MainActivity.TOKEN
                        parameter("idUser", idUser)
                    }
            }
        }
    }
}