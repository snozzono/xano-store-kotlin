package com.miapp.xanostorekotlin.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.eCommerce.ECommerceService
import com.miapp.xanostorekotlin.model.product.Product
import kotlinx.coroutines.launch

class ProductViewModel(private val eCommerceService: ECommerceService) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val productList = eCommerceService.getProducts()
                _products.postValue(productList)
            } catch (e: Exception) {
                _error.postValue("Error fetching products: ${e.message}")
            }
        }
    }
}

class ProductViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            val eCommerceService = RetrofitClient.createECommerceService(context)
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(eCommerceService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
