//package com.foggyskies.petapp.presentation.ui.adhomeless.di
//
//import com.foggyskies.data.vehicle.room.AdsHomelessRemoteDataSource
//import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.engine.android.*
//import io.ktor.client.plugins.*
//import io.ktor.client.request.*
//
//class RequestModule : AdsHomelessRemoteDataSource {
//
//    override suspend fun getAllAds(): List<AdHomelessEntity> {
//        HttpClient(Android) {
////            install(JsonFeature) {
////                serializer = KotlinxSerializer()
////            }
////            install(ContentNegotiation){
////                json(Json {
////                    prettyPrint = true
////                    isLenient = true
////                })
////            }
//            install(HttpTimeout){
//                requestTimeoutMillis = 3000
//            }
//        }.use {
//            try {
//                return it.get("http://192.168.0.88:8080/ads/homeless").body()
//            } catch (e: Exception) {
//                return emptyList()
//            }
//        }
//    }
//}