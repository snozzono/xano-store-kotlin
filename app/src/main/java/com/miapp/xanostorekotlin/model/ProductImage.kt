// Define el paquete donde se encuentra este archivo de modelo de datos.
package com.miapp.xanostorekotlin.model

// Importamos la anotación @SerializedName de la librería GSON.
// Esta anotación nos permite mapear un nombre de campo del JSON a un nombre de variable en Kotlin,
// incluso si no coinciden exactamente. Es una excelente práctica para evitar problemas.
import com.google.gson.annotations.SerializedName

/**
 * ProductImage: Representa la estructura completa de un objeto de imagen tal como lo devuelve y espera la API de Xano.
 *
 * Este modelo se usa en dos lugares:
 * 1. Para recibir la respuesta de la API de subida (`/upload/image`).
 * 2. Para enviar los datos de la imagen a la API de creación de productos (`/product`).
 */
data class ProductImage(
    // El @SerializedName("path") le dice a GSON: "Cuando veas la clave 'path' en el JSON,
    // guarda su valor en esta variable 'path'".
    @SerializedName("path")
    val path: String,

    // Mapeamos la clave 'name' del JSON a nuestra variable 'name'.
    @SerializedName("name")
    val name: String?,

    // Mapeamos la clave 'type' del JSON.
    @SerializedName("type")
    val type: String?,

    // Mapeamos la clave 'size' del JSON.
    @SerializedName("size")
    val size: Int?,

    // Mapeamos la clave 'mime' del JSON.
    @SerializedName("mime")
    val mime: String?,

    // Mapeamos la clave 'access' del JSON.
    @SerializedName("access")
    val access: String?,

    // Mapeamos la clave 'url' del JSON, que puede ser nula.
    @SerializedName("url")
    val url: String?,

    // 'meta' es un objeto anidado dentro del JSON. También necesita su propio data class.
    @SerializedName("meta")
    val meta: ImageMeta?
) : java.io.Serializable // Implementa Serializable para poder pasar este objeto entre fragmentos/actividades si es necesario.

/**
 * ImageMeta: Representa el objeto anidado 'meta' que contiene el ancho y alto de la imagen.
 */
data class ImageMeta(
    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?
) : java.io.Serializable // También es buena idea hacerlo serializable.
