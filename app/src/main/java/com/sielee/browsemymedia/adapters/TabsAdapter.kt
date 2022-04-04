package com.sielee.browsemymedia.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sielee.browsemymedia.ui.CropResizeFragment
import com.sielee.browsemymedia.ui.FilterFragment

private const val TAB_NO = 2
class TabsAdapter(fragment: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragment,lifecycle) {
    override fun getItemCount(): Int = TAB_NO


    override fun createFragment(position: Int): Fragment {
       return when(position){
          0-> FilterFragment()
          else ->CropResizeFragment()
       }
    }

}