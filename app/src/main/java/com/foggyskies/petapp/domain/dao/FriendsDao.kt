package com.foggyskies.petapp.domain.dao

import androidx.room.*
import com.foggyskies.petapp.data.FriendTable
import com.foggyskies.petapp.data.FriendTable.Companion.TABLE_FRIEND

@Dao
interface FriendsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(friend: FriendTable)

    @Query("SELECT * FROM $TABLE_FRIEND ")
    suspend fun getFriends(): List<FriendTable>

    @Delete
    suspend fun deleteOne(friend: FriendTable)
}