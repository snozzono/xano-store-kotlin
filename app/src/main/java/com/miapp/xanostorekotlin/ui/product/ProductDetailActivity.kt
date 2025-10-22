package com.miapp.xanostorekotlin.ui.product

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.miapp.xanostorekotlin.databinding.ActivityProductDetailBinding
import com.miapp.xanostorekotlin.model.product.Product
import com.miapp.xanostorekotlin.ui.product.adapter.ImageSliderAdapter

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        @Suppress("DEPRECATION")
        val product: Product? = intent.getParcelableExtra("PRODUCT_EXTRA")


        product?.let {
            setupUI(it)
        }
    }

    private fun setupUI(product: Product) {
        title = product.name

        binding.tvProductName.text = "Nombre: ${product.name}"
        binding.tvProductPrice.text = product.price?.let { "Precio: $it" } ?: "Precio no disponible"
        binding.tvProductDescription.text = "Descripción: ${product.description ?: "Sin descripción."}"
        binding.tvProductStock.text = "Stock: ${product.stock ?: "No especificado"}"

        product.image?.image?.url?.let { imageUrl ->
            val imageUrls = listOf(imageUrl)
            if (imageUrls.isNotEmpty()) {
                val adapter = ImageSliderAdapter(imageUrls)
                binding.imageViewPager.adapter = adapter
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
