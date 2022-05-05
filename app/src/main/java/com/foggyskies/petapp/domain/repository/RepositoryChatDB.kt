package com.foggyskies.petapp.domain.repository

import com.foggyskies.petapp.MainSocketViewModel
import com.foggyskies.petapp.domain.db.ChatDB
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

class RepositoryChatDB(
    val dbChat: ChatDB
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
        val localChats = dbChat.chatDao().getAllChats()
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
            dbChat.chatDao().insertChat(it.toChat())
        }
        deletedItems.forEach {
            dbChat.chatDao().deleteChat(it.toChat())
        }
    }

    suspend fun insertMessage(idChat: String, message: ChatMessage) {
        dbChat.insertMessages(idChat, message = message)
    }
}