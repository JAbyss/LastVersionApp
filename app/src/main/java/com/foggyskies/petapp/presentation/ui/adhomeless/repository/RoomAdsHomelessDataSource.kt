//package com.foggyskies.data.vehicle.room
//
//import com.foggyskies.petapp.presentation.ui.adhomeless.dao.AdsHomelessDao
//import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity
//import com.google.gson.Gson
//
//class RoomAdsHomelessDataSource(
//    private val gson: Gson,
//    private val adsHomelessDao: AdsHomelessDao
//) : AdsHomelessLocalDataSource {
//    override suspend fun loadAllAds(): List<AdHomelessEntity> = adsHomelessDao.loadAllAds()
//
//    override suspend fun isExist(idAd: Int): AdHomelessEntity = adsHomelessDao.isExist(idAd)
//
//    override suspend fun saveRemoteResponse(response: List<AdHomelessEntity>) {
//        adsHomelessDao.saveAllAds(entities = response
////        response.map {
////            VehicleEntity(
////                idVehicle = it.idVehicle,
////                image = it.image,
////                name = it.name,
////                engPower = it.engPower,
////                rentCost = it.rentCost,
////                location = it.location,
////                rating = it.rating,
////                images = gson.toJson(it.images),
////                addresses = gson.toJson(it.addresses)
////            )
////        }
//        )
//    }
//
//    override suspend fun addAd(item: AdHomelessEntity)  = adsHomelessDao.addAd(item)
//
////    override suspend fun addAd(vehicleEntity: VehicleListResponse) = vehicleListDao.addVehicle(
////        vehicleEntity = VehicleEntity(
////            idVehicle = vehicleEntity.idVehicle,
////            image = vehicleEntity.image,
////            name = vehicleEntity.name,
////            engPower = vehicleEntity.engPower,
////            rentCost = vehicleEntity.rentCost,
////            location = vehicleEntity.location,
////            rating = vehicleEntity.rating,
////            images = gson.toJson(vehicleEntity.images),
////            addresses = gson.toJson(vehicleEntity.addresses)
////        )
////    )
//
//    override suspend fun loadOneAd(idAd: Int): AdHomelessEntity {
//
//        return adsHomelessDao.loadOneAd(
//            idAd = idAd
//        )
//    }
//}
