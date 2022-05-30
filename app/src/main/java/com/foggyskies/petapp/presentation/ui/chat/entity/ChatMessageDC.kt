package com.foggyskies.petapp.presentation.ui.chat.entity

import com.foggyskies.petapp.presentation.ui.globalviews.FormattedChatDC
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDC(
//    @SerialName(value = "_id")
    var id: String,
    var idUser: String,
    var author: String = "",
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList()
){
    fun toFC(): FormattedChatDC {
        return FormattedChatDC(
            id = id,
            nameChat = author,
            idCompanion = idUser,
            image = "",
            lastMessage = message
        )
    }
}