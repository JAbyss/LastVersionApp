package com.foggyskies.petapp.presentation.ui.chat.entity

import kotlinx.serialization.Serializable

@Serializable
data class ChatUserEntity(
    var idUser: String,
    var nameUser: String
)

@Serializable
data class ChatMainEntity(
    var idChat: String,
    var users: List<ChatUserEntity>
)