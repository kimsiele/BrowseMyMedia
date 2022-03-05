package com.sielee.browsemymedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.databinding.PhotoItemBinding

class PhotosAdapter:ListAdapter<PhotoModel,PhotosAdapter.PhotosViewHolder>(DiffUtilItemCallback()) {
    class PhotosViewHolder(private val binding: PhotoItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(photoModel: PhotoModel?) {
            Glide.with(binding.root.context).load(photoModel?.path).into(binding.ivPhotoItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
       return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DiffUtilItemCallback:DiffUtil.ItemCallback<PhotoModel>() {
    override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
        return oldItem==newItem
    }

}
