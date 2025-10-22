package com.miapp.xanostorekotlin.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductImage(
    @SerializedName("id")
    val id: Int,

    @SerializedName("product_id")
    val productId: Int,
    
    // Este campo ahora contiene el objeto anidado con la URL
    @SerializedName("image")
    val image: ImageDetails?
) : Parcelable
