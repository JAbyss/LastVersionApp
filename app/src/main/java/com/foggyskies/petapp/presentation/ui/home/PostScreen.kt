package com.foggyskies.petapp.presentation.ui.home

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.foggyskies.petapp.MainActivity
import com.foggyskies.petapp.MainActivity.Companion.isNetworkAvailable
import com.foggyskies.petapp.R
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.chat.entity.CommentDC
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.globalviews.post.BottomCommentBar
import com.foggyskies.petapp.presentation.ui.globalviews.post.sidescreens.CommentsScreen
import com.foggyskies.petapp.presentation.ui.globalviews.post.sidescreens.ImageScreen
import com.foggyskies.petapp.presentation.ui.globalviews.post.sidescreens.LikesScreen
import com.foggyskies.petapp.presentation.ui.profile.human.ContentPreviewDC
import com.foggyskies.petapp.routs.Routes
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@kotlinx.serialization.Serializable
data class FormattedCommentDC(
    val users: HashMap<String, UserIUSI>,
    var comments: List<CommentDC>
)

class PostScreenHandler {

    var isTagMenuOpen by mutableStateOf(false)

    var statePost by mutableStateOf(StatePost.IMAGE)

    var commentValue by mutableStateOf(TextFieldValue(""))

    var iconUsersReply by mutableStateOf(true)

//    var selectedPage by mutableStateOf<PageProfileFormattedDC?>(null)

    var selectedPost by mutableStateOf<SelectedPostWithIdPageProfile?>(null)

    var listComments by mutableStateOf<FormattedCommentDC>(
        FormattedCommentDC(
            users = hashMapOf(),
            comments = emptyList()
        )
    )

    var likedUsersList by mutableStateOf(emptyList<UserIUSI>())

    var isLiked by mutableStateOf(false)

    @OptIn(
        ExperimentalAnimationApi::class,
        com.google.accompanist.pager.ExperimentalPagerApi::class
    )
    @Composable
    fun PostScreen(
        onLongPress: (Offset) -> Unit
    ) {

        var isVisibleLikeAnimation by remember {
            mutableStateOf(false)
        }

        var isStartSecondStepAnimation by remember {
            mutableStateOf(false)
        }

//        val scope = rememberCoroutineScope()

        fun doubleTapLike() {
//            scope.launch {
            CoroutineScope(Dispatchers.Default).launch {
                likePost()
//                doubleTapAction()
                isVisibleLikeAnimation = true
                delay(200)
                isStartSecondStepAnimation = true
                delay(500)
                isVisibleLikeAnimation = false
                isStartSecondStepAnimation = false
            }
        }

        val context = LocalContext.current

        val display_metrics = LocalContext.current.resources.displayMetrics


        val height: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics =
                    (context as Activity).windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                windowMetrics.bounds.height() - insets.bottom - insets.top
            } else {
                val resourceId =
                    context.resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    display_metrics.heightPixels - context.resources.getDimensionPixelSize(
                        resourceId
                    )
                } else
                    0
            }

        val Hheiht = (height / display_metrics.density).toInt() * 0.8f

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth(0.92f)
                .height(Hheiht.dp)
                .background(Color.White)
        ) {

            val state = rememberLazyListState()

            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
            ) {
                AnimatedContent(
                    targetState = this@PostScreenHandler.statePost,
                    transitionSpec = {
                        slideInVertically(
                            animationSpec = tween(400),
                            initialOffsetY = { it }) with fadeOut(animationSpec = tween(400)) + slideOutVertically(
                            animationSpec = tween(400),
                            targetOffsetY = { it })
                    }
                ) { stateTarget ->
                    when (stateTarget) {
                        StatePost.IMAGE -> {
                            ImageScreen(
                                selectedPost?.item?.address!!,
                                selectedPost?.description!!,
                                onDoubleTap = ::doubleTapLike,
//                                onLongPress = onLongPress
                            )
                        }
                        StatePost.COMMENTS -> {
                            CommentsScreen(state, this@PostScreenHandler)
                        }
                        StatePost.LIKES -> {
                            LikesScreen(this@PostScreenHandler)
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = isVisibleLikeAnimation,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {

                    val maxDimension = 80f
                    val animationSpec = tween<Float>(700, easing = FastOutLinearInEasing)

                    val animatables = listOf(
                        remember { Animatable(0f) },
                        remember { Animatable(0f) }
                    )

                    animatables.forEachIndexed { index, animatable ->
                        LaunchedEffect(animatable) {
                            animatable.animateTo(
                                targetValue = 1f,
                                animationSpec = animationSpec
                            )
                        }
                    }

                    val dys = animatables.map { it.value }
                    Box(
                        Modifier.align(Alignment.Center)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                                .graphicsLayer(alpha = 0.99f)
                        ) {

                            dys.forEach { dy ->
                                drawCircle(
                                    color = Color.White,
                                    radius = maxDimension * dy * 2f,
                                    alpha = 1 - dy
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isStartSecondStepAnimation,
                            modifier = Modifier.align(Alignment.Center)
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.ic_like),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.Center),
                                Color.White
                            )
                        }
                    }
                }
            }
            BottomCommentBar(this@PostScreenHandler)
        }
    }

    suspend fun likePost() {
        if (isNetworkAvailable.value)
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 30000
                }
            }.use {
                isLiked = it.get("${Routes.SERVER.REQUESTS.BASE_URL}/content/addLikeToPost") {
                    this.headers["Auth"] = MainActivity.TOKEN
                    parameter("idPageProfile", selectedPost?.idPageProfile)
                    parameter("idPost", selectedPost?.item?.id)
                }
                selectedPost?.isLiked = isLiked
            }
    }

    suspend fun sendNewComment() {

        if (commentValue.text.isNotBlank() || commentValue.text.isNotEmpty()) {
            val comment = CommentDC(
                id = "",
                idUser = MainActivity.IDUSER,
                message = commentValue.text,
                date = ""
            )
            listComments.comments = listComments.comments + comment
            if (isNetworkAvailable.value)
                HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                    install(HttpTimeout) {
                        requestTimeoutMillis = 30000
                    }
                }.use {
                    it.post<HttpResponse>("${Routes.SERVER.REQUESTS.BASE_URL}/content/addCommentToPost") {
                        headers["Auth"] = MainActivity.TOKEN
                        headers["Content-Type"] = "Application/Json"
                        parameter("idPageProfile", selectedPost?.idPageProfile)
                        parameter("idPost", selectedPost?.item?.id!!)
                        body = comment
                    }
                }
            commentValue = TextFieldValue("")
        }
    }

    suspend fun getLikedUsers() {
        if (isNetworkAvailable.value)
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                likedUsersList =
                    it.get("${Routes.SERVER.REQUESTS.BASE_URL}/content/getLikedUsers") {
                        this.headers["Auth"] = MainActivity.TOKEN
                        parameter("idPageProfile", selectedPost?.idPageProfile)
                        parameter("idPost", selectedPost?.item?.id!!)
                    }
            }
    }

    private suspend fun getInfoAboutOnePost() {
        if (isNetworkAvailable.value)
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                selectedPost =
                    it.get("${Routes.SERVER.REQUESTS.BASE_URL}/content/getInfoAboutOnePost") {
                        this.headers["Auth"] = MainActivity.TOKEN
                        parameter("idPageProfile", selectedPost?.idPageProfile)
                        parameter("idPost", selectedPost?.item?.id!!)
                    }
            }
    }

    fun selectPost(
        postSPWIP: SelectedPostWithIdPageProfile,
        action: () -> Unit
    ) {
        selectedPost = postSPWIP
        isLiked = postSPWIP.isLiked

        action()
    }

    suspend fun getComments() {
        if (isNetworkAvailable.value)
            HttpClient(Android) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 3000
                }
            }.use {
                listComments = it.get("${Routes.SERVER.REQUESTS.BASE_URL}/content/getComments") {
                    this.headers["Auth"] = MainActivity.TOKEN
                    parameter("idPageProfile", selectedPost?.idPageProfile)
                    parameter("idPost", selectedPost?.item?.id!!)
                }
            }
    }
}

data class UsersSearchState(
    var isLoading: Boolean = false,
    var users: List<UsersSearch> = listOf()
) {
    fun clear() {
        users = emptyList()
        isLoading = false
    }
}

@kotlinx.serialization.Serializable
data class SelectedPostWithIdPageProfile(
    var idPageProfile: String,
    var item: ContentPreviewDC,
    var author: String,
    var image: String,
    var description: String,
    var countLikes: String = "",
    var countComets: String = "",
    var isLiked: Boolean = false
)

@kotlinx.serialization.Serializable
data class ContentUsersDC(
    var id: String,
    var type: String,
    var likes: List<String>,
    var comments: List<CommentDC>,
    var address: String,
    var description: String = ""
)

enum class StatePost {
    IMAGE, COMMENTS, LIKES
}