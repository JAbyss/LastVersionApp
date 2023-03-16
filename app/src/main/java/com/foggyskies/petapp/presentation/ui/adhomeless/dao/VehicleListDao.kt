//package com.foggyskies.petapp.presentation.ui.adhomeless.dao
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity
//
//@Dao
//interface AdsHomelessDao {
//
//    @Query("SELECT * FROM ${AdHomelessEntity.TABLE_NAME}")
//    suspend fun loadAllAds(): List<AdHomelessEntity>
////
//    @Query("SELECT * FROM ${AdHomelessEntity.TABLE_NAME} WHERE :idAd like idAd")
//    suspend fun isExist(idAd: Int): AdHomelessEntity
////
//    @Insert(entity = AdHomelessEntity::class, onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addAd(adHomelessEntity: AdHomelessEntity)
////
//    @Insert
//    @JvmSuppressWildcards
//    suspend fun saveAllAds(entities: List<AdHomelessEntity>)
////
//    @Query("SELECT * FROM ${AdHomelessEntity.TABLE_NAME} WHERE :idAd like idAd")
//    suspend fun loadOneAd(idAd: Int): AdHomelessEntity
//
//}
