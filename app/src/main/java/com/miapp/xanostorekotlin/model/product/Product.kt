package com.miapp.xanostorekotlin.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("id")
    val id: Int,

    @SerializedName("created_at")
    val createdAt: Long?,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("price")
    val price: Double?,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("enabled")
    val enabled: Boolean?,

    @SerializedName("_product_image_of_product")
    val image: ProductImage?
) : Parcelable
