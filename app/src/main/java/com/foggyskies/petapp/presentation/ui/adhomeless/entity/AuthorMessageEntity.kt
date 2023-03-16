package com.foggyskies.petapp.presentation.ui.adhomeless.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
data class AuthorMessageEntity(
    val idUser: Int,
    val nameUser: String,
    val image: String
)