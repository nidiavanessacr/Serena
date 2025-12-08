package com.vane.serena.network

data class LoginResponse(
    val login: Boolean,
    val mensaje: String,
    val user_id: Int?
)
