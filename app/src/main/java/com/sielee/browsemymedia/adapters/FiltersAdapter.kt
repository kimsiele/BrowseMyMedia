package com.sielee.browsemymedia.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sielee.browsemymedia.data.model.FilterItem
import com.sielee.browsemymedia.databinding.ItemFilterBinding

class FiltersAdapter(private val filterItems: ArrayList<FilterItem>, private val filterClick:(Int)->Unit):RecyclerView.Adapter<FiltersAdapter.FiltersViewHolder>() {

    private var currentSelection = filterItems[0]
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return FiltersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FiltersViewHolder, position: Int) {
        holder.bind(filterItems[position])
    }

    override fun getItemCount(): Int = filterItems.size

   @SuppressLint("NotifyDataSetChanged")
   fun setCurrentFilter(
       position: Int
    ) {
       val filterItem = filterItems[position]
        if (currentSelection != filterItem) {
            currentSelection = filterItem
            notifyDataSetChanged()
            filterClick.invoke(position)
        }
    }

    inner class FiltersViewHolder(private val binding: ItemFilterBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(filterItem: FilterItem){
            binding.apply {

           ivFilter.apply {
                setImageBitmap(filterItem.bitmap)
                scaleType = ImageView.ScaleType.FIT_START
            }
            tvFilter.text = filterItem.filter.name
            }
            binding.root.setOnClickListener {
                setCurrentFilter(adapterPosition)

            }

        }

    }
}