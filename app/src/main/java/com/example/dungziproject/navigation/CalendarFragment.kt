package com.example.dungziproject.navigation


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dungziproject.CalendarMainActivity
import com.example.dungziproject.databinding.FragmentCalendarBinding

class CalendarFragment :Fragment() {
    var binding: FragmentCalendarBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        //일주일 시간표
        binding!!.scheduleBtn.setOnClickListener {
            activity?.let{
                val intent = Intent(context, TimeTableActivity::class.java)
                startActivity(intent)
            }
        }


        //월 캘린더
        val intent = Intent(activity, CalendarMainActivity::class.java)
        binding!!.calendarBtn.setOnClickListener {
            startActivity(intent)
       
        }
        return binding!!.root
    }
}