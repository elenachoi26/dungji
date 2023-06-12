package com.example.dungziproject.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LinearFragment() // Fragment for LinearLayout
            1 -> GridFragment() // Fragment for GridLayout
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }


}
