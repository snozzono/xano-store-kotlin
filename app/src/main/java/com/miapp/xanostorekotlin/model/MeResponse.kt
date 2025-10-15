package com.miapp.xanostorekotlin.model

// Modelo que representa la respuesta completa del endpoint /auth/me
// Contiene el objeto de usuario anidado.
data class MeResponse(
    val user: User? // El nombre "user" debe coincidir con la clave en el JSON de la API
)
