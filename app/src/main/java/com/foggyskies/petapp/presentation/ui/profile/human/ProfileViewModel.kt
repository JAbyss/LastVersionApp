package com.foggyskies.petapp.presentation.ui.profile.human

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64.encodeToString
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.presentation.ui.profile.entity.PetCardEntity
import com.foggyskies.petapp.presentation.ui.registation.RegistrationUserDC
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import kotlin.io.use

enum class StateProfile {
    HUMAN, PET
}

@kotlinx.serialization.Serializable
data class PageProfileDC(
    var id: String,
    var title: String,
    var description: String,
    var image: String
){

    fun withCountSubsAndContents(countSubscribers: String, countContents: String): PageProfileFormattedDC {
        return PageProfileFormattedDC(
            id = id,
            title = title,
            description = description,
            image = image,
            countSubscribers = countSubscribers,
            countContents = countContents
        )
    }
}
@Serializable
data class PageProfileFormattedDC(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var image: String = "",
    var countSubscribers: String = "",
    var countContents: String = ""
)

@Serializable
data class ContentPreviewDC(
    val id: String,
    val address: String
)

class ProfileViewModel : ViewModel() {

    val swipableMenu = SwappableMenu()

    val circularSelector = CircularSelector()

    var density by mutableStateOf(0f)

    var isMyContactClicked by mutableStateOf(false)

    var isStatusClicked by mutableStateOf(false)

    var nowSelectedStatus by mutableStateOf("Сплю")

    var stateProfile by mutableStateOf(StateProfile.HUMAN)

    var selectedPage by mutableStateOf(PageProfileFormattedDC())

    var listPostImages by mutableStateOf(emptyList<ContentPreviewDC>())

    fun getContentPage(idPageProfile: String){
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listPostImages = it.get<List<ContentPreviewDC>>("http://${MAINENDPOINT}/content/getContentPreview") {
                    headers["Auth"] = TOKEN
                    parameter("idPageProfile", idPageProfile)
                }
            }
        }
    }

    var imageProfile by mutableStateOf("test_avatar.jpg")
    var nameProfile by mutableStateOf(USERNAME)

    val a by derivedStateOf {
        if (stateProfile == StateProfile.HUMAN) {
            imageProfile = "test_avatar.jpg"
            nameProfile = USERNAME
            isVisibleInfoUser = true
            swipableMenu.listIcon = listOf(
                ItemSwappableMenu(Image = R.drawable.ic_menu_vack, onValueSelected = {
//                    backHolder?.onBackPressed()
                }),
                ItemSwappableMenu(Image = R.drawable.ic_menu_ads),

                ItemSwappableMenu(Image = R.drawable.ic_menu_home_1, animationImages = listOf(
                    R.drawable.ic_menu_home_1,
                    R.drawable.ic_menu_home_2,
                    R.drawable.ic_menu_home_3,
                ), isAnimate = true, onValueSelected = {
//                    nav_controller.navigate("Home")
                }),
                ItemSwappableMenu(Image = R.drawable.ic_menu_logout),
                )
            swipableMenu.itemsOffset = listOf(
                Offset(x = 10f, y = -70f),
                Offset(x = -50f, y = -45f),
                Offset(x = -70f, y = 10f),
                Offset(x = 70f, y = -10f),
                )
        } else {
            getContentPage(selectedPage.id)
            swipableMenu.listIcon = listOf(
                ItemSwappableMenu(Image = R.drawable.ic_menu_vack),
                ItemSwappableMenu(Image = R.drawable.ic_menu_ads),

                ItemSwappableMenu(Image = R.drawable.ic_menu_home_1, animationImages = listOf(
                    R.drawable.ic_menu_home_1,
                    R.drawable.ic_menu_home_2,
                    R.drawable.ic_menu_home_3,
                ), isAnimate = true, onValueSelected = {
//                    nav_controller.navigate("Home")
                }),
                ItemSwappableMenu(Image = R.drawable.ic_photo_svgrepo_com),
                ItemSwappableMenu(Image = R.drawable.ic_video),
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

    var isAddingNewCard by mutableStateOf(false)

    var pagesProfileList = mutableStateListOf<PetCardEntity>(
//        PetCardEntity(
//            image = "http://194.67.93.244/media/petap/image_dog_for_preview.jpg",
//            name = "Элька",
//            breed = "Дворняжка"
//        )
    )

    fun getPagesProfile(){
//        viewModelScope.launch {
//            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                expectSuccess = false
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 3000
//                }
//            }.use {
////                            val responseRegistration =
//                val response =
//                    it.get<HttpResponse>("http://${MainActivity.MAINENDPOINT}/createPageProfile") {
//                        headers["Auth"] = TOKEN
//                    }
//                if (response.status.isSuccess()){
//                    isAddingNewCard = false
//                }
//            }
//        }
    }

    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val byte_array = baos.toByteArray()
        val string = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getEncoder(). encodeToString(byte_array)
        } else {
            ""
        }
        string.replace("\\n", "")
        return string
    }

    fun createNewPage(item: PageProfileDC){
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                expectSuccess = false
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
//                            val responseRegistration =
                val response =
                    it.post<HttpResponse>("http://${MainActivity.MAINENDPOINT}/createPageProfile") {
                        headers["Auth"] = TOKEN
                        headers["Content-Type"] = "Application/Json"
                        body = item
                    }
                if (response.status.isSuccess()){
                    isAddingNewCard = false
                }
            }
        }
    }
}