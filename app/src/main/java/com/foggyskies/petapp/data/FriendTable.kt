package com.foggyskies.petapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foggyskies.petapp.data.FriendTable.Companion.TABLE_FRIEND
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.UserIUSI

@Entity(tableName = TABLE_FRIEND)
data class FriendTable(
    @PrimaryKey
    @ColumnInfo(name = "idUser")
    val idUser: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "image")
    val image: String,
){
    companion object {
        const val TABLE_FRIEND = "Friends"
    }

    fun toIUSI(): UserIUSI {
        return UserIUSI(
            id = idUser,
            status = status,
            username = username,
            image = image
        )
    }
}
