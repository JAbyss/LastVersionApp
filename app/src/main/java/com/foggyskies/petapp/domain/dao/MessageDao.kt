package com.foggyskies.petapp.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage

@Dao
interface MessageDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOne(chatMessage: ChatMessage)

//    @Query("SELECT TOP (50) * FROM :string ORDER BY date ASC")
//    suspend fun getFiftyMessage(string: String): List<ChatMessage>
}