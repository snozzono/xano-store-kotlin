package com.miapp.xanostorekotlin.model

import com.google.gson.annotations.SerializedName

/**
 * User
 * Modelo básico de usuario según lo esperado por la API de Xano.
 * Ajusta los campos si tu API devuelve otros nombres.
 */
data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    // El campo "created_at" también se puede incluir si lo necesitas
    @SerializedName("created_at")
    val createdAt: Long
)