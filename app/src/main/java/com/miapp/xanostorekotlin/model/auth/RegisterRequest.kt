package com.miapp.xanostorekotlin.model.auth

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String // Asegúrate de que coincida con el nombre del campo en tu API de Xano ("pass", "password", etc.)
)
