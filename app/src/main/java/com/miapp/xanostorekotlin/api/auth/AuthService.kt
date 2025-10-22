package com.miapp.xanostorekotlin.api.auth

import com.miapp.xanostorekotlin.model.auth.AuthResponse
import com.miapp.xanostorekotlin.model.auth.LoginRequest
import com.miapp.xanostorekotlin.model.auth.RegisterRequest
import com.miapp.xanostorekotlin.model.auth.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("auth/me")
    suspend fun getMe(): User?
}