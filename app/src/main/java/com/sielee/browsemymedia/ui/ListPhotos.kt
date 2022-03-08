package com.sielee.browsemymedia.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.adapters.PhotosAdapter
import com.sielee.browsemymedia.databinding.FragmentListPhotosBinding
import com.sielee.browsemymedia.viewmodels.FoldersViewModel
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory

const val TAG ="ListPhotos"
class ListPhotos : Fragment() {
    private lateinit var binding: FragmentListPhotosBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var viewModel: FoldersViewModel
    private lateinit var photosAdapter: PhotosAdapter
    private val args:ListPhotosArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListPhotosBinding.inflate(inflater)
        setHasOptionsMenu(true)

        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[FoldersViewModel::class.java]
        gridLayoutManager = GridLayoutManager(requireContext(), 3)
        val folder = args.folder
        activity?.actionBar?.title = folder?.folderName
        val photos = viewModel.getPhotos(folder?.path!!)

        photosAdapter = PhotosAdapter(PhotosAdapter.PhotoClickListener { _, position ->
            findNavController().navigate(
                ListPhotosDirections.actionListPhotosToPhotoDetails(photos.toTypedArray(),position)
            )
        }, requireContext())



        binding.apply {
            rvPhotoList.layoutManager = gridLayoutManager
            rvPhotoList.adapter = photosAdapter
            photosAdapter.submitList(photos)
        }
        Log.d(TAG, "onCreateView: $folder")

        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photos_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                requireActivity().onBackPressed()
                true
            }
            R.id.actionSearchPhoto ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionSortPhotos ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionOpenCamera ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionHidePhotos->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionShowHidden ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

}