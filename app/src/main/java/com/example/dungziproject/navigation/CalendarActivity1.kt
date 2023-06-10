package com.example.dungziproject.navigation

import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.CalendarChange
import com.example.dungziproject.CalendarInsert
import com.example.dungziproject.R
import com.example.dungziproject.databinding.ActivityCalendar1Binding
import com.example.dungziproject.databinding.CalendarEvents2Binding
import com.example.dungziproject.navigation.model.eventData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList

class CalendarActivity1 : AppCompatActivity() {
    lateinit var binding: ActivityCalendar1Binding
    var data : ArrayList<eventData> = arrayListOf()
    var firestore : FirebaseFirestore?=null
    var uid :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendar1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        data.add(eventData(1, "a", "b", 2, "c", "d", 3))

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val strDate = "${year}년 ${(month+1)}월 ${dayOfMonth}일"
            binding.textView.text = strDate

            val newAdapter = eventAdapter(year,(month+1),dayOfMonth)
            binding.recyclerView2.adapter = newAdapter

            binding.recyclerView2.layoutManager = LinearLayoutManager(this)
            val addSnapshotListener = firestore?.collection("calendars")
                ?.orderBy("start_time", Query.Direction.ASCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFireStroreException ->
                    data.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        val item = snapshot.toObject(eventData::class.java)

                        if ((year == item!!.year) && ((month + 1) == item.month) && (dayOfMonth == item.day)) {
                            data.add(item)
                        }
                    }
                    newAdapter.notifyDataSetChanged()
                }

            val itemTouchCallback = object : ItemTouchHelper.SimpleCallback (
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
            ){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos: Int = viewHolder.adapterPosition
                    val toPos: Int = target.adapterPosition
                    newAdapter.swapData(fromPos, toPos)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    newAdapter.removeData(viewHolder.layoutPosition)
                }
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val icon: Bitmap
                    // actionState가 SWIPE 동작일 때 배경을 빨간색으로 칠하는 작업을 수행하도록 함
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        val itemView = viewHolder.itemView
                        val height = (itemView.bottom - itemView.top).toFloat()
                        val width = height / 4
                        val paint = Paint()
                        if (dX < 0) {  // 왼쪽으로 스와이프하는지 확인
                            // ViewHolder의 백그라운드에 깔아줄 사각형의 크기와 색상을 지정
                            paint.color = Color.parseColor("#ff0000")
                            val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                            c.drawRect(background, paint)

                            // 휴지통 아이콘과 표시될 위치를 지정하고 비트맵을 그려줌
                            // 비트맵 이미지는 Image Asset 기능으로 추가하고 drawable 폴더에 위치하도록 함

                            icon = BitmapFactory.decodeResource(resources,
                                R.drawable.ic_menu_delete
                            )
                            val iconDst = RectF(itemView.right.toFloat() - 3  - width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                            c.drawBitmap(icon, null, iconDst, null)
                        }
                    }

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }

            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.recyclerView2)

            //추가버튼 누르면 calendarinsertActivity로 이동
            binding.imageButton2.setOnClickListener {
                val intent = Intent(this, CalendarInsert::class.java)
                intent.putExtra("year", year)
                intent.putExtra("month", month+1)
                intent.putExtra("day", dayOfMonth)
                startActivity(intent)
            }
        }




    }


    inner class eventAdapter(year: Int, month: Int, dayOfMonth: Int) : RecyclerView.Adapter<eventAdapter.ViewHolder>(){
        val year = year
        val month = month
        val day = dayOfMonth

        inner class ViewHolder(val binding: CalendarEvents2Binding) : RecyclerView.ViewHolder(binding.root){ //viewholder 설정
            init { //초기설정
                Log.d("짜증나", "짜증나")
                firestore?.collection("calendars")
                    ?.orderBy("start_time",Query.Direction.ASCENDING)
                    ?.addSnapshotListener { querySnapshot, firebaseFireStroreException ->
                        data.clear()
                        Log.d("DFdfdf", "DFDF")
                        for(snapshot in querySnapshot!!.documents){
                            val item = snapshot.toObject(eventData::class.java)

                            if( (year == item!!.year) && (month == item.month) && (day == item.day)){
                                data.add(item)
                            }
                        }
                        notifyDataSetChanged()
                    }
                binding.imageButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        //변경버튼 누르면 액티비티 전환, 단 현재 정보를 넘겨야함
                        val selectedItem = data[position]
                        val intent = Intent(itemView.context, CalendarChange::class.java)
                        intent.putExtra("year", selectedItem.year)
                        intent.putExtra("month", selectedItem.month)
                        intent.putExtra("day", selectedItem.day)
                        intent.putExtra("start_time", selectedItem.start_time)
                        intent.putExtra("end_time", selectedItem.end_time)
                        intent.putExtra("event", selectedItem.event)
                        intent.putExtra("place", selectedItem.place)
                        itemView.context.startActivity(intent)
                    }
                }

            }}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = CalendarEvents2Binding.inflate(LayoutInflater.from(parent.context), parent,false)
            Log.d("짜증나", "짜증나")
            firestore?.collection("calendars")
                ?.orderBy("start_time",Query.Direction.ASCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFireStroreException ->
                    data.clear()
                    Log.d("DFdfdf", "DFDF")
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

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("dfdfdf","nowat")
            val data = data[position]
            holder.binding.startTime.text = data.start_time
            holder.binding.endTime.text = data.end_time
            holder.binding.event.text = data.event
            holder.binding.place.text = data.place
            //notifyItemChanged(position)
        }
        // ViewHolder 포지션을 받아 그 위치의 데이터를 삭제하고 notifyItemRemoved로 어댑터에 갱신명령을 전달
        fun removeData(position: Int) {


            val query = firestore?.collection("calendars")
                ?.whereEqualTo("year", data[position].year)
                ?.whereEqualTo("month", data[position].month)
                ?.whereEqualTo("day", data[position].day)
                ?.whereEqualTo("event", data[position].event)

            query?.get()
                ?.addOnSuccessListener { querySnapshot ->
                    for (documentSnapshot in querySnapshot.documents) {
                        val documentId = documentSnapshot.id
                        // 문서를 삭제합니다.
                        firestore?.collection("calendars")?.document(documentId)?.delete()
                    }
                }
                ?.addOnFailureListener { exception ->
                    // 삭제 작업 실패 시 에러 처리
                }

            data.removeAt(position)
            notifyItemRemoved(position)

        }

        // 두 개의 ViewHolder 포지션을 받아 Collections.swap으로 첫번째 위치와 두번째 위치의 데이터를 교환
        fun swapData(fromPos: Int, toPos: Int) {
            Collections.swap(data, fromPos, toPos)
            notifyItemMoved(fromPos, toPos)
        }


    }



}





