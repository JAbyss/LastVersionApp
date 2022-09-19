package com.foggyskies.petapp.presentation.ui.profile

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.foggyskies.petapp.R
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.presentation.ui.MenuVisibilityHelper
import com.foggyskies.petapp.presentation.ui.home.entity.ItemSwappableMenu
import com.foggyskies.petapp.presentation.ui.home.entity.SwappableMenu
import com.foggyskies.petapp.presentation.ui.home.widgets.post.ContentUsersDC
import com.foggyskies.petapp.presentation.ui.home.widgets.post.PostScreenHandler
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.profile.requests.contentPreview
import com.foggyskies.petapp.presentation.ui.profile.requests.deletePageProfile
import com.foggyskies.petapp.presentation.ui.profile.requests.logOut
import com.foggyskies.petapp.presentation.ui.profile.requests.pagesProfileByIdUser
import com.foggyskies.petapp.temppackage.GalleryHandler
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream


enum class StateProfile {
    HUMAN, PET
}

enum class MENUS {
    NEWCONTENT, POST, RIGHT, SEARCHUSERS, CHATS, FRIENDS, ATTACH
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

    val backgroundScope = CoroutineScope(IO)

    val startString = "Описание публикации..."

    var userMode by mutableStateOf(UserMode.OWNER)

    val menuHelper = MenuVisibilityHelper(action = { swipableMenu.isReadyMenu = it })

    var descriptionMenuNewContent by mutableStateOf(startString)

    var focusState by mutableStateOf(false)

    val profileHandler = GalleryHandler()

    val postScreenHandler = PostScreenHandler()

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

    var selectedPost by mutableStateOf<ContentPreviewDC?>(null)
    var selectedPostInfo by mutableStateOf<ContentUsersDC?>(null)

    var isVisiblePostWindow by mutableStateOf(false)

    var isLiked by mutableStateOf(false)

    fun photoScreenClosed() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(400)
            swipableMenu.isReadyMenu = true
        }
    }

    val listIconOther = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_back,
            position = SwappableMenu.PositionsIcons.TOP,
            onValueSelected = {
                it.navigate(it.backQueue[1].destination.route!!)
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
           position = SwappableMenu.PositionsIcons.TOP_LEFT,
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
            position = SwappableMenu.PositionsIcons.LEFT,
            onValueSelected = {
                it.navigate("Home")
            })
    )

    val listIconStateHuman = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_back,
            position = SwappableMenu.PositionsIcons.TOP,
            onValueSelected = {
                it.navigate(it.backQueue[1].destination.route!!)
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
            position = SwappableMenu.PositionsIcons.TOP_LEFT,
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
            position = SwappableMenu.PositionsIcons.LEFT,
            onValueSelected = {
                it.navigate("Home")
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_logout,
            position = SwappableMenu.PositionsIcons.RIGHT,
            onValueSelected = {
                logOut(it)
            }
        )
    )

    val listIconStatePage = listOf(
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_back,
            position = SwappableMenu.PositionsIcons.TOP,
            onValueSelected = {
                changeStateProfile(StateProfile.HUMAN)
            }
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_menu_ads,
            position = SwappableMenu.PositionsIcons.TOP_LEFT,
            onValueSelected = {
            }
        ),
        ItemSwappableMenu(Image = R.drawable.ic_menu_home_1,
            animationImages = listOf(
                R.drawable.ic_menu_home_1,
                R.drawable.ic_menu_home_2,
                R.drawable.ic_menu_home_3,
            ),
            position = SwappableMenu.PositionsIcons.LEFT,
            isAnimate = true,
            onValueSelected = {
            }),
        ItemSwappableMenu(
            Image = R.drawable.ic_delete,
            position = SwappableMenu.PositionsIcons.RIGHT,
            onValueSelected = {
                deletePageProfile()
            }
        ),
        ItemSwappableMenu(
            Image = R.drawable.ic_photo_svgrepo_com,
            position = SwappableMenu.PositionsIcons.BOTTOM
        ) {
            menuHelper.changeVisibilityMenu(
                MENUS.NEWCONTENT
            )
        },
    )

    val swipableMenu = SwappableMenu().apply {
        listIcon = listIconStateHuman
    }

    var initUserName by mutableStateOf("")
    var initImageProfile by mutableStateOf("")
//    var listPagesProfile by mutableStateOf(emptyList<PageProfileFormattedDC>())

    var listPagesProfile = mutableStateListOf<PageProfileFormattedDC>()
    var initImagePageProfile by mutableStateOf(selectedPage.image)
    var isInitialized by mutableStateOf(false)
    fun stateUserProfile(username: String, image: String, idUser: String, isOwnerMode: Boolean) {

        if (initUserName != username){
//        if (!isInitialized) {
            userMode = if (isOwnerMode) UserMode.OWNER else UserMode.OTHER
            initUserName = username
            initImageProfile = image
            listPagesProfile.clear()
            swipableMenu.listIcon = listIconSM
            pagesProfileByIdUser(idUser)
//            isInitialized = true
        }
//        listP
    }

    val nameProfile by derivedStateOf {
        if (stateProfile == StateProfile.HUMAN) initUserName else selectedPage.title
    }
    val imageProfile by derivedStateOf {
        if (stateProfile == StateProfile.HUMAN) initImageProfile else initImagePageProfile
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
            contentPreview(selectedPage.id)
            initImagePageProfile = selectedPage.image
        }
    }

    var isAddingNewCard by mutableStateOf(false)

//    fun createNewPage(item: PageProfileDC) {
//        if (isNetworkAvailable.value)
//            CoroutineScope(Dispatchers.IO).launch {
//                HttpClient(Android) {
////                    install(JsonFeature) {
////                        serializer = KotlinxSerializer()
////                    }
////                    install(ContentNegotiation){
////                        json(Json {
////                            prettyPrint = true
////                            isLenient = true
////                        })
////                    }
//                    expectSuccess = false
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 30000
//                    }
//                }.use {
//                    val response: HttpResponse =
//                        it.post("${Routes.SERVER.REQUESTS.BASE_URL}/createPageProfile") {
//                            headers["Auth"] = TOKEN
//                            headers["Content-Type"] = "Application/Json"
//                            setBody(item)
//                        }
//                    if (response.status.isSuccess()) {
//                        isAddingNewCard = false
////                        listPagesProfile += item
//                    }
//                }
//            }
//    }

    /**
     * Отправляет на сервер новый пост ContentRequestDC
     * Если ответ от сервера 200-300, то добавляет созданный пост в лист контента
     * @param ContentRequestDC
     */
//    fun addNewImagePost(item: ContentRequestDC) {
//        if (isNetworkAvailable.value)
//            CoroutineScope(Dispatchers.IO).launch {
//                HttpClient(Android) {
////                    install(JsonFeature) {
////                        serializer = KotlinxSerializer()
////                    }
////                    install(ContentNegotiation){
////                        json(Json {
////                            prettyPrint = true
////                            isLenient = true
////                        })
////                    }
//                    expectSuccess = false
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 30000
//                    }
//                }.use {
////                            val responseRegistration =
//                    val response: HttpResponse =
//                        it.post("${Routes.SERVER.REQUESTS.BASE_URL}/content/addPostImage") {
//                            headers["Auth"] = TOKEN
//                            headers["Content-Type"] = "Application/Json"
//                            setBody(item)
//                        }
//                    if (response.status.isSuccess()) {
//                        menuHelper.changeVisibilityMenu(MENUS.NEWCONTENT)
//                        selectedPage.apply {
//                            countContents = "${((countContents.toInt()) + 1)}"
//                        }
//                        listPostImages = listPostImages + response.body<ContentPreviewDC>()
//                    }
//                }
//            }
//    }

//    fun checkInternet(func: () -> Unit) {
//        if (isNetworkAvailable.value) {
//            func()
//        }
//    }

//    fun getAvatar() {
//        if (isNetworkAvailable.value)
//            CoroutineScope(Dispatchers.IO).launch {
//                HttpClient(Android) {
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 30000
//                    }
//                }.use {
//
//                    val response: HttpResponse =
//                        it.get("${Routes.SERVER.REQUESTS.BASE_URL}/avatar") {
//                            headers["Auth"] = TOKEN
//                        }
//                    if (response.status.isSuccess()) {
//                        humanPhoto = response.bodyAsText()
//                        initImageProfile = humanPhoto
//                    }
//                }
//            }
//    }

//    fun changeAvatar(image: String) {
//        if (isNetworkAvailable.value)
//            CoroutineScope(Dispatchers.IO).launch {
//                HttpClient(Android) {
////                install(JsonFeature) {
////                    serializer = KotlinxSerializer()
////                }
////                    install(ContentNegotiation){
////                        json(Json {
////                            prettyPrint = true
////                            isLenient = true
////                        })
////                    }
////                expectSuccess = false
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 3000
//                    }
//                }.use {
//
//                    val response: HttpResponse =
//                        it.post("${Routes.SERVER.REQUESTS.BASE_URL}/changeAvatar") {
//                            headers["Auth"] = TOKEN
////                        headers["Content-Type"] = "text/plain"
//                            setBody(image)
//                        }
//                    if (response.status.isSuccess()) {
//                        humanPhoto = response.bodyAsText()
//                        initImageProfile = humanPhoto
//                    }
//                }
//            }
//    }

//    fun changeAvatarPageProfile(image: String) {
//        if (isNetworkAvailable.value)
//            CoroutineScope(Dispatchers.IO).launch {
//                HttpClient(Android) {
////                install(JsonFeature) {
////                    serializer = KotlinxSerializer()
////                }
////                    install(ContentNegotiation){
////                        json(Json {
////                            prettyPrint = true
////                            isLenient = true
////                        })
////                    }
////                expectSuccess = false
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 30000
//                    }
//                }.use {
//
//                    val response: HttpResponse =
//                        it.post("${Routes.SERVER.REQUESTS.BASE_URL}/changeAvatarProfile") {
//                            headers["Auth"] = TOKEN
//                            headers["idPage"] = selectedPage.id
////                        headers["Content-Type"] = "text/plain"
//                            setBody(image)
//                        }
//                    if (response.status.isSuccess()) {
//                        initImagePageProfile = response.bodyAsText()
//                        listPagesProfile.forEach { page ->
//                            if (page.id == selectedPage.id) {
//                                page.image = initImagePageProfile
//                                return@forEach
//                            }
//                        }
//                    }
//                }
//            }
//    }

//    private fun getPagesProfileByIdUser(idUser: String) {
//        if (isNetworkAvailable.value)
//            CoroutineScope(Dispatchers.IO).launch {
//                HttpClient(Android) {
////                    install(JsonFeature) {
////                        serializer = KotlinxSerializer()
////                    }
////                    install(ContentNegotiation){
////                        json(Json {
////                            prettyPrint = true
////                            isLenient = true
////                        })
////                    }
////                expectSuccess = false
////                    install(ContentNegotiation){
////                        json(Json {
////                            prettyPrint = true
////                            isLenient = true
////                        })
////                    }
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 30000
//                    }
//                }.use {
//
//                    listPagesProfile =
//                        it.get("${Routes.SERVER.REQUESTS.BASE_URL}/getPagesProfileByIdUser") {
//                            headers["Auth"] = TOKEN
//                            parameter("idUser", idUser)
////                        headers["Content-Type"] = "text/plain"
////                        body = image
//                        }.body()
////                if (response.status.isSuccess()) {
////                    humanPhoto = response.readText()
////                    initImageProfile = humanPhoto
////                }
//                }
//            }
//    }
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

//todo Переделать сжатие картинок
fun encodeToBase64(image: Bitmap): String {
    val baos = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 60, baos)
    val byte_array = baos.toByteArray()
    val string = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        java.util.Base64.getEncoder().encodeToString(byte_array)
    } else {
//        Base64.getEncoder().encode(byte_array);
        android.util.Base64.encodeToString(byte_array, android.util.Base64.NO_WRAP)
    }
    string.replace("\\n", "")
    return string
}