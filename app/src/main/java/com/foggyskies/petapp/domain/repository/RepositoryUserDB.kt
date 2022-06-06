package com.foggyskies.petapp.domain.repository

import androidx.compose.runtime.toMutableStateList
import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.domain.db.UserDB
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
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
        }.toMutableList()
        // FIXME UNKNOWN
        msViewModel.listNewMessages.forEach { newChat ->
            formattedChat.forEachIndexed { index, it ->
                if (newChat.id == it.id){
                    formattedChat[index] = formattedChat[index].copy(lastMessage = newChat.new_message.message)
                }
            }
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

    suspend fun insertMessage(idChat: String, message: ChatMessageDC) {
        dbUser.insertMessages(idChat, message = message)
    }
    //FiXME IN WORK
    suspend fun deleteMessage(idChat: String, idMessage: String){
        dbUser.deleteMessage(idChat, idMessage)
    }

    suspend fun getFriends(msViewModel: MainSocketViewModel) {
        val localFriends = dbUser.friendDao().getFriends()
        val formattedList = localFriends.map { it.toIUSI() }
        msViewModel.listFriends = formattedList.toMutableStateList()
        msViewModel.sendAction("getFriends|")
    }
}