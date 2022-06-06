package com.foggyskies.petapp.presentation.ui.chat.entity

@kotlinx.serialization.Serializable
data class CommentDC(
    var id: String,
    var idUser: String,
    var message: String,
    var date: String
)