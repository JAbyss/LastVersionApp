//package com.foggyskies.petapp.presentation.ui.adhomeless.entity
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.TypeConverter
//import androidx.room.TypeConverters
//import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity.Companion.TABLE_NAME
//import kotlinx.serialization.Serializable
//
//@Entity(tableName = TABLE_NAME)
//@Serializable
//data class AdHomelessEntity(
//    @PrimaryKey()
//    val idAd: Int,
//    val name: String,
//    @field:TypeConverters(ImageListConvertor::class)
//    var image: List<String> = emptyList(),
//    val breed: String,
//    val gender: String,
//    val neuter: Boolean,
//    val old: String,
//    val previewLabel: String,
//    val description: String,
//    @field:TypeConverters(AuthorDCConvertor::class)
//    val author: AuthorMessageEntity
//) {
//    companion object{
//        const val TABLE_NAME = "ads_homeless_entity_table"
//    }
//}
//
//class ImageListConvertor {
//    private val gson = Gson()
//    @TypeConverter
//    fun stringToList(data: String?): List<String> {
//        if (data == null) {
//            return emptyList()
//        }
//
////        val listType = object : TypeToken<List<String>>() {
////
////        }.type
//
//        return gson.fromJson<List<String>>(data, List::class.java)
//    }
//
//    @TypeConverter
//    fun listToString(objects: List<String>): String {
//        return gson.toJson(objects)
//    }
//}
//
//class AuthorDCConvertor {
//    private val gson = Gson()
//    @TypeConverter
//    fun stringToDC(data: String?): AuthorMessageEntity {
//        if (data == null) {
//            return AuthorMessageEntity(
//                idUser = 0,
//                nameUser = "",
//                image = ""
//            )
//        }
//
////        val listType = object : TypeToken<AuthorMessageEntity>() {
////
////        }.type
//
//        return gson.fromJson(data, AuthorMessageEntity::class.java)
//    }
//
//    @TypeConverter
//    fun listToString(objects: AuthorMessageEntity): String {
//        return gson.toJson(objects)
//    }
//}
//
////@Entity(tableName = TABLE_NAME)
////data class VehicleEntity(
////    @PrimaryKey()
////    @ColumnInfo(name = "idVehicle")
////    var idVehicle: Int,
////    @ColumnInfo(name = "previewImage")
////    var image: String,
////    @ColumnInfo(name = "vehicleName")
////    var name: String,
////    @ColumnInfo(name = "engPower")
////    var engPower: String,
////    @ColumnInfo(name = "rentCost")
////    var rentCost: Int,
////    @ColumnInfo(name = "location")
////    var location: String,
////    @ColumnInfo(name = "rating")
////    var rating: Int,
////    @ColumnInfo(name = "images")
////    var images: String,
////    @ColumnInfo(name = "addresses")
////    var addresses: String
////) {
////    companion object{
////        const val TABLE_NAME = "vehicle_entity_table"
////    }
////}