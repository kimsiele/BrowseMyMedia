package com.sielee.browsemymedia.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.databinding.FragmentEditPhotoBinding

class EditPhoto : Fragment() {
    private lateinit var binding:FragmentEditPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPhotoBinding.inflate(inflater)
        setHasOptionsMenu(true)
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
                Toast.makeText(requireContext(), "${item.title}", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false

        }
    }

}