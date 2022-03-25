package com.foggyskies.petapp.presentation.ui.adhomeless

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.foggyskies.data.vehicle.room.AdsHomelessLocalDataSource
import com.foggyskies.data.vehicle.room.AdsHomelessRemoteDataSource
import com.foggyskies.data.vehicle.room.RoomAdsHomelessDataSource
import com.foggyskies.petapp.presentation.ui.adhomeless.db.AdsHomelessDataBase
import com.foggyskies.petapp.presentation.ui.adhomeless.di.RequestModule
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessEntity
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AdHomelessPetEntity
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.AuthorMessageEntity
import com.foggyskies.petapp.presentation.ui.adhomeless.entity.GenderPet
import com.foggyskies.petapp.presentation.ui.adhomeless.repository.AdsHomelessRepository
import com.foggyskies.petapp.presentation.ui.home.entity.CircularSelector
import com.foggyskies.petapp.presentation.ui.home.entity.SwipableMenu
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch

class AdsHomelessViewModel(application: Application) : AndroidViewModel(application) {

    var petAdsList by mutableStateOf(emptyList<AdHomelessEntity>())

    val swipableMenu = SwipableMenu()

    val circularSelector = CircularSelector()

    init {

        val roomAds = Room.databaseBuilder(
            application,
            AdsHomelessDataBase::class.java,
            "vehicle_room_database"
        ).build()

        val adsHomelessLocalDataSource: AdsHomelessLocalDataSource =
            RoomAdsHomelessDataSource(Gson(), roomAds.adsHomelessDao())

        val adsHomelessRemoteDataSource: AdsHomelessRemoteDataSource = RequestModule()

        var repository = AdsHomelessRepository(
            adsHomelessLocalDataSource = adsHomelessLocalDataSource,
            adsHomelessRemoteDataSource = adsHomelessRemoteDataSource
        )
        viewModelScope.launch {
            petAdsList = repository.fetchAdsList()
        }
    }

    var isFullSizeImage by mutableStateOf(false)

    var selectedImage by mutableStateOf<ImagePainter?>(null)

    enum class StatePhotoSize{
        OPEN, CLOSE
    }

    fun changePhotoSize(imagePainter: ImagePainter? = null, state: StatePhotoSize){
        when(state){
            StatePhotoSize.OPEN -> {
                isFullSizeImage = true
                selectedImage = imagePainter
            }
            StatePhotoSize.CLOSE -> {
                isFullSizeImage = false
                selectedImage = null
            }
        }
    }

    var isExpandAd by mutableStateOf(false)

    var petAd by mutableStateOf<AdHomelessEntity>(
        AdHomelessEntity(
            idAd = 0,
            name = "",
            image = listOf(),
            breed = "",
            gender = "",
            neuter = false,
            old = "",
            previewLabel = "",
            description = "",
            author = AuthorMessageEntity(
                idUser = 0,
                nameUser = "",
                image = ""
            )
        )
    )
}