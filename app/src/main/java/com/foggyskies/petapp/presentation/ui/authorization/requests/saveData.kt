package com.foggyskies.petapp.presentation.ui.authorization.requests

import com.foggyskies.petapp.MainActivity.Companion.sharedPreference
import com.foggyskies.petapp.data.sharedpreference.MainPreference
import com.foggyskies.petapp.data.sharedpreference.PreferencesRepositoryImpl
import com.foggyskies.petapp.presentation.ui.authorization.models.SaveAuthData

fun saveData(
    data: SaveAuthData
) {
    MainPreference.Token = data.idToken
    MainPreference.IdUser = data.idUser
    MainPreference.Username = data.username
    MainPreference.Password = data.password
}