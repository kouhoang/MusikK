package com.kouhoang.music.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kouhoang.music.data.models.Photo
import com.kouhoang.music.databinding.ItemPhotoBinding

class PhotoViewPager2Adapter(
    private val photos: List<Photo>,
    private val context: Context
) : RecyclerView.Adapter<PhotoViewPager2Adapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]

        Glide.with(context).load(photo.imageUri).into(holder.binding.imgPhoto)
    }

    inner class PhotoViewHolder(val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root)
}