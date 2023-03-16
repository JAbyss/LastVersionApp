package com.foggyskies.petapp.presentation.ui.authorization.models

@kotlinx.serialization.Serializable
data class RegistrationUserWithCodeDC(
    var username: String,
    var password: String,
    var e_mail: String,
    var code: String
) {
    fun toAuth() = LoginUserDC(
        username = username,
        password = password
    )
}