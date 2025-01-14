package com.valentinConTilde.onmywayapp.data.models

data class UserLocation(
    val userId: String? = "",
    val latitude: String,
    val longitude: String,
    val date: String,
    val speed: String,
    val persistence : Boolean,
    val batteryPercentage: Float? = null,
    val applicationVersion: String,
    val locationAccuracy: String
)
