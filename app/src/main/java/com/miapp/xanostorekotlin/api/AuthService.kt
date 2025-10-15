package com.miapp.xanostorekotlin.api

import com.miapp.xanostorekotlin.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // ¡NUEVA FUNCIÓN! Añade esta función para el registro.
    @POST("auth/signup") // Asegúrate de que el endpoint sea el correcto ("signup", "register", etc.)
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("auth/me")
    suspend fun getMe(): User?
}
