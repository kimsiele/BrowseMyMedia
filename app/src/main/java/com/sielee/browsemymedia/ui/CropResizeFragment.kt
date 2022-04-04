package com.sielee.browsemymedia.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sielee.browsemymedia.databinding.FragmentCropResizeBinding
import com.sielee.browsemymedia.viewmodels.FoldersViewModelFactory
import com.sielee.browsemymedia.viewmodels.SharedViewModel
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File


class CropResizeFragment : Fragment() {
    private lateinit var binding: FragmentCropResizeBinding
    private lateinit var viewModel: SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCropResizeBinding.inflate(inflater)

        val viewModelFactory = FoldersViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(parentFragment?.activity!!, viewModelFactory)[SharedViewModel::class.java]
        viewModel.photo.observe(viewLifecycleOwner,{ photo ->
            binding.cropImageView2.setImageUriAsync(Uri.fromFile(File(photo?.path!!)))
        })

        binding.apply {
            ibRotate.setOnClickListener {
                CropImage.activity().setAllowRotation(true)
            }
            ibFlipLeftRight.setOnClickListener {
                CropImage.activity().setFlipHorizontally(true)
            }
            ibFlipTopBottom.setOnClickListener {
                CropImage.activity().setFlipVertically(true)
            }
        }

        return binding.root
    }

}