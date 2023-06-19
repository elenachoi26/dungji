package com.example.dungziproject

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.R.color.pink
import com.example.dungziproject.databinding.ActivityCalendarMainBinding
import com.example.dungziproject.databinding.CalendarEventsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CalendarMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityCalendarMainBinding
    var data : ArrayList<eventData> = arrayListOf()
    var firestore : FirebaseFirestore?=null
    var uid :String? = null

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragmentId",R.id.action_calendar)
        startActivity(intent)
    }
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        data.add(eventData(1, "a", "b", 2, "c", "d", 3))

        binding.imageView.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentId",R.id.action_calendar)
            startActivity(intent)
        }
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val strDate = "${year}년 ${(month+1)}월 ${dayOfMonth}일"
            binding.textView.text = strDate

            //runOnUiThread {
            //    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
            //}
            val newAdapter = eventAdapter(year,(month+1),dayOfMonth)
            binding.recyclerView2.adapter = newAdapter

            binding.recyclerView2.layoutManager = LinearLayoutManager(this)
            firestore?.collection("calendars")
                ?.orderBy("start_time",Query.Direction.ASCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFireStroreException ->
                    data.clear()
                    //Log.d("DFdfdf", "DFDF")
                    for(snapshot in querySnapshot!!.documents){
                        Log.d("month",snapshot.toString())
                        val item = snapshot.toObject(eventData::class.java)

                        if( (year == item!!.year) && ((month+1) == item.month) && (dayOfMonth == item.day)){
                            data.add(item)
                        }
                    }
                    newAdapter.notifyDataSetChanged()
                }

        }


        binding.button.setOnClickListener {
            //일정수정
            val intent = Intent(this, CalendarActivity1::class.java)
            startActivity(intent)

        }

        binding.button2.setOnClickListener {
            //가족일정맞추기
            val intent = Intent(this, CalendarActivity2::class.java)
            startActivity(intent)
        }
    }

    inner class eventAdapter(year: Int, month: Int, dayOfMonth: Int) : RecyclerView.Adapter<eventAdapter.ViewHolder>(){
        val year = year
        val month = month
        val day = dayOfMonth

        inner class ViewHolder(val binding: CalendarEventsBinding) : RecyclerView.ViewHolder(binding.root){ //viewholder 설정
            init { //초기설정

                firestore?.collection("calendars")
                    ?.orderBy("start_time",Query.Direction.ASCENDING)
                    ?.addSnapshotListener { querySnapshot, firebaseFireStroreException ->
                        data.clear()
                        for(snapshot in querySnapshot!!.documents){
                            val item = snapshot.toObject(eventData::class.java)

                            if( (year == item!!.year) && (month == item.month) && (day == item.day)){
                                data.add(item)
                            }
                        }
                        notifyDataSetChanged()
                    }
            }}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = CalendarEventsBinding.inflate(LayoutInflater.from(parent.context), parent,false)
            firestore?.collection("calendars")
                ?.orderBy("start_time",Query.Direction.ASCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFireStroreException ->
                    data.clear()
                    for(snapshot in querySnapshot!!.documents){
                        val item = snapshot.toObject(eventData::class.java)

                        if( (year == item!!.year) && (month == item.month) && (day == item.day)){
                            data.add(item)
                        }
                    }
                    notifyDataSetChanged()
                }
            return ViewHolder(view)
        }


        override fun getItemCount(): Int {
            return data.size
        }


        @SuppressLint("ResourceAsColor")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = data[position]
            holder.binding.startTime.text = data.start_time
            holder.binding.endTime.text = data.end_time
            holder.binding.event.text = data.event
            holder.binding.place.text = data.place
            val context = binding.root.context
            if(data.event!!.contains("생일")){
                holder.binding.textView2.setTextColor(ContextCompat.getColor(context, R.color.pink))
            }else{
                holder.binding.textView2.setTextColor(ContextCompat.getColor(context, R.color.yellow))
            }
            //notifyItemChanged(position)
        }

    }


}
data class eventData(
    var day : Int = 0,
    var end_time: String? = null,
    var event: String? = null,
    var month : Int = 0,
    var place: String? = null,
    var start_time : String? = null,
    var year : Int = 0
)