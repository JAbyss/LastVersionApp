package com.foggyskies.petapp.extendfun

public fun Float.inRange(minimumValue: Float, maximumValue: Float): Boolean {
    if (minimumValue > maximumValue) throw IllegalArgumentException("Cannot coerce value to an empty range: maximum $maximumValue is less than minimum $minimumValue.")
    if (this > minimumValue)
        if (this < maximumValue)
            return true
    return false
}