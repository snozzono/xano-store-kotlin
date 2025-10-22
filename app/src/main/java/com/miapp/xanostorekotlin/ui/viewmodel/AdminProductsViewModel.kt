package com.miapp.xanostorekotlin.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.eCommerce.ECommerceService
import com.miapp.xanostorekotlin.model.product.Product
import com.miapp.xanostorekotlin.model.product.UpdateProductRequest
import kotlinx.coroutines.launch

class AdminProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val eCommerceService: ECommerceService

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        val tokenManager = com.miapp.xanostorekotlin.api.auth.TokenManager(application)
        eCommerceService = RetrofitClient.createECommerceService(application)
    }

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val productList = eCommerceService.getProducts()
                _products.postValue(productList)
            } catch (e: Exception) {
                _errorMessage.postValue("Error al obtener productos: ${e.message}")
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                eCommerceService.deleteProduct(product.id)
                // Actualizar la lista localmente para reflejar el cambio
                val updatedList = _products.value?.filter { it.id != product.id }
                _products.postValue(updatedList)
            } catch (e: Exception) {
                _errorMessage.postValue("Error al eliminar producto: ${e.message}")
            }
        }
    }

    fun toggleProductStatus(product: Product) {
        viewModelScope.launch {
            try {
                // El nuevo estado es el opuesto al actual (si es nulo, se asume como activo por defecto)
                val newStatus = !(product.enabled ?: true)
                val request = UpdateProductRequest(enabled = newStatus)
                val updatedProduct = eCommerceService.updateProduct(product.id, request)

                // Actualizar el producto en la lista local
                val currentList = _products.value?.toMutableList()
                val index = currentList?.indexOfFirst { it.id == product.id }
                if (index != null && index != -1) {
                    currentList[index] = updatedProduct
                    _products.postValue(currentList)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error al cambiar estado: ${e.message}")
            }
        }
    }
}
