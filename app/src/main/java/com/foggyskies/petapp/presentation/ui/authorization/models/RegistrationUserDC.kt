package com.foggyskies.petapp.presentation.ui.authorization.models

@kotlinx.serialization.Serializable
data class RegistrationUserDC(
    var username: String,
    var password: String,
    var e_mail: String
) {
    fun toRegistrationWithCode(code: String): RegistrationUserWithCodeDC {
        return RegistrationUserWithCodeDC(
            username,
            password,
            e_mail,
            code
        )
    }

    fun toAuth(): LoginUserDC {
        return LoginUserDC(
            username,
            password
        )
    }
}