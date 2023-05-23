package com.example.dungziproject.navigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dungziproject.ChatActivity

import com.example.dungziproject.databinding.FragmentChatBinding

class ChatFragment :Fragment() {
    var binding: FragmentChatBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val intent = Intent(activity, ChatActivity::class.java) // Intent 객체를 생성하고 ChatActivity를 대상으로 지정합니다.
        startActivity(intent) // 생성한 Intent를 사용하여 ChatActivity를 시작합니다.

        return binding!!.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val context = requireContext() // 현재 fragment의 Context를 가져옵니다.
//        val intent = Intent(context, ChatActivity::class.java) // Intent 객체를 생성하고 ChatActivity를 대상으로 지정합니다.
//        startActivity(intent) // 생성한 Intent를 사용하여 ChatActivity를 시작합니다.
//    }
}