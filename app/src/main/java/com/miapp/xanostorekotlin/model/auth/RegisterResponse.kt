package com.miapp.xanostorekotlin.model.auth

// Este modelo puede variar. Si tu API devuelve el usuario creado, puedes reutilizar UserProfile.
// Si solo devuelve un mensaje, podría ser algo así. Por simplicidad, asumimos que devuelve el token.
data class RegisterResponse(
    val authToken: String
)
