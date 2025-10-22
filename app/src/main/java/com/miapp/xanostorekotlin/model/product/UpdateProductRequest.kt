package com.miapp.xanostorekotlin.model.product

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the JSON body for a PATCH request to update a product.
 * All fields are optional. When using Gson, fields with 'null' values will be omitted
 * from the JSON output, which is the desired behavior for a PATCH request.
 */
data class UpdateProductRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("stock")
    val stock: Int? = null,

    @SerializedName("brand")
    val brand: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("enabled")
    val enabled: Boolean? = null
)
