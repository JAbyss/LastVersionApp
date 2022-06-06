package com.foggyskies.petapp.domain.db

import android.provider.BaseColumns
import androidx.room.Database
import androidx.room.RoomDatabase
import com.foggyskies.petapp.MainActivity.Companion.IDUSER
import com.foggyskies.petapp.MainActivity.Companion.USERNAME
import com.foggyskies.petapp.data.Chat
import com.foggyskies.petapp.data.FriendTable
import com.foggyskies.petapp.domain.dao.ChatDao
import com.foggyskies.petapp.domain.dao.FriendsDao
import com.foggyskies.petapp.domain.db.Messages.COLUMN_ID
import com.foggyskies.petapp.presentation.ui.chat.entity.ChatMessageDC
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Messages : BaseColumns {
    const val TABLE_NAME = "message_"
    const val TABLE_NAME_NEW_MESSAGES = "new_messages_"
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

    fun createTableMessages(tableName: String) {
        val CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS ${Messages.TABLE_NAME + tableName} (" +
                    " ${Messages.COLUMN_ID} TEXT PRIMARY KEY," +
                    " ${Messages.COLUMN_AUTHOR} TEXT, ${Messages.COLUMN_DATE} TEXT," +
                    " ${Messages.COLUMN_MESSAGE} TEXT," +
                    " ${Messages.COLUMN_LIST_IMAGES} TEXT )"
        openHelper.writableDatabase.execSQL(CREATE_TABLE)
    }

    suspend fun insertMessages(idChat: String, message: ChatMessageDC) {
        val listS = Json.encodeToString(message.listImages)
        val INSERT_MESSAGE =
            "INSERT OR IGNORE INTO ${Messages.TABLE_NAME + idChat} VALUES (" +
                    " '${message.id}'," +
                    " '${message.idUser}'," +
                    " '${message.date}'," +
                    " '${message.message}'," +
                    " '$listS') "
        openHelper.writableDatabase.execSQL(INSERT_MESSAGE)
    }

    suspend fun deleteMessage(idChat: String, idMessage: String) {
        val DELETE_MESSAGE =
            "DELETE FROM ${Messages.TABLE_NAME + idChat} WHERE $COLUMN_ID LIKE '$idMessage'"
        openHelper.writableDatabase.execSQL(DELETE_MESSAGE)
    }

//    fun getOneMessage(idChat: String, idMessage: String): ChatMessageDC? {
//        val SELECT_MESSAGE =
//            "SELECT * FROM ${Messages.TABLE_NAME+idChat} WHERE id LIKE \'$idMessage\'"
//        val cursor = openHelper.readableDatabase.query(SELECT_MESSAGE)
//        var messageGlob: ChatMessageDC? = null
//        with(cursor){while(moveToNext()){
//            val message = ChatMessageDC(
//                id = cursor.getString(0),
//                idUser = cursor.getString(1),
//                date = cursor.getString(2),
//                message = cursor.getString(3),
//                listImages = Json.decodeFromString(cursor.getString(4))
//            )
//            messageGlob = message
//        } }
//        return messageGlob
//    }

    suspend fun getImageList(idChat: String, idMessage: String): List<String> {
        val SELECT_LIST_IMAGES =
            "SELECT listImages FROM message_$idChat WHERE id LIKE \'$idMessage\'"
        val cursor = openHelper.readableDatabase.query(SELECT_LIST_IMAGES)
        var stringList = ""
        with(cursor) {
            while (moveToNext()) {
                stringList = cursor.getString(0)
            }
        }
        val listImages = Json.decodeFromString<List<String>>(stringList)
        return listImages
    }

    suspend fun loadNextMessages(idChat: String, lastMessageID: String, callback: (ChatMessageDC) -> Unit){
        val SELECT_NEXT_MESSAGES =
            "SELECT * FROM ${Messages.TABLE_NAME + idChat} WHERE id  < '$lastMessageID' ORDER BY 1 DESC LIMIT 100"

        val cursor = openHelper.readableDatabase.query(SELECT_NEXT_MESSAGES)
//        val listMessages = mutableListOf<ChatMessageDC>()
        with(cursor) {

            while (moveToNext()) {
                val message = ChatMessageDC(
                    id = cursor.getString(0),
                    idUser = cursor.getString(1),
                    date = cursor.getString(2),
                    message = cursor.getString(3),
                    listImages = Json.decodeFromString(cursor.getString(4))
                )
                callback(message)
//                listMessages.add(message)
            }
        }
    }

    suspend fun loadFiftyMessages(idChat: String, nameChat: String): MutableList<ChatMessageDC> {
//        ORDER BY date ASC !!
        val SELECT_FIFTY_MESSAGES =
            "SELECT * FROM ${Messages.TABLE_NAME + idChat} ORDER BY 1 DESC LIMIT 100"
//        Limit 50
//        ORDER BY date ASC
//        TOP (50)
        val cursor = openHelper.readableDatabase.query(SELECT_FIFTY_MESSAGES)
        val listMessages = mutableListOf<ChatMessageDC>()
//        CoroutineScope(Dispatchers.Default).launch {
        with(cursor) {

            while (moveToNext()) {
                val message = ChatMessageDC(
                    id = cursor.getString(0),
                    idUser = cursor.getString(1),
                    date = cursor.getString(2),
                    message = cursor.getString(3),
                    listImages = Json.decodeFromString(cursor.getString(4)),
                    author = if (cursor.getString(1) == IDUSER) USERNAME else nameChat
                )
                listMessages.add(message)
            }
        }
//                while(moveToNext()){
//                    async {
//                        val message = ChatMessageDC(
//                            id = cursor.getString(0),
//                            idUser = cursor.getString(1),
//                            date = cursor.getString(2),
//                            message = cursor.getString(3),
//                            listImages = Json.decodeFromString(cursor.getString(4))
//                        )
//                        listMessages.add(message)
//                    }
//                } }
//        }.join()
        cursor.close()

        return listMessages
    }

    /**
     * New Messages
     */

    fun createTableNewMessage(idChat: String) {
        val CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS ${Messages.TABLE_NAME_NEW_MESSAGES + idChat} (" +
                    " ${Messages.COLUMN_ID} TEXT PRIMARY KEY," +
                    " ${Messages.COLUMN_AUTHOR} TEXT, ${Messages.COLUMN_DATE} TEXT," +
                    " ${Messages.COLUMN_MESSAGE} TEXT," +
                    " ${Messages.COLUMN_LIST_IMAGES} TEXT )"
        openHelper.writableDatabase.execSQL(CREATE_TABLE)
    }

    suspend fun insertNewMessage(idChat: String, message: ChatMessageDC) {
        val images = Json.encodeToString(message.listImages)
        val INSERT_MESSAGE =
            "INSERT OR IGNORE INTO ${Messages.TABLE_NAME_NEW_MESSAGES + idChat} VALUES (" +
                    " \"${message.id}\"," +
                    " \"${message.idUser}\"," +
                    " \"${message.date}\"," +
                    " \'${message.message}\'," +
                    " \'$images\') "
        openHelper.writableDatabase.execSQL(INSERT_MESSAGE)
    }

    suspend fun loadNewMessages(idChat: String): MutableList<ChatMessageDC> {
        val SELECT_NEW_MESSAGES =
            "SELECT * FROM ${Messages.TABLE_NAME_NEW_MESSAGES + idChat} ORDER BY date ASC "
        val cursor = openHelper.readableDatabase.query(SELECT_NEW_MESSAGES)
        val listNewMessages = mutableListOf<ChatMessageDC>()
        with(cursor) {
            while (moveToNext()) {
                val message = ChatMessageDC(
                    id = cursor.getString(0),
                    idUser = cursor.getString(1),
                    date = cursor.getString(2),
                    message = cursor.getString(3),
                    listImages = Json.decodeFromString(cursor.getString(4))
                )
                listNewMessages.add(message)
            }
        }
        cursor.close()

        return listNewMessages
    }

}