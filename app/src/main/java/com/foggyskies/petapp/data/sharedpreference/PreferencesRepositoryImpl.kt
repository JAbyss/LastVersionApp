package com.foggyskies.petapp.data.sharedpreference

import android.content.SharedPreferences

class PreferencesRepositoryImpl constructor(
    private val preferences: SharedPreferences
): PreferencesRepository {

    override fun saveToken(token: String) {
        preferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return preferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveUsername(username: String){
        preferences.edit().putString(KEY_USERNAME, username).apply()
    }

    fun savePassword(password: String){
        preferences.edit().putString(KEY_PASSWORD, password).apply()
    }

    fun getUsername(): String? {
        return preferences.getString(KEY_USERNAME, null)
    }

    fun getPassword(): String? {
        return preferences.getString(KEY_PASSWORD, null)
    }

    fun saveIdUser(id: String) {
        preferences.edit().putString(KEY_ID_USER, id).apply()
    }

    fun getIdUser(): String? {
        return preferences.getString(KEY_ID_USER, null)
    }

    fun clearPreference(){
        preferences.edit().putString(KEY_ID_USER, null).apply()
        preferences.edit().putString(KEY_USERNAME, null).apply()
        preferences.edit().putString(KEY_PASSWORD, null).apply()
        preferences.edit().putString(KEY_ACCESS_TOKEN, null).apply()
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "pwjkbf"

        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "wQPMFC"

        const val KEY_ID_USER = "Gwtdng"
    }
}