package com.miapp.xanostorekotlin.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.eCommerce.ECommerceService
import com.miapp.xanostorekotlin.model.product.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class AddProductViewModel(application: Application) : AndroidViewModel(application) {

    private val eCommerceService: ECommerceService

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    private val _productLoaded = MutableLiveData<Product?>()
    val productLoaded: LiveData<Product?> = _productLoaded

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        eCommerceService = RetrofitClient.createECommerceService(application)
    }

    fun loadProduct(productId: Int) {
        if (productId == -1) {
            _productLoaded.postValue(null)
            return
        }
        viewModelScope.launch {
            try {
                val product = eCommerceService.getProductById(productId)
                _productLoaded.postValue(product)
            } catch (e: Exception) {
                _errorMessage.postValue("Error al cargar el producto: ${e.message}")
            }
        }
    }

    fun saveOrUpdateProduct(
        productToEdit: Product?,
        name: String,
        description: String,
        price: Double,
        stock: Int,
        brand: String,
        category: String,
        images: List<Any>
    ) {
        viewModelScope.launch {
            try {
                if (productToEdit == null) {
                    val createRequest = CreateProductRequest(name, description, price, stock, brand, category)
                    val newProduct = eCommerceService.createProduct(createRequest)

                    val newImages = images.filterIsInstance<Uri>()
                    uploadAndAssociateImages(newImages, newProduct.id)

                } else {
                    val updateRequest = UpdateProductRequest(name, description, price, stock, brand, category)
                    eCommerceService.updateProduct(productToEdit.id, updateRequest)

                    val newImages = images.filterIsInstance<Uri>()
                    uploadAndAssociateImages(newImages, productToEdit.id)
                    
                    val existingImageIds = images.filterIsInstance<ProductImage>().map { it.id }.toSet()
                    val originalImageIds = productToEdit.image?.let { setOf(it.id) } ?: emptySet()
                    val idsToDelete = originalImageIds - existingImageIds
                }

                _operationSuccess.postValue(true)
            } catch (e: Exception) {
                _operationSuccess.postValue(false)
                _errorMessage.postValue("Error en la operación: ${e.message}")
            }
        }
    }
    
    private suspend fun uploadAndAssociateImages(imageUris: List<Uri>, productId: Int) {
        for (uri in imageUris) {
            val uploadedImage = uploadSingleImage(uri)
            
            // --- VALIDACIÓN AÑADIDA ---
            // Solo asociar si la imagen se subió correctamente Y tiene un path válido.
            if (uploadedImage != null && uploadedImage.image?.path?.isNotBlank() == true) {
                val associateRequest = AssociateImageRequest(productId = productId, imageId = uploadedImage.id)
                eCommerceService.associateImageToProduct(associateRequest)
            } else {
                // Opcional: Registrar un error si la subida falló silenciosamente.
                // Log.e("AddProductViewModel", "Failed to upload image or received invalid path for URI: $uri")
            }
        }
    }

    private suspend fun uploadSingleImage(uri: Uri): ProductImage? {
        return try {
            getApplication<Application>().contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("content", "image.jpg", requestBody)
                eCommerceService.uploadImage(part)
            }
        } catch (e: Exception) {
            // Log.e("AddProductViewModel", "Exception during image upload: ${e.message}")
            null // Asegurarse de que cualquier excepción resulte en un nulo
        }
    }
}
