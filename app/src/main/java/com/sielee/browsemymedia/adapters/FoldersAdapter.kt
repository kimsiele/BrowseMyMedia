package com.sielee.browsemymedia.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sielee.browsemymedia.data.model.FolderModel
import com.sielee.browsemymedia.databinding.FolderItemBinding

const val TAG = "FolderAdapter"

class FoldersAdapter(private val clickListener:OnItemClick,private val context: Context) :
    ListAdapter<FolderModel, FoldersAdapter.FolderViewHolder>(DiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener, context)
    }

    class FolderViewHolder(private val binding: FolderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folderModel: FolderModel?,itemClick: OnItemClick, context: Context) {
            Log.d(TAG, "FistImage: ${folderModel?.photoFirst}")
            binding.apply {
                Glide
                    .with(context)
                    .load(folderModel?.photoFirst)
                    .into(ivFolderItem)
                tvFolderName.text = folderModel?.folderName
                tvPhotosCount.text = folderModel?.photosCount.toString()

                root.setOnClickListener {
                    itemClick.onClick(folderModel!!)
                }
            }
        }
    }

    class DiffItemCallback : DiffUtil.ItemCallback<FolderModel>() {
        override fun areItemsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean {
            return oldItem.folderName == newItem.folderName
        }

        override fun areContentsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean {
            return oldItem == newItem
        }

    }
    class OnItemClick(val clickListener:(folderModel:FolderModel)->Unit){
        fun onClick(folderModel: FolderModel) = clickListener(folderModel)
    }
}