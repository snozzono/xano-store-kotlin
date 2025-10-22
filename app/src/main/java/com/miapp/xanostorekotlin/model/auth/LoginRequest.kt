package com.miapp.xanostorekotlin.model.auth

/**
 * LoginRequest
 * Cuerpo del POST de login.
 */
data class LoginRequest(
    val email: String,
    val password: String
)