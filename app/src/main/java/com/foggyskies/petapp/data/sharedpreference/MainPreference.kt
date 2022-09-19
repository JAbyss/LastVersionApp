package com.foggyskies.petapp.data.sharedpreference

import org.koin.java.KoinJavaComponent.inject

object MainPreference {
    val sp by inject<PreferencesRepositoryImpl>(PreferencesRepositoryImpl::class.java)

    var Password
        get() = sp.getPassword() ?: ""
        set(value) = sp.savePassword(value)

    var Username
        get() = sp.getUsername()
        set(value) = sp.saveUsername(value!!)

    var Token
        get() = sp.getToken() ?: ""
        set(value) = sp.saveToken(value)

    var IdUser
        get() = sp.getIdUser() ?: ""
        set(value) = sp.saveIdUser(value)

    fun clearPreference(){
        sp.clearPreference()
    }
}