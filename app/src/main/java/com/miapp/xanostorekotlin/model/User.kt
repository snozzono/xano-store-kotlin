package com.miapp.xanostorekotlin.model

import com.google.gson.annotations.SerializedName

/**
 * User
 * Modelo básico de usuario según lo esperado por la API de Xano.
 * Ajusta los campos si tu API devuelve otros nombres.
 */
data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String
)