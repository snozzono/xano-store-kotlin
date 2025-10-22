package com.miapp.xanostorekotlin.model.shared

// Modelo para deserializar el JSON de error que devuelve Xano.
// Ejemplo: { "message": "El usuario con este email ya existe." }
data class ApiError(
    val message: String?
)