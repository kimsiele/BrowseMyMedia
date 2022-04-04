package com.sielee.browsemymedia.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.sielee.browsemymedia.adapters.FiltersAdapter
import com.sielee.browsemymedia.data.model.FilterItem
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.databinding.FragmentFilterFramentBinding
import com.sielee.browsemymedia.utils.ThumbnailManager
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory
import com.sielee.browsemymedia.viewmodels.SharedViewModel
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import java.io.File

class FilterFragment : Fragment() {
    private lateinit var binding: FragmentFilterFramentBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var filterAdapter: FiltersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilterFramentBinding.inflate(inflater)

        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(
                parentFragment?.activity!!,
                viewModelFactory
            )[SharedViewModel::class.java]
        photoHandler { photo ->

            val imageUri = Uri.fromFile(File(photo.path!!))
            Glide.with(context!!).load(imageUri).into(binding.ivToFilter)
        }
       setAdapterData()
        return binding.root

    }

    private fun photoHandler(photoCompletion: (photoModel: PhotoModel) -> Unit) {
        viewModel.photo.observe(viewLifecycleOwner, { photo ->
            photoCompletion.invoke(photo)
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterData() {
        val thumbnailManager = ThumbnailManager()
        thumbnailManager.clearThumbs()
        photoHandler { photo ->
            val image = File(photo.path!!)
            val bitmapImage = Bitmap.createBitmap(
                BitmapFactory.decodeFile(
                    image.absolutePath,
                    BitmapFactory.Options()
                )
            )
            val withoutFilters = Filter("none")
            thumbnailManager.addThumbs(FilterItem(bitmapImage, withoutFilters))

            val filters: MutableList<Filter> = FilterPack.getFilterPack(context!!)
            filters.forEach {filter ->
                val filterItem = FilterItem(bitmapImage, filter)
                thumbnailManager.addThumbs(filterItem)
            }

            val filterItems = thumbnailManager.processThumbs()
            filterAdapter = FiltersAdapter(filterItems) { position ->
                binding.ivToFilter.setImageBitmap(filterItems[position].filter.processFilter(bitmapImage))
            }
            binding.rvFilters.adapter = filterAdapter
            filterAdapter.notifyDataSetChanged()
        }
    }

}