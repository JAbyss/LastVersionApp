package com.foggyskies.petapp.presentation.ui.adhomeless.entity

import com.foggyskies.petapp.data.FriendTable

@kotlinx.serialization.Serializable
data class UserIUSI(
    var id: String,
    var username: String,
    var status: String,
    var image: String
){
    fun toFriendTable(): FriendTable {
        return FriendTable(
            idUser = id,
            status = status,
            image = image,
            username = username
        )
    }
}
