package com.sielee.browsemymedia.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.databinding.ItemViewpagerLayoutBinding

class PhotoPagerAdapter(
    private val photos: Array<PhotoModel>,
    private val clickedItemListener: ClickedItemListener,
    private val context: Context
):RecyclerView.Adapter<PhotoPagerAdapter.PhotosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = ItemViewpagerLayoutBinding.inflate(layoutInflater,parent,false)
        return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val currentPhoto = photos[position]
       holder.bind(currentPhoto,context, clickedItemListener)
    }

    override fun getItemCount(): Int = photos.size

    class PhotosViewHolder(private val binding: ItemViewpagerLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(
            photoModel: PhotoModel,
            context: Context,
            clickedItemListener: ClickedItemListener
        ){
            Glide
                .with(context)
                .load(photoModel.path)
                .apply(RequestOptions().fitCenter())
                .into( binding.ivPhoto)

            binding.root.setOnClickListener {
                clickedItemListener.onClick(photoModel)
            }
        }
    }
    class ClickedItemListener(val clickListener: (photoModel: PhotoModel) -> Unit) {
        fun onClick(photoModel: PhotoModel) = clickListener(photoModel)
    }
}