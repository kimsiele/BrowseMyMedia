package com.sielee.browsemymedia.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.adapters.FoldersAdapter
import com.sielee.browsemymedia.databinding.FragmentListPhotosFolderBinding
import com.sielee.browsemymedia.viewmodels.FoldersViewModel
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory
import java.security.Permission
import java.util.jar.Manifest

const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
class ListPhotosFolder : Fragment() {
    private lateinit var binding: FragmentListPhotosFolderBinding
    private lateinit var folderAdapter: FoldersAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var viewModel: FoldersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListPhotosFolderBinding.inflate(inflater)
        folderAdapter = FoldersAdapter(itemClick,requireContext())
        askPermission()

        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[FoldersViewModel::class.java]
        gridLayoutManager = GridLayoutManager(requireContext(), 3)

        binding.apply {
            rvFolders.apply {
                adapter = folderAdapter
                layoutManager = gridLayoutManager
            }
        }
        folderAdapter.submitList(viewModel.folders)
        return binding.root
    }

    val itemClick = FoldersAdapter.OnItemClick{ folderModel ->
    findNavController().navigate(ListPhotosFolderDirections.actionListPhotosFolderToListPhotos(
    ))
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionSearchForlders -> {
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionOpenCamera -> {
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionSortFolders -> {
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }
    private fun askPermission(){
        if(ContextCompat.checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        )!=PackageManager.PERMISSION_GRANTED){
           if ( ActivityCompat.shouldShowRequestPermissionRationale(
                   requireActivity(),
               READ_EXTERNAL_STORAGE)) {
               AlertDialog.Builder(requireContext())
                   .setTitle("Permission Required")
                   .setMessage("Permission required to access photos")
                   .setPositiveButton("Allow"){ _, _ ->
                       ActivityCompat.requestPermissions(
                           requireActivity(),
                           arrayOf(READ_EXTERNAL_STORAGE),
                           MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                       )
                       activity?.onBackPressed()
                   }
                   .setNegativeButton("Deny"){ dialog, _ -> dialog.cancel()}
                   .show()
            }
            else{
               ActivityCompat.requestPermissions(
                   requireActivity(),
                   arrayOf(READ_EXTERNAL_STORAGE),
                   MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
               )
            }
        }


    }

}