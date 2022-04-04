package com.sielee.browsemymedia.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.adapters.FoldersAdapter
import com.sielee.browsemymedia.data.model.FolderModel
import com.sielee.browsemymedia.databinding.FragmentListPhotosFolderBinding
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory
import com.sielee.browsemymedia.viewmodels.SharedViewModel

class ListPhotosFolder : Fragment() {


    private lateinit var binding: FragmentListPhotosFolderBinding
    private lateinit var folderAdapter: FoldersAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var viewModel: SharedViewModel
    private lateinit var globalMenu: Menu


    companion object {
        const val READ_EXTERNAL_STORAGE_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListPhotosFolderBinding.inflate(inflater)
        setHasOptionsMenu(true)
        checkPermission()
        folderAdapter = FoldersAdapter(
            FoldersAdapter.OnItemClick { folderModel ->
                findNavController()
                    .navigate(
                        ListPhotosFolderDirections.actionListPhotosFolderToListPhotos(
                            folderModel
                        )
                    )
            },
            requireContext()
        )

        gridLayoutManager = GridLayoutManager(requireContext(), 3)

        binding.apply {
            rvFolders.apply {
                adapter = folderAdapter
                layoutManager = gridLayoutManager
            }
        }
        foldersHandler { folders ->
            folderAdapter.submitList(folders)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        foldersHandler { folders ->
            folderAdapter.submitList(folders)
        }
    }

    private fun initializeViewModel() {
        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[SharedViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        globalMenu = menu
        val searchItem = globalMenu.findItem(R.id.actionSearchForlders)
        val searchView = searchItem.actionView as SearchView
        performSearch(searchView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionSearchForlders -> {
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionOpenCamera -> {
                openCamera()
                true
            }
            R.id.actionSortFolders -> {
                sortFolders()
                true
            }
            else -> false
        }
    }

    private fun foldersHandler(completionHandler: (folder: List<FolderModel>) -> Unit) {
        checkPermission()
            viewModel.folderList.observe(viewLifecycleOwner, { folders ->
                completionHandler.invoke(folders)
            })
        }

    private fun performSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterFolders(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFolders(newText!!)
                return true
            }

        })
        searchView.isIconfiedByDefault
    }

    private fun filterFolders(query: String) {
        foldersHandler { folders ->
            val filteredFolders = viewModel.folderSearchFilter(folders, query)
            if (filteredFolders.isNotEmpty()) {
                folderAdapter.submitList(null)
                folderAdapter.submitList(filteredFolders)
            } else {
                Toast.makeText(context!!, "No Match found!", Toast.LENGTH_SHORT).show()
                folderAdapter.submitList(null)
            }
        }
    }

    private var requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                initializeViewModel()
            }else{
                Toast.makeText(context!!, "Permission DENIED", Toast.LENGTH_SHORT).show()
                    /*AlertDialog.Builder(requireContext())
                        .setTitle("Permission Required")
                        .setMessage("Permission required to access photos")
                        .setPositiveButton("Allow") { _, _ ->
                            requestPermissions(
                                arrayOf(READ_EXTERNAL_STORAGE),
                                READ_EXTERNAL_STORAGE_CODE
                            )
                        }
                        .setNegativeButton("Deny") { dialog, _ ->
                            dialog.dismiss()
                            activity!!.onBackPressed()
                        }
                        .show()*/
            }
        }

    private fun sortFolders() {
        var checkedItem = 0
        val sortItems = arrayOf("Name", "Last Modified", "Date taken")
        var selectedItem: String = sortItems[checkedItem]
        MaterialAlertDialogBuilder(context!!)
            .setTitle("Sort by")
            .setSingleChoiceItems(sortItems, checkedItem) { _, which ->
                checkedItem = which
                selectedItem = sortItems[which]
            }
            .setPositiveButton("DONE") { _, _ ->
                when (selectedItem) {
                    sortItems[0] -> {
                        foldersHandler {
                            folderAdapter.submitList(viewModel.sortFoldersByName(it))
                        }

                    }
                    sortItems[1] -> {
                        Toast.makeText(context!!, "TODO($selectedItem)", Toast.LENGTH_SHORT).show()
                    }
                    sortItems[2] -> {
                        Toast.makeText(context!!, "TODO($selectedItem)", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(cameraIntent)
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                initializeViewModel()
            }
            shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permission Required")
                    .setMessage("Permission required to access photos")
                    .setPositiveButton("Allow") { _, _ ->
                        requestPermissions(
                            arrayOf(READ_EXTERNAL_STORAGE),
                            READ_EXTERNAL_STORAGE_CODE
                        )
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        dialog.dismiss()
                        activity!!.onBackPressed()
                    }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                initializeViewModel()
            }else{
                Toast.makeText(context!!, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

