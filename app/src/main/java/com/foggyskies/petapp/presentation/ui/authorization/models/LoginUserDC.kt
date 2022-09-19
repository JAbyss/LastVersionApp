package com.foggyskies.petapp.presentation.ui.authorization.models

@kotlinx.serialization.Serializable
data class LoginUserDC(
    var username: String,
    var password: String
)