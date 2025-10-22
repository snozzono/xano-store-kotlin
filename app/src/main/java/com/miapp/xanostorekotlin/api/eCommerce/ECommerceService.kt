package com.miapp.xanostorekotlin.api.eCommerce

import com.miapp.xanostorekotlin.model.product.AssociateImageRequest
import com.miapp.xanostorekotlin.model.product.CreateProductRequest
import com.miapp.xanostorekotlin.model.product.Product
import com.miapp.xanostorekotlin.model.product.ProductImage
import com.miapp.xanostorekotlin.model.product.UpdateProductRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ECommerceService {
    @GET("product")
    suspend fun getProducts(): List<Product>

    @GET("product/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Product

    @POST("product")
    suspend fun createProduct(@Body request: CreateProductRequest): Product

    @PATCH("product/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Int,
        @Body product: UpdateProductRequest
    ): Product

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") productId: Int)

    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): ProductImage

    // Endpoint para asociar una imagen a un producto
    @POST("product_image") // Asume que este es tu endpoint de la tabla de unión
    suspend fun associateImageToProduct(@Body request: AssociateImageRequest)
}
