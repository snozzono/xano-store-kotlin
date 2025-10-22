package com.miapp.xanostorekotlin.ui.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.databinding.ItemAdminProductBinding
import com.miapp.xanostorekotlin.model.product.Product

class AdminProductAdapter(
    private var products: List<Product>,
    private val listener: OnProductAdminListener
) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemAdminProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = "S/ ${product.price}"

            val imageUrl = product.image?.image?.url

            if (imageUrl != null) {
                binding.imageViewProduct.load(imageUrl) {
                    placeholder(R.drawable.ic_menu_gallery)
                    error(R.drawable.ic_menu_gallery)
                }
            } else {
                binding.imageViewProduct.setImageResource(R.drawable.ic_menu_gallery)
            }

            binding.switchProductStatus.isChecked = product.enabled ?: true
            binding.switchProductStatus.setOnCheckedChangeListener { _, isChecked ->
                listener.onProductToggleClicked(product)
            }

            itemView.setOnClickListener {
                listener.onProductClicked(product)
            }

            itemView.setOnLongClickListener {
                listener.onProductDeleteClicked(product)
                true
            }
        }
    }

    interface OnProductAdminListener {
        fun onProductDeleteClicked(product: Product)
        fun onProductToggleClicked(product: Product)
        fun onProductClicked(product: Product)
    }
}
