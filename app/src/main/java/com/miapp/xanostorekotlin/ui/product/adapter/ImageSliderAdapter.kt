package com.miapp.xanostorekotlin.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miapp.xanostorekotlin.databinding.ItemImageSliderBinding

class ImageSliderAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        holder.binding.imageView.load(imageUrl) {
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_dialog_alert)
        }
    }

    override fun getItemCount(): Int = images.size
}