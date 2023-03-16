package com.foggyskies.petapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.room.Room
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.foggyskies.petapp.MainActivity.Companion.TOKEN
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.PushNotificationService.Companion.ISAPPLIFE
import com.foggyskies.petapp.PushNotificationService.Companion.notificationsList
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.data.sharedpreference.PreferencesRepositoryImpl
import com.foggyskies.petapp.domain.db.UserDB
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.network.ConnectivityObserver
import com.foggyskies.petapp.network.NetworkConnectivityObserver
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationScreen
import com.foggyskies.petapp.presentation.ui.authorization.AuthorizationViewModel
import com.foggyskies.petapp.presentation.ui.chat.ChatScreen
import com.foggyskies.petapp.presentation.ui.chat.ChatViewModel
import com.foggyskies.petapp.presentation.ui.home.HomeMVIModel
import com.foggyskies.petapp.presentation.ui.home.HomeScreen
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import com.foggyskies.petapp.presentation.ui.navigationtree.NavTree
import com.foggyskies.petapp.presentation.ui.profile.ProfileScreen
import com.foggyskies.petapp.presentation.ui.profile.ProfileViewModel
import com.foggyskies.petapp.presentation.ui.splashscreen.SplashScreen
import com.foggyskies.petapp.temppackage.DownloadingScreen
import com.foggyskies.petapp.temppackage.DownloadingScreenMini
import com.foggyskies.petapp.workers.UploadFileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.scope.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject

enum class DBs {
    User
}

val mainModule = module {
    single(named(DBs.User)) {
        Room.databaseBuilder(
            androidContext(),
            UserDB::class.java,
            "chat-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        RepositoryUserDB(get(named(DBs.User)))
    }
    single {
        val SP = androidContext().getSharedPreferences("1", Context.MODE_PRIVATE)
        PreferencesRepositoryImpl(SP)
    }
    single {
        NetworkConnectivityObserver(androidContext())
    }
    single {
        ViewModelProvider(androidContext() as ComponentActivity)
    }
}

class MainActivity : ComponentActivity() {
    private val MY_PERMISSIONS_REQUEST = 1234
    private val PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )
    } else {
        arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ISAPPLIFE = true
        sharedPreference = application.getSharedPreferences("1", Context.MODE_PRIVATE)
        requestPermissions(PERMISSIONS, MY_PERMISSIONS_REQUEST)
        try {

            startKoin {
                androidContext(this@MainActivity)
                modules(listOf(mainModule))
            }
        } catch (_: java.lang.Exception) {

        }
        loader = ImageLoader.Builder(this)
            .bitmapFactoryMaxParallelism(1)
            .crossfade(true)
            .dispatcher(Dispatchers.Default)
//            .bitmapFactoryMaxParallelism(1)
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
//                    .directory(File("/storage/emulated/0/Download/Images/"))
                    .maxSizePercent(0.2)
                    .build()
            }
            .build()

        loaderForGallery = ImageLoader.Builder(this)
            .crossfade(true)
            .bitmapFactoryMaxParallelism(1)
            .dispatcher(Dispatchers.Default)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .respectCacheHeaders(false)
            .addLastModifiedToFileCacheKey(false)
            .allowRgb565(true)
            .build()
        loaderForPost =
            ImageLoader.Builder(this)
                .crossfade(true)

                .bitmapFactoryMaxParallelism(1)
                .dispatcher(Dispatchers.Default)
                .respectCacheHeaders(false)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .memoryCache {
                    MemoryCache.Builder(this)
                        .maxSizePercent(0.20)
                        .build()
                }
//                .respectCacheHeaders(false)
//                .addLastModifiedToFileCacheKey(false)
//                .allowRgb565(true)
                .build()
//        if (!LIFESERVICE){
//            Intent(this, PushNotificationService::class.java).also {
//                startService(it)
//                super.onPause()
//            }
//            Log.e("SERVISE STARTED", "dsssssssssssssss $LIFESERVICE")
//        }
//        val splashviewModel = SplashScreenViewModel()
//
//        installSplashScreen()
//            .setKeepOnScreenCondition{
//            !splashviewModel.isLoading
//        }


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        val downloadDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//        downloadDir.path
//        if (downloadDir.canWrite()) {
//            val dbFileName =
//            val src = FileInputStream(applicationContext.getDatabasePath(DbName.DATABASE_NAME))
//            val dst = FileOutputStream(File(downloadDir, dbFileName))
//            src.copyTo(dst)
//            src.close()
//            dst.close()
//        }
//        MediaStore.Downloads.
//        var directory = "/storage/emulated/0"
//        val originString = "$directory/RusLana"
//        val path = Paths.get(originString)
//        val file = File(originString)
//        if (!Files.exists(path)) {
////            file.mkdirs()
//            Files.createTempDirectory(path, "")
//        }
//        val readyPath = "$originString/image_12_123.png"
//        File(readyPath).writeText("awfawfawf")

        val connectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            isNetworkAvailable = connectivityObserver.observe()
                .collectAsState(initial = ConnectivityObserver.Status.Unavailable)
//            val connection_live_data = ConnectionLiveData(context)
//            isNetworkAvailable = connection_live_data.observeAsState(false)
            Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
                val nav_controller = rememberNavController()
                LoadingApp(nav_controller)
                val provider by inject<ViewModelProvider>(ViewModelProvider::class.java)
                val uploadModel = provider["UploadFileViewModel", UploadFileViewModel::class.java]
                AnimatedVisibility(
                    visible = uploadModel.miniDownloadVisible.value,
                    modifier = Modifier
                        .padding(top = 15.dp, end = 7.dp)
                        .align(Alignment.TopEnd)
                        .alpha(uploadModel.alphaForMini.value)
                ) {
                    DownloadingScreenMini(viewModel = uploadModel)
                }
                AnimatedVisibility(
                    visible = uploadModel.fullDownloadVisible.value,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    DownloadingScreen(viewModel = uploadModel)
                }
            }
        }
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );
    }

    fun onClick() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ISAPPLIFE = false
        Log.e("FLAGGGGGGG", "SETTTTTTTTTTTTTTTTTTTTTTT $ISAPPLIFE")
    }


    companion object {
        var USERNAME = ""
        var TOKEN = ""
        var IDUSER = ""
        lateinit var loader: ImageLoader
        lateinit var loaderForGallery: ImageLoader
        lateinit var loaderForPost: ImageLoader
        lateinit var sharedPreference: SharedPreferences

        lateinit var isNetworkAvailable: State<ConnectivityObserver.Status>
    }
}

@SuppressLint("SuspiciousIndentation")
@ExperimentalMaterialApi
@Composable
fun LoadingApp(nav_controller: NavHostController) {
    val uri = "https://www.example.com"

    val context = LocalContext.current

    val window: Window = (context as Activity).window
    val statusBarColor: Int = Color(0xFFC2C8CC).toArgb()
    window.statusBarColor = statusBarColor

    val viewModelProvider = ViewModelProvider(context as ComponentActivity)
    val mainSocketViewModel =
        viewModelProvider["MainSocketViewModel", (MainSocketViewModel::class.java)]

    val uploadFileViewModel =
        viewModelProvider["UploadFileViewModel", (UploadFileViewModel::class.java)]

    NavHost(navController = nav_controller, startDestination = NavTree.Splash.name) {
        composable(NavTree.Splash.name) {
            SplashScreen(nav_controller = nav_controller)
        }
        composable(NavTree.Authorization.name) {
            val viewModel =
                viewModelProvider["AuthorizationViewModel", (AuthorizationViewModel::class.java)]

            AuthorizationScreen(nav_controller, viewModel)
        }
        composable(NavTree.Home.name) {
            val viewModel = viewModelProvider["HomeViewModel", (HomeMVIModel::class.java)]

            SideEffect {
                Log.e(
                    "УТЕЧКА",
                    "УРОВЕНЬ НАВИГАЦИЯ АКТИВИТ\n\n${mainSocketViewModel.mainSocket.toString()}\n\n"
                )
            }

            HomeScreen(viewModel, nav_controller)
        }
        composable(
            NavTree.ChatSec.name
        ) {

            val str = it.arguments?.getString("itemChat") as String
            val item = Json.decodeFromString<FormattedChatDC>(str)

            val viewModel = viewModelProvider["ChatViewModel", (ChatViewModel::class.java)]
            ChatScreen(viewModel, item, mainSocketViewModel)
        }
        composable(
            "Chat/{itemChat}",
            arguments = listOf(navArgument("itemChat") {
                type = NavType.StringType
            }),
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/itemChat={itemChat}token={token}" })
        ) {
            val token = it.arguments?.getString("token")
            if (token != null) {
                TOKEN = token
                USERNAME = context.getSharedPreferences(
                    "User",
                    Context.MODE_PRIVATE
                ).getString("username", "").toString()
//                mainSocketViewModel.sendAction("deleteAllSentNotifications|")
                notificationsList = mutableListOf()
            }

            val str = it.arguments?.getString("itemChat") as String
            val item = Json.decodeFromString<FormattedChatDC>(str)

            val viewModel = viewModelProvider["ChatViewModel", (ChatViewModel::class.java)]
            ChatScreen(viewModel, item, mainSocketViewModel)
        }
        composable("Profile") {

            val viewModel =
                viewModelProvider["ProfileViewModel", (ProfileViewModel::class.java)]
            MainPreference.Username?.let {_ ->
                val isOwnerMode = it.arguments?.getBoolean("mode", true) ?: true
                val username = it.arguments?.getString("username", MainPreference.Username)!!
                val image = it.arguments?.getString("image", "")!!
                val idUser = it.arguments?.getString("idUser", MainPreference.IdUser)!!
                viewModel.stateUserProfile(username, image, idUser, isOwnerMode)
            }

//            LaunchedEffect(key1 = Unit) {
//
//            }
            ProfileScreen(
                nav_controller = nav_controller,
                viewModel,
                uploadFileViewModel
            )
        }
    }
}