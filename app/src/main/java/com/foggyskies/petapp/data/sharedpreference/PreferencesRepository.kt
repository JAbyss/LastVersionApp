package com.foggyskies.petapp.data.sharedpreference

interface PreferencesRepository {

    // Auth
    fun saveToken(token: String)

    fun getToken(): String?
}