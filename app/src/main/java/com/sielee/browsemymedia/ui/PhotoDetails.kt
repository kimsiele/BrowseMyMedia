package com.sielee.browsemymedia.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.R.drawable.*
import com.sielee.browsemymedia.databinding.FragmentPhotoDetailsBinding

class PhotoDetails : Fragment() {
    private lateinit var binding:FragmentPhotoDetailsBinding
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.actionBar?.setBackgroundDrawable(context.getDrawable(top_gradient_background))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentPhotoDetailsBinding.inflate(inflater)
        setHasOptionsMenu(true)
        binding.apply {
            vpPhotoDetails.setOnClickListener {
                val actionbar = requireActivity().actionBar
                if (actionbar?.isShowing==true && bottomNav.isVisible){
                    actionbar.hide()
                    bottomNav.isVisible = false
                }else{
                    actionbar?.show()
                    bottomNav.isVisible = true
                }
            }
            bottomNav.setupWithNavController(findNavController())

        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                requireActivity().onBackPressed()
                true
            }
            R.id.actionCopy ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionRename ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionUseAs ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.actionProperties ->{
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }


}