package com.miapp.xanostorekotlin.model.product

import com.google.gson.annotations.SerializedName

/**
 * Data class for creating a new product.
 * Images are associated in a separate step after the product is created.
 */
data class CreateProductRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("category")
    val category: String
)
