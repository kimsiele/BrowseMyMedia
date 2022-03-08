package com.sielee.browsemymedia.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.databinding.PhotoItemBinding

class PhotosAdapter(private val photoListener:PhotoClickListener, private val context: Context):ListAdapter<PhotoModel,PhotosAdapter.PhotosViewHolder>(DiffUtilItemCallback()) {
    class PhotosViewHolder(private val binding: PhotoItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(photoModel: PhotoModel?,photoClickListener: PhotoClickListener,position: Int,context:Context) {
            binding.apply {
                Glide.with(context).load(photoModel?.path).into(ivPhotoItem)
                root.setOnClickListener {
                    photoClickListener.onClick(photoModel!!,position)
                }
            }

        }

    }

    class PhotoClickListener(val clickListener:(photoModel: PhotoModel,position:Int)->Unit) {
        fun onClick(photoModel: PhotoModel, position: Int) = clickListener(photoModel, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
       return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(getItem(position),photoListener,position,context)
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
