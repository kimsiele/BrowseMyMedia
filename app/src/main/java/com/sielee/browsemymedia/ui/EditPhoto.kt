package com.sielee.browsemymedia.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.persistableBundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.adapters.PhotosAdapter
import com.sielee.browsemymedia.adapters.TabsAdapter
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.databinding.FragmentEditPhotoBinding
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory
import com.sielee.browsemymedia.viewmodels.SharedViewModel

class EditPhoto : Fragment() {
    private lateinit var binding:FragmentEditPhotoBinding
    private lateinit var viewModel: SharedViewModel
    private val args:EditPhotoArgs by navArgs()
    private lateinit var photo:PhotoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photo = args.photoToEdit
        Log.d("EditPhoto", "onCreate: ${photo.path}")
        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[SharedViewModel::class.java]
        viewModel.setPhotoToEdit(photo)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentEditPhotoBinding.inflate(inflater)

        viewModel.photo.observe(viewLifecycleOwner,{
            Log.d("EditPhoto", "onCreateView: ${it.name}")
        })
        val viewPagerAdapter = TabsAdapter(parentFragmentManager,lifecycle)
        binding.viewPagerTabs.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayoutEdits, binding.viewPagerTabs){tab, position ->
            binding.viewPagerTabs.setCurrentItem(1,false)

            when(position) {
                0 -> {
                    tab.icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_add_filter,
                        resources.newTheme()
                    )
                }
                1 -> tab.icon =ResourcesCompat.getDrawable(resources,R.drawable.ic_crop_rotate,resources.newTheme())
            }

        }.attach()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_photo_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when(item.itemId){
            android.R.id.home ->{
                requireActivity().onBackPressed()
                true
            }
            R.id.actionDone ->{
                /*val file = File(photo?.path!!)
                val fileName = file.name.substringBefore(".")+"_1." + file.name.substringAfter(".")
                binding.gpuimageview.saveToPictures(file.parent,fileName,null)
                Toast.makeText(requireContext(), fileName, Toast.LENGTH_SHORT).show()*/
                true
            }
            else -> false

        }
    }

}