package com.miapp.xanostorekotlin.ui.admin.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miapp.xanostorekotlin.databinding.ItemImagePreviewBinding
import com.miapp.xanostorekotlin.model.product.ProductImage

class ImagePreviewAdapter(
    private val images: MutableList<Any>,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ImagePreviewAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = images[position]
        holder.bind(item)
    }

    override fun getItemCount() = images.size

    inner class ImageViewHolder(private val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonDeleteImage.setOnClickListener {
                // Ensure the position is valid before acting on it
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(adapterPosition)
                }
            }
        }

        fun bind(item: Any) {
            when (item) {
                is Uri -> {
                    // Cargar desde una Uri local (imagen nueva)
                    binding.imageViewPreview.load(item)
                }
                is ProductImage -> {
                    // Cargar desde una URL (imagen existente)
                    binding.imageViewPreview.load(item.image?.url) // <-- CORREGIDO
                }
            }
        }
    }
}
