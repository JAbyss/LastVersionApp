package com.foggyskies.petapp.domain.db

import android.provider.BaseColumns
import androidx.room.Database
import androidx.room.RoomDatabase
import com.foggyskies.petapp.data.Chat
import com.foggyskies.petapp.data.FriendTable
import com.foggyskies.petapp.domain.dao.ChatDao
import com.foggyskies.petapp.domain.dao.FriendsDao
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Messages : BaseColumns {
    const val TABLE_NAME = "message_"
    const val COLUMN_ID = "id"
    const val COLUMN_AUTHOR = "author"
    const val COLUMN_DATE = "date"
    const val COLUMN_MESSAGE = "message"
    const val COLUMN_LIST_IMAGES = "listImages"
}

@Database(entities = [Chat::class, FriendTable::class], version = 2, exportSchema = true)
abstract class UserDB : RoomDatabase() {

    abstract fun chatDao(): ChatDao

    abstract fun friendDao(): FriendsDao

    fun createTable(tableName: String) {
        val CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS ${Messages.TABLE_NAME + tableName} (" +
                    " ${Messages.COLUMN_ID} TEXT PRIMARY KEY," +
                    " ${Messages.COLUMN_AUTHOR} TEXT, ${Messages.COLUMN_DATE} TEXT," +
                    " ${Messages.COLUMN_MESSAGE} TEXT," +
                    " ${Messages.COLUMN_LIST_IMAGES} TEXT )"
        openHelper.writableDatabase.execSQL(CREATE_TABLE)
    }

    suspend fun insertMessages(idChat: String, message: ChatMessage) {
        val listS = Json.encodeToString(message.listImages)
        val INSERT_MESSAGE =
            "INSERT OR REPLACE INTO ${Messages.TABLE_NAME + idChat} VALUES (" +
                    " \"${message.id}\"," +
                    " \"${message.author}\"," +
                    " \"${message.date}\"," +
                    " \'${message.message}\'," +
                    " \'$listS\') "
        openHelper.writableDatabase.execSQL(INSERT_MESSAGE)
    }

    fun getOneMessage(idChat: String, idMessage: String): ChatMessage? {
        val SELECT_MESSAGE =
            "SELECT * FROM ${Messages.TABLE_NAME+idChat} WHERE id LIKE \'$idMessage\'"
        val cursor = openHelper.readableDatabase.query(SELECT_MESSAGE)
        var messageGlob: ChatMessage? = null
        with(cursor){while(moveToNext()){
            val message = ChatMessage(
                id = cursor.getString(0),
                author = cursor.getString(1),
                date = cursor.getString(2),
                message = cursor.getString(3),
                listImages = Json.decodeFromString(cursor.getString(4))
            )
            messageGlob = message
        } }
        return messageGlob
    }

    suspend fun getImageList(idChat: String, idMessage: String): List<String> {
        val SELECT_LIST_IMAGES =
            "SELECT listImages FROM message_$idChat WHERE id LIKE \'$idMessage\'"
        val cursor = openHelper.readableDatabase.query(SELECT_LIST_IMAGES)
        var stringList = ""
        with(cursor){while(moveToNext()){
            stringList = cursor.getString(0)
        } }
        val listImages = Json.decodeFromString<List<String>>(stringList)
        return listImages
    }

    suspend fun loadFiftyMessages(idChat: String): MutableList<ChatMessage> {
        val SELECT_FIFTY_MESSAGES =
            "SELECT * FROM ${Messages.TABLE_NAME + idChat} ORDER BY date ASC "
//        Limit 50
//        ORDER BY date ASC
//        TOP (50)
        val cursor = openHelper.readableDatabase.query(SELECT_FIFTY_MESSAGES)
          val listMessages = mutableListOf<ChatMessage>()
        with(cursor){while(moveToNext()){
            val message = ChatMessage(
                id = cursor.getString(0),
                author = cursor.getString(1),
                date = cursor.getString(2),
                message = cursor.getString(3),
                listImages = Json.decodeFromString(cursor.getString(4))
            )
            listMessages.add(message)
        } }
        cursor.close()

        return listMessages
    }
}