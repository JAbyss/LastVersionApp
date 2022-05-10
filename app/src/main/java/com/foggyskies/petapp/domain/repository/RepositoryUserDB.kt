package com.foggyskies.petapp.domain.repository

import androidx.compose.runtime.toMutableStateList
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.domain.db.UserDB
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

class RepositoryUserDB(
    val dbUser: UserDB
) {

    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
        }
    }

    suspend fun getChats(msViewModel: MainSocketViewModel) {
        val localChats = dbUser.chatDao().getAllChats()
        val formattedChat = localChats.map {
            FormattedChatDC(
                id = it.idChat,
                nameChat = it.companionName,
                image = it.imageCompanion,
                idCompanion = it.companionId,
                lastMessage = it.lastMessage
            )
        }
        msViewModel.listChats = formattedChat.toMutableList()
        msViewModel.sendAction("getChats|")
    }

    suspend fun updateChats(
        needAddItems: List<FormattedChatDC>,
        deletedItems: List<FormattedChatDC>
    ) {
        needAddItems.forEach {
            dbUser.chatDao().insertChat(it.toChat())
        }
        deletedItems.forEach {
            dbUser.chatDao().deleteChat(it.toChat())
        }
    }

    suspend fun updateFriends(
        needAddItems: List<UserIUSI>,
        deletedItems: List<UserIUSI>
    ){
        needAddItems.forEach {
            dbUser.friendDao().insertOne(it.toFriendTable())
        }
        deletedItems.forEach {
            dbUser.friendDao().deleteOne(it.toFriendTable())
        }
    }

    suspend fun insertMessage(idChat: String, message: ChatMessage) {
        dbUser.insertMessages(idChat, message = message)
    }

    suspend fun getFriends(msViewModel: MainSocketViewModel) {
        val localFriends = dbUser.friendDao().getFriends()
        val formattedList = localFriends.map { it.toIUSI() }
        msViewModel.listFriends = formattedList.toMutableStateList()
        msViewModel.sendAction("getFriends|")
    }
}