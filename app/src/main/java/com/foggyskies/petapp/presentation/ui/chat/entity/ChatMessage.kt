package com.foggyskies.petapp.presentation.ui.chat.entity

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ChatMessage(
    var id: String ,
    var author: String,
    var date: String,
    var message: String,
    var listImages: List<String> = emptyList()
)
