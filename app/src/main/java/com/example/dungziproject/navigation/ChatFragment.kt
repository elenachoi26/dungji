package com.example.dungziproject.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dungziproject.R
import com.example.dungziproject.databinding.FragmentCalendarBinding
import com.example.dungziproject.databinding.FragmentChatBinding

class ChatFragment :Fragment() {
    var binding: FragmentChatBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding!!.root
    }
}