package com.example.dungziproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.dungziproject.databinding.ActivityCalendarChangeBinding
import com.example.dungziproject.databinding.ActivityCalendarInsertBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CalendarInsert : AppCompatActivity() {
    lateinit var binding: ActivityCalendarInsertBinding
    var firestore : FirebaseFirestore?=null
    var uid :String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        val year = intent.extras?.getInt("year")
        val month = intent.extras?.getInt("month")
        val day = intent.extras?.getInt("day")
        //*********년도***********//

        // 스피너 초기화
        val Yearspinner = binding.spinnerDateYear

        // 스피너에 표시할 데이터 리스트 생성
        val yearList = listOf("2020", "2021", "2022", "2023","2024","2025")

        // 어댑터 생성 및 데이터 설정
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearList)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 어댑터를 스피너에 설정
        Yearspinner.adapter = yearAdapter
        val desiredItem = year.toString() // 내가 정하고자 하는 항목
        val desiredIndex = yearList.indexOf(desiredItem)
        Yearspinner.setSelection(desiredIndex)

        // 스피너 아이템 선택 이벤트 리스너 설정 (선택된 항목 변경 시 호출)
        Yearspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "선택된 항목: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때의 동작 정의

            }
        }


        //*********월***********//

        // 스피너 초기화
        val Monthspinner = binding.spinnerDateMonth

        // 스피너에 표시할 데이터 리스트 생성
        val monthList = listOf("1", "2", "3", "4","5","6","7","8","9","10","11","12")

        // 어댑터 생성 및 데이터 설정
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monthList)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 어댑터를 스피너에 설정
        Monthspinner.adapter = monthAdapter
        val desiredItem2 = month.toString() // 내가 정하고자 하는 항목
        val desiredIndex2 = monthList.indexOf(desiredItem2)
        Monthspinner.setSelection(desiredIndex2)

        // 스피너 아이템 선택 이벤트 리스너 설정 (선택된 항목 변경 시 호출)
        Monthspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "선택된 항목: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때의 동작 정의

            }
        }




        //*********일***********//

        // 스피너 초기화
        val Dayspinner = binding.spinnerDateDay

        // 스피너에 표시할 데이터 리스트 생성
        val dayList :ArrayList<String> = arrayListOf()
        for( i in 1..31){
            dayList.add(i.toString())
        }

        // 어댑터 생성 및 데이터 설정
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayList)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 어댑터를 스피너에 설정
        Dayspinner.adapter = dayAdapter
        val desiredItem3 = day.toString() // 내가 정하고자 하는 항목
        val desiredIndex3 = dayList.indexOf(desiredItem3)
        Dayspinner.setSelection(desiredIndex3)

        // 스피너 아이템 선택 이벤트 리스너 설정 (선택된 항목 변경 시 호출)
        Dayspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "선택된 항목: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때의 동작 정의

            }
        }


        //*********시작 시간(시)***********//

        // 스피너 초기화
        val StartHourspinner = binding.spinnerStartTimeHour

        // 스피너에 표시할 데이터 리스트 생성
        var startHourList :ArrayList<String> = arrayListOf()
        for( i in 0..23){
            startHourList.add(i.toString())
        }

        // 어댑터 생성 및 데이터 설정
        val startHourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, startHourList)
        startHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 어댑터를 스피너에 설정
        StartHourspinner.adapter = startHourAdapter

        // 스피너 아이템 선택 이벤트 리스너 설정 (선택된 항목 변경 시 호출)
        StartHourspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "선택된 항목: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때의 동작 정의

            }
        }


        //*********시작 시간(분)***********//

        // 스피너 초기화
        val StartMinspinner = binding.spinnerStartTimeMinute

        // 스피너에 표시할 데이터 리스트 생성
        val startMinList :ArrayList<String> = arrayListOf()
        for( i in 0..59){
            startMinList.add(i.toString())
        }

        // 어댑터 생성 및 데이터 설정
        val startMinAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, startMinList)
        startMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 어댑터를 스피너에 설정
        StartMinspinner.adapter = startMinAdapter


        // 스피너 아이템 선택 이벤트 리스너 설정 (선택된 항목 변경 시 호출)
        StartMinspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "선택된 항목: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때의 동작 정의

            }
        }

        //*********종료 시간(시)***********//

        // 스피너 초기화
        val EndHourspinner = binding.spinnerEndTimeHour

        // 스피너에 표시할 데이터 리스트 생성
        var endHourList :ArrayList<String> = arrayListOf()
        for( i in 0..23){
            endHourList.add(i.toString())
        }

        // 어댑터 생성 및 데이터 설정
        val endHourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, endHourList)
        endHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 어댑터를 스피너에 설정
        EndHourspinner.adapter = endHourAdapter



        // 스피너 아이템 선택 이벤트 리스너 설정 (선택된 항목 변경 시 호출)
        EndHourspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "선택된 항목: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때의 동작 정의

            }
        }

        //*********종료 시간(분)***********//

        // 스피너 초기화
        val EndMinspinner = binding.spinnerEndTimeMinute

        // 스피너에 표시할 데이터 리스트 생성
        var endMinList :ArrayList<String> = arrayListOf()
        for( i in 0..59){
            endMinList.add(i.toString())
        }

        // 어댑터 생성 및 데이터 설정
        val endMinAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, endMinList)
        endMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 어댑터를 스피너에 설정
        EndMinspinner.adapter = endMinAdapter


        // 스피너 아이템 선택 이벤트 리스너 설정 (선택된 항목 변경 시 호출)
        EndMinspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "선택된 항목: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택된 항목이 없을 때의 동작 정의

            }
        }


        //*****************초기 세팅 끝************************************//
        //저장버튼을 누르면 현재 스피너 선택한 값, 이름과 장소에 수정한 텍스트를 토대로 바꿔서 파이어베이스 내용을 바꾸고 activity 전환

        binding.button3.setOnClickListener {
            // 스피너에서 선택한 값을 가져옴
            val updateyear = binding.spinnerDateYear.selectedItem.toString().toInt()
            val updatemonth = binding.spinnerDateMonth.selectedItem.toString().toInt()
            val updateday = binding.spinnerDateDay.selectedItem.toString().toInt()
            var starthour = binding.spinnerStartTimeHour.selectedItem.toString()
            var startmin = binding.spinnerStartTimeMinute.selectedItem.toString()
            if (starthour.toInt() < 10 ){
                starthour = "0$starthour"
            }
            if (startmin.toInt() < 10 ){
                startmin = "0$startmin"
            }
            val start_time = "$starthour:$startmin"
            var endhour = binding.spinnerEndTimeHour.selectedItem.toString()
            var endmin = binding.spinnerEndTimeMinute.selectedItem.toString()
            if (endhour.toInt() < 10 ){
                endhour = "0$endhour"
            }
            if (endmin.toInt() < 10 ){
                endmin = "0$endmin"
            }
            val end_time = "$endhour:$endmin"
            // 텍스트필드에서 선택한 값을 가져옴
            val event = binding.eventName.text.toString()
            val place = binding.placeName.text.toString()


            // Firebase 데이터베이스 추가
            // 문서 추가
            val db = FirebaseFirestore.getInstance()
            val collectionRef = db.collection("calendars")

            val data = hashMapOf(
                "year" to updateyear,
                "month" to updatemonth,
                "day" to updateday,
                "start_time" to start_time,
                "end_time" to end_time,
                "event" to event,
                "place" to place
                // 필요한 만큼 필드와 값 추가
            )

            collectionRef.add(data)
                .addOnSuccessListener { documentReference ->
                    // 추가 성공
                    val documentId = documentReference.id
                }
                .addOnFailureListener { e ->
                    // 추가 실패
                }

            val intent = Intent(this, CalendarMainActivity::class.java)
            startActivity(intent)
        }


    }





}