//package com.foggyskies.petapp.presentation.ui.adhomeless.repository
//
//import android.util.Log
//import com.foggyskies.data.vehicle.room.AdsHomelessLocalDataSource
//import com.foggyskies.data.vehicle.room.AdsHomelessRemoteDataSource
//import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity
//import java.lang.Exception
//
//class AdsHomelessRepository(
//    private val adsHomelessLocalDataSource: AdsHomelessLocalDataSource,
//    private val adsHomelessRemoteDataSource: AdsHomelessRemoteDataSource
//) {
//    suspend fun fetchAdsList(): List<AdHomelessEntity> {
//
//
//        val response = adsHomelessRemoteDataSource.getAllAds()
//
//        return if (response.isNullOrEmpty()) {
//            getAdsFromDB()
//        } else {
//
//            response.forEach { item ->
//                val a = adsHomelessLocalDataSource.isExist(item.idAd)
//
//                if (a == null) {
//                    adsHomelessLocalDataSource.addAd(item)
//                }
//            }
//
//            response
//        }
//    }
//
//    suspend fun getAdsFromDB(): List<AdHomelessEntity> {
//
//        return adsHomelessLocalDataSource.loadAllAds()
//    }
//}