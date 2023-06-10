package com.example.dungziproject.navigation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dungziproject.R
import com.example.dungziproject.databinding.AlbumItemDetailBinding
import com.example.dungziproject.databinding.FragmentAlbumBinding
import com.example.dungziproject.databinding.FragmentHomeBinding
import com.example.dungziproject.navigation.model.ContentDTO
import com.example.dungziproject.navigation.model.User
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AlbumFragment :Fragment() {
    var binding: FragmentAlbumBinding?=null
    var uid :String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        val viewPager = binding?.viewPager
        viewPager?.adapter = AlbumPagerAdapter(requireActivity())


        val tabTitles = listOf<String>("Linear", "Grid")
        val tabIcons = listOf<Int>(R.drawable.baseline_list_24, R.drawable.baseline_grid_view_24)

        if (viewPager != null) {
            TabLayoutMediator(
                binding!!.tabLayout,
                viewPager,
                { tab, position ->
                    tab.setIcon(tabIcons[position])
                }).attach()
        }



        binding!!.uploadBtn.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(requireContext(), PostingActivity::class.java))
            }
        }

        return binding!!.root
    }


}