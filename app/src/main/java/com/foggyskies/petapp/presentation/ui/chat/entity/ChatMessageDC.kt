package com.foggyskies.petapp.presentation.ui.chat.entity

import com.foggyskies.petapp.presentation.ui.mainmenu.screens.FormattedChatDC
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDC(
//    @SerialName(value = "_id")
    @SerialName("_id")
    var id: String,
    var idUser: String,
    var author: String = "",
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList(),
    var listFiles: List<FileDC> = emptyList()
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
@Serializable
data class FileDC(
    val name: String = "",
    val size: String = "",
    val type: String = "",
    val path: String
){
    val fullName = "$name.$type"
}