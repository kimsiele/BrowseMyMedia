package com.sielee.browsemymedia.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.adapters.PhotosAdapter
import com.sielee.browsemymedia.data.model.FolderModel
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.databinding.FragmentListPhotosBinding
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory
import com.sielee.browsemymedia.viewmodels.SharedViewModel

const val TAG = "ListPhotos"

class ListPhotos : Fragment() {
    private lateinit var binding: FragmentListPhotosBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var viewModel: SharedViewModel
    private lateinit var photosAdapter: PhotosAdapter
    private val args: ListPhotosArgs by navArgs()
    private lateinit var folder: FolderModel
    private lateinit var actionBar: ActionBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folder = args.folder!!
        actionBar = (activity as AppCompatActivity).supportActionBar!!
        actionBar.title = folder.folderName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListPhotosBinding.inflate(inflater)
        setHasOptionsMenu(true)

        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[SharedViewModel::class.java]
        gridLayoutManager = GridLayoutManager(requireContext(), 3)

        photosHandler { photos ->
            photosAdapter = PhotosAdapter(PhotosAdapter.PhotoClickListener { _, position ->
                findNavController().navigate(
                    ListPhotosDirections.actionListPhotosToPhotoDetails(
                        photos.toTypedArray(),
                        position
                    )
                )
            }, requireContext())

            binding.apply {
                rvPhotoList.layoutManager = gridLayoutManager
                rvPhotoList.adapter = photosAdapter
                photosAdapter.submitList(photos)
                viewModel.setUpCurrentFolderPhotos(photos.toTypedArray())
            }
        }
        Log.d(TAG, "onCreateView: $folder")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        actionBar.title = folder.folderName
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photos_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearchPhoto)
        val searchView = searchItem.actionView as SearchView
        performSearch(searchView)

        val hideHiddenItem = menu.findItem(R.id.actionHidePhotos)
        val showHiddenItem = menu.findItem(R.id.actionShowHidden)
        photosHandler { photos ->
            hideHiddenItem.isVisible = viewModel.hiddenPhotosExist(photos)
            showHiddenItem.isVisible = !viewModel.hiddenPhotosExist(photos)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            R.id.actionSortPhotos -> {
               sortPhotos()
                true
            }
            R.id.actionOpenCamera -> {
                openCamera()
                true
            }
            R.id.actionHidePhotos -> {
                true
            }
            R.id.actionShowHidden -> {
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        startActivity(cameraIntent)
    }

    private fun photosHandler(completionHandler: (photos: List<PhotoModel>) -> Unit) {
        viewModel.getPhotos(folder.path!!).observe(viewLifecycleOwner, { photos ->
            completionHandler.invoke(photos)
        })
    }

    private fun performSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPhotos(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPhotos(newText!!)
                return true
            }

        })
        searchView.isIconfiedByDefault
    }

    private fun sortPhotos() {
        var checkedItem = 0
        val sortItems = arrayOf("Name", "Last Modified", "Date taken", "size")
        var selectedItem: String = sortItems[checkedItem]
        MaterialAlertDialogBuilder(context!!)
            .setTitle("Sort by")
            .setSingleChoiceItems(sortItems, checkedItem) { _, which ->
                checkedItem = which
                selectedItem = sortItems[which]
            }
            .setPositiveButton("DONE") { _, _ ->
                photosHandler { photo ->
                    when (selectedItem) {
                        sortItems[0] -> {
                            photosAdapter.submitList(viewModel.sortPhotosByName(photo))
                        }
                        sortItems[1] -> {
                            photosAdapter.submitList(viewModel.sortPhotosByDateModified(photo))
                        }
                        sortItems[3] -> {
                            photosAdapter.submitList(viewModel.sortPhotosBySize(photo))
                        }
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun filterPhotos(query: String) {
        photosHandler { photos ->
            val filteredPhotos = viewModel.photoSearchFilter(photos, query)
            if (filteredPhotos.isNotEmpty()) {
                photosAdapter.submitList(null)
                photosAdapter.submitList(filteredPhotos)
            } else {
                Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT)
                    .show()
                photosAdapter.submitList(null)
            }
        }
    }

}