package com.foggyskies.petapp.presentation.ui.adhomeless.entity

enum class GenderPet{
    MALE, FEMALE
}

data class AdHomelessPetEntity(
    var image: String,
    var name: String? = null,
    var breed: String,
    var description: String? = null,
    var age: String,
    var gender: GenderPet,
    var isNeuter: Boolean? = null
)