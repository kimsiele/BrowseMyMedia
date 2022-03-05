package com.sielee.browsemymedia.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sielee.browsemymedia.R
import com.sielee.browsemymedia.databinding.FragmentListPhotosBinding
import com.sielee.browsemymedia.databinding.FragmentListPhotosFolderBinding

class ListPhotos : Fragment() {
    private lateinit var binding: FragmentListPhotosBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListPhotosBinding.inflate(inflater)
        setHasOptionsMenu(true)

        binding.apply {
            rvPhotoList.setOnClickListener {
                findNavController().navigate(R.id.action_listPhotos_to_photoDetails)
            }

        }

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