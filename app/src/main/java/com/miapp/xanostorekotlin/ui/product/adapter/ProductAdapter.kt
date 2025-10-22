package com.miapp.xanostorekotlin.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.databinding.ItemProductBinding
import com.miapp.xanostorekotlin.model.product.*

class ProductAdapter(private var products: List<Product> = emptyList()) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = String.format("$%.2f", product.price)
            // Load the image if available
            product.image?.let { productImage ->
                binding.imageViewProduct.load(productImage.image?.url) {
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_menu_gallery)
                }
            } ?: run {
                binding.imageViewProduct.setImageResource(R.drawable.ic_menu_gallery)
            }
        }
    }
}
