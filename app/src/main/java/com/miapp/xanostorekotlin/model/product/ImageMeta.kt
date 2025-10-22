package com.miapp.xanostorekotlin.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * ImageMeta: Representa el objeto anidado 'meta' que contiene el ancho y alto de la imagen.
 */
@Parcelize
data class ImageMeta(
    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?
) : Parcelable
