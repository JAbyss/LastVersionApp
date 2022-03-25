package com.foggyskies.petapp.presentation.ui.adhomeless.entity

@kotlinx.serialization.Serializable
data class UserIUSI(
    var id: String,
    var username: String,
    var status: String,
    var image: String
)
