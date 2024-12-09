package com.sindesoft.onmywayapp.data.models

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("_id")
    val id: String? = "",
    val googleId: String, // From `sub`
    val email: String,
    val emailVerified: Boolean,
    val givenName: String,
    val familyName: String,
    val profilePicture: String? = null,
)
