package com.example.dungziproject.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dungziproject.R
import com.example.dungziproject.databinding.FragmentHomeBinding
import com.example.dungziproject.databinding.FragmentProfileBinding

class ProfileFragment :Fragment() {
    var binding: FragmentProfileBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }
}