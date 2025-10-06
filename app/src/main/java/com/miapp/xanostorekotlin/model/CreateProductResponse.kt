package com.miapp.xanostorekotlin.model

/**
 * CreateProductResponse
 * Respuesta del POST de creación. Puede devolver el producto creado
 * o un objeto con éxito. Aquí asumimos que devuelve el producto.
 */
data class CreateProductResponse(
    val product: Product?
)