package com.example.dungziproject.navigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dungziproject.R
import com.example.dungziproject.TimeTableActivity
import com.example.dungziproject.databinding.FragmentCalendarBinding
import com.example.dungziproject.databinding.FragmentHomeBinding

class CalendarFragment :Fragment() {
    var binding: FragmentCalendarBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        binding!!.timeTableBtn.setOnClickListener {
            activity?.let{
                val intent = Intent(context, TimeTableActivity::class.java)
                startActivity(intent)
            }
        }

        binding!!.calendarBtn.setOnClickListener {
        }

        return binding!!.root
    }
}