//package com.foggyskies.petapp.data
//
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.foggyskies.petapp.data.Chat.Companion.TABLE_CHAT
//
//@Entity(tableName = "Chat")
//data class Chat(
//    @PrimaryKey
//    @ColumnInfo(name = "idChat")
//    val idChat: String,
//    @ColumnInfo(name = "companionId")
//    val companionId: String,
//    @ColumnInfo(name = "companionName")
//    val companionName: String,
//    @ColumnInfo(name = "image")
//    val imageCompanion: String,
//    @ColumnInfo(name = "lastMessage")
//    val lastMessage: String
//){
//    companion object {
//        const val TABLE_CHAT = "Chat"
//    }
//}
