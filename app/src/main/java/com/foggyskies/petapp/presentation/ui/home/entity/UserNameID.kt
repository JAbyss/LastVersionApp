package com.foggyskies.petapp.presentation.ui.home.entity

@kotlinx.serialization.Serializable
data class UserNameID(
    var id: String,
    var username: String
)