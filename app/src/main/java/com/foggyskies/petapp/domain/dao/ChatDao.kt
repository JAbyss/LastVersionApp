package com.foggyskies.petapp.domain.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.room.*
import com.foggyskies.petapp.data.Chat
import com.foggyskies.petapp.data.Chat.Companion.TABLE_CHAT
import com.foggyskies.petapp.domain.db.Messages
import com.foggyskies.petapp.presentation.ui.chat.MessageDC
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage

@Dao
interface ChatDao {

    //    @Query("SELECT TOP (50) messages FROM $TABLE_CHAT WHERE :idChat LIKE idChat ORDER BY ")
//    fun getFiftyMessages(idChat: String): List<ChatMessage>
    @Query("SELECT * FROM $TABLE_CHAT WHERE idChat LIKE :idChat ")
    suspend fun getChatByIdChat(idChat: String): Chat

    @Query("SELECT EXISTS(SELECT idChat FROM $TABLE_CHAT WHERE idChat LIKE :idChat)")
    suspend fun checkOnExistChat(idChat: String): Boolean

//    @Query("CREATE TABLE ${Messages.TABLE_NAME}:tableName (" +
//            " ${Messages.COLUMN_ID} TEXT PRIMARY KEY," +
//            " ${Messages.COLUMN_AUTHOR} TEXT, ${Messages.COLUMN_DATE} TEXT," +
//            " ${Messages.COLUMN_MESSAGE} TEXT," +
//            " ${Messages.COLUMN_LIST_IMAGES} TEXT )")
//    suspend fun createNewMessageTable(tableName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Delete
    suspend fun deleteChat(chat: Chat)

    @Query("SELECT * FROM $TABLE_CHAT")
    suspend fun getAllChats(): List<Chat>

}
//class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//    override fun onCreate(db: SQLiteDatabase) {
//
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        // This database is only a cache for online data, so its upgrade policy is
//        // to simply to discard the data and start over
////        db.execSQL(SQL_DELETE_ENTRIES)
////        onCreate(db)
//    }
//    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
////        onUpgrade(db, oldVersion, newVersion)
//    }
//    companion object {
//        // If you change the database schema, you must increment the database version.
//        const val DATABASE_VERSION = 1
//        const val DATABASE_NAME = "chat-db"
//    }
//}