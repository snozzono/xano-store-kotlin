package com.miapp.xanostorekotlin.model.product

import com.google.gson.annotations.SerializedName

/**
 * Data class for associating an uploaded image with a product.
 */
data class AssociateImageRequest(
    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("image_id")
    val imageId: Int
)
