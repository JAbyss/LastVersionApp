package com.foggyskies.petapp.presentation.ui.authorization.models;

@kotlinx.serialization.Serializable
data class Token(
    var token: String
)

@kotlinx.serialization.Serializable
data class SaveAuthData(
    var idToken: String,
//    var idUser: String,
    var username: String,
    var password: String
)