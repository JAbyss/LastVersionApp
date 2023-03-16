package com.foggyskies.petapp.domain.dao

import androidx.room.*
import com.foggyskies.petapp.data.Chat
import com.foggyskies.petapp.data.Chat.Companion.TABLE_CHAT

@Dao
interface ChatDao {

    @Query("SELECT * FROM $TABLE_CHAT WHERE idChat LIKE :idChat ")
    suspend fun getChatByIdChat(idChat: String): Chat

    @Query("SELECT EXISTS(SELECT idChat FROM $TABLE_CHAT WHERE idChat LIKE :idChat)")
    suspend fun checkOnExistChat(idChat: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Delete
    suspend fun deleteChat(chat: Chat)

    @Query("SELECT * FROM $TABLE_CHAT")
    suspend fun getAllChats(): List<Chat>

}