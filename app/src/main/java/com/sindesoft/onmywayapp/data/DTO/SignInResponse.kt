package com.sindesoft.onmywayapp.data.DTO

import com.sindesoft.onmywayapp.data.models.User

data class SignInResponse(
    val status: String,
    val user: User
)
