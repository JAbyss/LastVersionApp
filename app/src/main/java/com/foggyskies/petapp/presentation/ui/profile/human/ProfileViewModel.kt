package com.foggyskies.petapp.presentation.ui.profile.human

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foggyskies.petapp.temppackage.GalleryHandler
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.MAINENDPOINT
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.home.ContentUsersDC
import com.foggyskies.petapp.presentation.ui.home.PostScreenHandler
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream

enum class StateProfile {
    HUMAN, PET
}

enum class MENUS {
    NEWCONTENT, POST, RIGHT, SEARCHUSERS, CHATS, FRIENDS
}

@kotlinx.serialization.Serializable
data class PageProfileDC(
    var id: String,
    var title: String,
    var description: String,
    var image: String
) {

    fun withCountSubsAndContents(
        countSubscribers: String,
        countContents: String
    ): PageProfileFormattedDC {
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

enum class UserMode {
    OWNER, OTHER
}

class ProfileViewModel : ViewModel() {
    val startString = "Описание публикации..."

    var userMode by mutableStateOf(UserMode.OWNER)

    val menuHelper = MenuVisibilityHelper(action = { swipableMenu.isReadyMenu = it })

    var descriptionMenuNewContent by mutableStateOf(startString)

    var focusState by mutableStateOf(false)

    val profileHandler = GalleryHandler()

    val descriptionMenuNewContentHandler by derivedStateOf {

        if (focusState) {
            descriptionMenuNewContent = if (descriptionMenuNewContent == startString)
                ""
            else
                descriptionMenuNewContent
        } else {
            if (descriptionMenuNewContent == "")
                descriptionMenuNewContent = startString
        }
        descriptionMenuNewContent
    }

    var density by mutableStateOf(0f)

    var isMyContactClicked by mutableStateOf(false)

    var isStatusClicked by mutableStateOf(false)

    var nowSelectedStatus by mutableStateOf("Сплю")

    var stateProfile by mutableStateOf(StateProfile.HUMAN)

    var selectedPage by mutableStateOf(PageProfileFormattedDC())

    var listPostImages by mutableStateOf(emptyList<ContentPreviewDC>())

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
                    it.get("http://${MAINENDPOINT}/content/getContentPreview") {
                        headers["Auth"] = TOKEN
                        parameter("idPageProfile", idPageProfile)
                    }
            }
        }
    }

    var selectedPost by mutableStateOf<ContentPreviewDC?>(null)
    var selectedPostInfo by mutableStateOf<ContentUsersDC?>(null)

    var postScreenHandler = PostScreenHandler()

    var isVisiblePostWindow by mutableStateOf(false)
    var humanPhoto by mutableStateOf("")

    var isLiked by mutableStateOf(false)

    fun photoScreenClosed() {
        viewModelScope.launch {
            delay(400)
            swipableMenu.isReadyMenu = true
        }
    }

    fun getInfoAboutOnePost() {
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                selectedPostInfo =
                    it.get("http://${MAINENDPOINT}/content/getInfoAboutOnePost") {
                        headers["Auth"] = TOKEN
                        parameter("idPageProfile", selectedPage.id)
                        parameter("idPost", selectedPost?.id)
                    }
            }
        }

    }


    //    var imageProfile by mutableStateOf("")
//    var nameProfile by mutableStateOf(USERNAME)
    val listIconOther = listOf(
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
            })
    )

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
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_photo_svgrepo_com,
            offset = Offset(x = 50f, y = 45f)
        ) {
            menuHelper.changeVisibilityMenu(
                MENUS.NEWCONTENT
            )
        },
//        ItemSwappableMenu(
//            Image = R.drawable.ic_video,
//            offset = Offset(x = 70f, y = -10f),
//            onValueSelected = {
//
//            }
//        ),
    )

    val swipableMenu = SwappableMenu().apply {
        listIcon = listIconStateHuman
    }

    var initUserName by mutableStateOf("")
    var initImageProfile by mutableStateOf("images/avatars/avatar_6268d3987bcbb777469bb04c.png")
    var listPagesProfile by mutableStateOf(emptyList<PageProfileFormattedDC>())

    fun stateUserProfile(username: String, image: String, idUser: String, isOwnerMode: Boolean) {

        userMode = if (isOwnerMode) UserMode.OWNER else UserMode.OTHER
        initUserName = username
        initImageProfile = image
        listPagesProfile = emptyList()
        swipableMenu.listIcon = listIconSM
        getPagesProfileByIdUser(idUser)
//        listP
    }

    val nameProfile by derivedStateOf {
        if (stateProfile == StateProfile.HUMAN) initUserName else selectedPage.title
    }
    val imageProfile by derivedStateOf {
        if (stateProfile == StateProfile.HUMAN) initImageProfile else selectedPage.image
    }
    var isVisibleInfoUser by mutableStateOf(true)

    val listIconSM by derivedStateOf {
        if (userMode == UserMode.OWNER)
            if (stateProfile == StateProfile.HUMAN) listIconStateHuman else listIconStatePage
        else
            listIconOther
    }

    fun changeStateProfile(state: StateProfile) {
        stateProfile = state
        swipableMenu.listIcon = listIconSM
        if (stateProfile == StateProfile.HUMAN)
            isVisibleInfoUser = true
        else {
            getContentPage(selectedPage.id)
        }
//        when (stateProfile) {
//            StateProfile.HUMAN -> {
//                imageProfile = humanPhoto
//                nameProfile = USERNAME
//                isVisibleInfoUser = true
//                swipableMenu.listIcon = listIconStateHuman
//            }
//            StateProfile.PET -> {
//                getContentPage(selectedPage.id)
//                imageProfile = selectedPage.image
//                nameProfile = selectedPage.title
//                swipableMenu.listIcon = listIconStatePage
//            }
//        }
    }

    var isAddingNewCard by mutableStateOf(false)

    fun createNewPage(item: PageProfileDC) {
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
                if (response.status.isSuccess()) {
                    isAddingNewCard = false
                }
            }
        }
    }

    fun addNewImagePost(item: ContentRequestDC) {
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
                    it.post<HttpResponse>("http://${MainActivity.MAINENDPOINT}/content/addPostImage") {
                        headers["Auth"] = TOKEN
                        headers["Content-Type"] = "Application/Json"
                        body = item
                    }
                if (response.status.isSuccess()) {
                    menuHelper.changeVisibilityMenu(MENUS.NEWCONTENT)
                }
            }
        }
    }

//    fun getAvatar() {
//        viewModelScope.launch {
//            HttpClient(Android) {
//                install(HttpTimeout) {
//                    requestTimeoutMillis = 3000
//                }
//            }.use {
//
//                val response =
//                    it.get<HttpResponse>("http://${MainActivity.MAINENDPOINT}/avatar") {
//                        headers["Auth"] = TOKEN
//                    }
//                if (response.status.isSuccess()) {
//                    humanPhoto = response.readText()
//                    imageProfile = humanPhoto
//                }
//            }
//        }
//    }

    fun getAvatar() {
        viewModelScope.launch {
            HttpClient(Android) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {

                val response =
                    it.get<HttpResponse>("http://${MainActivity.MAINENDPOINT}/avatar") {
                        headers["Auth"] = TOKEN
                    }
                if (response.status.isSuccess()) {
                    humanPhoto = response.readText()
                    initImageProfile = humanPhoto
                }
            }
        }
    }

    fun changeAvatar(image: String) {
        viewModelScope.launch {
            HttpClient(Android) {
//                install(JsonFeature) {
//                    serializer = KotlinxSerializer()
//                }
//                expectSuccess = false
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {

                val response =
                    it.post<HttpResponse>("http://${MainActivity.MAINENDPOINT}/changeAvatar") {
                        headers["Auth"] = TOKEN
//                        headers["Content-Type"] = "text/plain"
                        body = image
                    }
                if (response.status.isSuccess()) {
                    humanPhoto = response.readText()
                    initImageProfile = humanPhoto
                }
            }
        }
    }

    private fun getPagesProfileByIdUser(idUser: String) {
        viewModelScope.launch {
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
//                expectSuccess = false
                install(HttpTimeout) {
                    requestTimeoutMillis = 30000
                }
            }.use {

                listPagesProfile =
                    it.get("http://${MainActivity.MAINENDPOINT}/getPagesProfileByIdUser") {
                        headers["Auth"] = TOKEN
                        parameter("idUser", idUser)
//                        headers["Content-Type"] = "text/plain"
//                        body = image
                    }
//                if (response.status.isSuccess()) {
//                    humanPhoto = response.readText()
//                    initImageProfile = humanPhoto
//                }
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class ContentRequestDC(
    val idPageProfile: String,
    var item: NewContentDC
)

@kotlinx.serialization.Serializable
data class NewContentDC(
    var type: String,
    var value: String,
    var description: String
)

fun encodeToBase64(image: Bitmap): String {
    val baos = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 60, baos)
    val byte_array = baos.toByteArray()
    val string = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        java.util.Base64.getEncoder().encodeToString(byte_array)
    } else {
        ""
    }
    string.replace("\\n", "")
    return string
}