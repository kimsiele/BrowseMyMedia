package com.sielee.browsemymedia.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.adapters.PhotoPagerAdapter
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.databinding.FragmentPhotoDetailsBinding
import com.sielee.browsemymedia.databinding.PropertiesDialogBinding
import com.sielee.browsemymedia.databinding.RenameDialogBinding
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory
import com.sielee.browsemymedia.viewmodels.SharedViewModel
import java.io.File

class PhotoDetails : Fragment() {
    private lateinit var currentPhoto: PhotoModel
    private lateinit var photoList: Array<PhotoModel>
    private lateinit var binding: FragmentPhotoDetailsBinding
    private val args: PhotoDetailsArgs by navArgs()
    private lateinit var photosPagerAdapter: PhotoPagerAdapter
    private var selectedPhotoPosition = 0
    lateinit var actionBar: ActionBar
    private lateinit var viewModel: SharedViewModel
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            deletePhoto()
        } else {
            Toast.makeText(
                context!!,
                "Permission is required to delete the item",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val WRITE_EXTERNAL_STORAGE_CODE = 1
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        actionBar = (activity as AppCompatActivity).supportActionBar!!
        Log.d("PhotoDetails", "onAttach: ${args.photos[0].name}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoDetailsBinding.inflate(inflater)
        setHasOptionsMenu(true)
        photoList = args.photos
        selectedPhotoPosition = args.position

        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[SharedViewModel::class.java]
        photoListHandler { photoList ->
            actionBar.title = photoList[selectedPhotoPosition].name
        }
        val clickedItemListener = PhotoPagerAdapter.ClickedItemListener {
            binding.bottomNav.isVisible = !binding.bottomNav.isVisible
        }




        binding.apply {
            photoListHandler { photoList ->
                photosPagerAdapter = PhotoPagerAdapter(
                    photoList.toTypedArray(),
                    clickedItemListener,
                    context!!
                )

                vpPhotoDetails.apply {
                    adapter = photosPagerAdapter
                    setCurrentItem(selectedPhotoPosition, false)
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            photoListHandler { photoList ->
                                actionBar.title = photoList[position].name
                            }

                        }
                    })
                }
            }
            bottomNav.apply {
                itemIconTintList = null
                setOnItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.editPhoto -> {
                            photoListHandler { photos ->
                                Log.d(
                                "PhotoDetail",
                                "onCreateView: ${photos[vpPhotoDetails.currentItem].name}"
                            )
                                findNavController().navigate(
                                    PhotoDetailsDirections.actionPhotoDetailsToEditPhoto(
                                        photos[vpPhotoDetails.currentItem]
                                    )
                                )

                            }

                            true
                        }
                        R.id.actionShare -> {
                            sharePhoto()
                            true
                        }
                        R.id.actionDelete -> {
                            showDeleteDialog()
                            true
                        }
                        else -> false
                    }
                }
            }

        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            R.id.actionCopy -> {
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionRename -> {
                renamePhoto()
                true
            }
            R.id.actionUseAs -> {
                useImageAs()
                true
            }
            R.id.actionProperties -> {
                showProperties()
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == ListPhotosFolder.READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                deletePhoto()
            } else {
                Toast.makeText(context!!, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun photoListHandler(photoHandler: (photoModel: MutableList<PhotoModel>) -> Unit) {
        viewModel.selectedFolderPhotos.observe(viewLifecycleOwner, { photoList ->
            photoHandler.invoke(photoList.toMutableList())
        })
    }

    private fun showProperties() {

        val propertiesBinding =
            PropertiesDialogBinding.inflate(LayoutInflater.from(context!!), null, false)
        currentPhoto = photoList[binding.vpPhotoDetails.currentItem]
        propertiesBinding.apply {
            propertyName.text = currentPhoto.name
            propertySize.text = currentPhoto.size
            propertyPath.text = currentPhoto.path
            propertyLastModified.text = currentPhoto.last_modified
        }
        MaterialAlertDialogBuilder(context!!)
            .setTitle("Properties")
            .setView(propertiesBinding.root)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun useImageAs() {
        photoListHandler { photoList ->
            currentPhoto = photoList[binding.vpPhotoDetails.currentItem]
            val file = File(currentPhoto.path!!)
            val uri = FileProvider.getUriForFile(
                context!!,
                "com.sielee.browsemymedia.fileprovider",
                file
            )
            val setAsIntent = Intent(Intent.ACTION_ATTACH_DATA).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(uri, "image/*")
                putExtra("mimeType", "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(setAsIntent, "Set as:"))
        }

    }

    private fun renamePhoto() {
        currentPhoto = photoList[binding.vpPhotoDetails.currentItem]
        val renameDialogBinding = RenameDialogBinding.inflate(
            LayoutInflater.from(context!!),
            null,
            false
        )
        renameDialogBinding.edRenameName.setText(currentPhoto.name?.substringBeforeLast("."))
        renameDialogBinding.edRenameExtension.setText(
            currentPhoto.name?.substring(
                currentPhoto.name!!.lastIndexOf(
                    "."
                )
            )
        )
        val file = File(currentPhoto.path!!)
        MaterialAlertDialogBuilder(context!!)
            .setTitle("Rename")
            .setView(renameDialogBinding.root)
            .setPositiveButton("OK") { _, _ ->
                renameImageFile(
                    currentPhoto.id,
                    "${renameDialogBinding.edRenameName.text}${renameDialogBinding.edRenameExtension.text}",
                    file
                )
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun renameImageFile(id: Long, name: String, file: File) {

        val values = ContentValues()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        } else {}*/

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name)

        context!!.contentResolver.update(
            uri,
            values,
            MediaStore.Images.Media._ID + "= ?",
            arrayOf(id.toString())
        )

    }

    /*  private fun addImage(renamedImage: File) {
          val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
          intent.data = Uri.fromFile(renamedImage)
          context!!.sendBroadcast(intent)
      }

      private fun removeImage(originalImage: File) {
          context!!.contentResolver.delete(
              MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
              MediaStore.Images.Media.DATA + "=?",
              arrayOf(originalImage.absolutePath)
          )
      }*/

    private fun sharePhoto() {
        currentPhoto = photoList[binding.vpPhotoDetails.currentItem]
        val file = File(currentPhoto.path!!)

        val uri = FileProvider.getUriForFile(
            context!!,
            "com.sielee.browsemymedia.fileprovider",
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        context!!.startActivity(shareIntent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(context!!)
            .setTitle("Are you sure you want to delete this photo?")
            .setPositiveButton("YES") { _, _ ->
                Log.d("PhotoDetails", "List before delete: ${photosPagerAdapter.itemCount} ")
                checkPermission()
                Log.d("PhotoDetails", "List After delete: ${photosPagerAdapter.itemCount} ")
                if (photosPagerAdapter.itemCount < 1) {
                    activity!!.onBackPressed()
                }
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deletePhoto() {
        photoListHandler { photoList ->
            currentPhoto = photoList[binding.vpPhotoDetails.currentItem]
            viewModel.deletePhoto(context!!, currentPhoto.path!!)
            photoList.remove(currentPhoto)
            photosPagerAdapter.notifyItemRemoved(binding.vpPhotoDetails.currentItem)
            photosPagerAdapter.notifyDataSetChanged()
        }

    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                context!!,
                WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                deletePhoto()
            }
            shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permission Required")
                    .setMessage("Permission is required to delete the item")
                    .setPositiveButton("Allow") { _, _ ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(WRITE_EXTERNAL_STORAGE),
                            WRITE_EXTERNAL_STORAGE_CODE
                        )
                        if (ContextCompat.checkSelfPermission(
                                context!!,
                                WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            deletePhoto()
                        }

                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}