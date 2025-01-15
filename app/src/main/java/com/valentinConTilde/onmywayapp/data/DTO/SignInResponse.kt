package com.valentinConTilde.onmywayapp.data.DTO

import com.valentinConTilde.onmywayapp.data.models.User

data class SignInResponse(
    val status: String,
    val user: User
)
