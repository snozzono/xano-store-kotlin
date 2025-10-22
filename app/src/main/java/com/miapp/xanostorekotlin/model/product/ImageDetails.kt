package com.miapp.xanostorekotlin.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageDetails(
    @SerializedName("path")
    val path: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("size")
    val size: Int?,

    @SerializedName("mime")
    val mime: String?,

    @SerializedName("access")
    val access: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("meta")
    val meta: ImageMeta?
) : Parcelable
