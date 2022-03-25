package com.foggyskies.data.vehicle.room

import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity

interface AdsHomelessLocalDataSource {

    suspend fun loadAllAds(): List<AdHomelessEntity>

    suspend fun isExist(idAd: Int): AdHomelessEntity

    suspend fun saveRemoteResponse(response: List<AdHomelessEntity>)

    suspend fun addAd(item: AdHomelessEntity)

    suspend fun loadOneAd(idAd: Int): AdHomelessEntity
}