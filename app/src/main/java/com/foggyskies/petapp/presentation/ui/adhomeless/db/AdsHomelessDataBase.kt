package com.foggyskies.petapp.presentation.ui.adhomeless.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.foggyskies.petapp.presentation.ui.adhomeless.dao.AdsHomelessDao
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity

@Database(
    entities = [AdHomelessEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AdsHomelessDataBase: RoomDatabase() {

    abstract fun adsHomelessDao(): AdsHomelessDao

}