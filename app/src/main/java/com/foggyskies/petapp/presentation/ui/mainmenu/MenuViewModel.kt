package com.foggyskies.petapp.presentation.ui.mainmenu

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.foggyskies.petapp.DBs
import com.foggyskies.petapp.domain.db.UserDB
import com.foggyskies.petapp.domain.repository.RepositoryUserDB
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.globalviews.UsersSearch
import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class MenuViewModel : ViewModel() {

    val backgroundScope = CoroutineScope(IO)

    val db: UserDB by KoinJavaComponent.inject(UserDB::class.java, named(DBs.User))

    // ЛИСТЫ ДЛЯ ЗАПРОСОВ

    var listFriends = mutableStateListOf<UserIUSI>()
    val listChats = mutableStateListOf<FormattedChatDC>()
    var listRequestsFriends = mutableStateListOf<UserIUSI>()
    val listFoundedUsers = mutableStateListOf<UsersSearch>()

    val mapChats = mutableStateMapOf<String, MutableState<FormattedChatDC>>()

    var selectedPage by mutableStateOf(Screens.IDLE)


}