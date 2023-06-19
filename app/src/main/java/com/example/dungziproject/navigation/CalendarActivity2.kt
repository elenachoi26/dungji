package com.example.dungziproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dungziproject.databinding.ActivityCalendar2Binding
import com.example.dungziproject.navigation.model.TimeTable
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.checkerframework.checker.units.qual.s
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates

data class flag(
    var flag:Int,
){
    constructor():this(0)
}
class CalendarActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityCalendar2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var id:String  // userId 저장할 변수
    private var spinnerKey = 0  // 처음에 spinner listener가 실행되는거 방지하는 키
    private var hashMap = HashMap<String, String>() // <nickname, userId> 저장할 HashMap
    private val color = listOf(
        Color.parseColor("#FF9696"), Color.parseColor("#FFB096"), Color.parseColor("#FFFF96"),
        Color.parseColor("#CEFF96"), Color.parseColor("#96BDFF"), Color.parseColor("#96A6FF"),
        Color.parseColor("#B296FF"), Color.parseColor("#FFD196"), Color.parseColor("#FFE996"),
        Color.parseColor("#F6FF96"), Color.parseColor("#96FFCC"), Color.parseColor("#96FFE6"),
        Color.parseColor("#96FFFF"), Color.parseColor("#96DEFF"), Color.parseColor("#F096FF"),
        Color.parseColor("#FF96BA"))    // 시간표 스케줄 backgroundColor
    private var scheduleCount = 0   // 시간표 스케줄 시간표의 backgroudColor를 변경해주기 위한 변수
    private lateinit var schedule: Array<IntArray>  // 스케줄 시간 중복을 막기 위한 2차원 배열
    private val weeklist :ArrayList<String> = arrayListOf("mon","tue","wed","thu","fri","sat","sun")
    private var userlist:ArrayList<String> = arrayListOf("")

    override fun onBackPressed() {
        // 원하는 동작을 여기에 코딩
        val intent = Intent(this, CalendarMainActivity::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendar2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView.setOnClickListener {
            val intent = Intent(this, CalendarMainActivity::class.java)
            startActivity(intent)
        }
        initLayout()
    }

    //가족일정맞추기 activity, family DB 조회
    fun initLayout(){
        auth = Firebase.auth
        database = Firebase.database.reference
        schedule = Array(13){IntArray(7)}   // 13행(시간) 7열(요일) 2차원 배열

        initFamilyTime() //가족 초기화
        initUserList() // user 초기화

        //timetable 조회 => familytime flag = 1로 바꿈
        //drawTimeTable()

        //familytime flag = 0인 것만 그리기



    }
    fun initUserList(){
        userlist.clear()
        database.child("user")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val user = postSnapshat.getValue(User::class.java)
                        userlist.add(user?.userId!!)
                        Log.i("userid",userlist.toString())
                    }
                    drawTimeTable()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    fun initFamilyTime(){
        //familytime 초기화 9~22까지 flag = 0
        for( day in weeklist) {
            for (s in 9..21){
                val e = s+1
                var start = s.toString()
                var end = e.toString()
                if( s < 10) {  start = "0${s}"}
                database.child("familytime").child("${day}${start}${end}").child("flag")
                    .setValue(0)
            }
        }
    }

    private fun drawTimeTable(){
        //timetable 조회 => familytime flag = 1로 바꿈
        for (id in userlist) {
            Log.i("id",id)
            // 특정 문서의 존재 여부 확인
            val userRef = database.child("timetable").child(id)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 문서가 존재하는 경우 처리
                        // 해당 문서에 접근하여 데이터를 가져오거나 원하는 작업을 수행할 수 있습니다.
                        database.child("timetable").child(id)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    scheduleCount = 0
                                    for (postSnapshat in snapshot.children) {
                                        val time = postSnapshat.getValue(TimeTable::class.java)
                                        val day = weekToEnglish(time?.week!!)
                                        Log.i("day",day)
                                        var start = 9
                                        if (time?.startTime!! != "09"){
                                            start = time?.startTime!!.toInt()
                                            Log.i("start",start.toString())
                                        }
                                        var end = time?.endTime!!.toInt()
                                        Log.i("end",end.toString())
                                        for (s in start..end-1){
                                            if(s == 9){
                                                database.child("familytime").child("${day}0910").child("flag")
                                                    .setValue(1)
                                                setTextViewFalse(
                                                    day,
                                                    "09",
                                                    "10",
                                                )
                                            }else{
                                                database.child("familytime").child("${day}${s}${s+1}").child("flag")
                                                    .setValue(1)
                                                setTextViewFalse(
                                                    day,
                                                    s.toString(),
                                                    (s+1).toString()
                                                )
                                            }

                                        }

                                    }
                                    // 비어있는 시간 조회
                                    familyTime()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                    } else {
                        // 문서가 존재하지 않는 경우 처리
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터 읽기 실패 시 처리
                }
            })

        }
    }
    private fun familyTime(){
        database.child("familytime")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val flag = postSnapshat.getValue(flag::class.java)!!.flag
                        if(flag == 0){
                            val familytimekey = postSnapshat.key.toString()
                            val day = familytimekey.substring(0, 3) // Extract the first 3 characters as the day
                            val startTime = familytimekey.substring(3, 5) // Extract the characters at index 3 and 4 as the start time
                            val endTime = familytimekey.substring(5)
                            setTextViewTrue(day,startTime,endTime)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    private fun weekToEnglish(week: String):String {
        return when(week){
            "월" -> "mon"
            "화" -> "tue"
            "수" -> "wed"
            "목" -> "thu"
            "금" -> "fri"
            "토" -> "sat"
            "일" -> "sun"
            else -> ""
        }
    }
    private fun weekToNumber(week: String):Int {
        return when(week) {
            "mon" -> 1
            "tue" -> 2
            "wed" -> 3
            "thu" -> 4
            "fri" -> 5
            "sat" -> 6
            "sun" -> 7
            else -> 0
        }
    }

    private fun weekCalculate(week: String):Int {
        return when(week) {
            "mon" -> Calendar.MONDAY
            "tue" -> Calendar.TUESDAY
            "wed" -> Calendar.WEDNESDAY
            "thu" -> Calendar.THURSDAY
            "fri" -> Calendar.FRIDAY
            "sat" -> Calendar.SATURDAY
            "sun" -> Calendar.SUNDAY
            else -> 0
        }
    }
    private fun setTextViewFalse(week: String, startTime: String, endTime: String) {
        val gridLayout = binding.gridlayout
        val textView = TextView(this)
        val layoutParams = GridLayout.LayoutParams()
        val textViewId = week + startTime + endTime  // ex) mon0909
        textView.text = "불가능"
        textView.textSize = 10f
        layoutParams.width = 0
        layoutParams.height = 0
        val col = weekToNumber(week)
        val row = startTime.toInt() - 8
        val span = endTime.toInt() - startTime.toInt()
        layoutParams.columnSpec = GridLayout.spec(col)
        layoutParams.rowSpec = GridLayout.spec(row, span)
        layoutParams.setGravity(Gravity.FILL)
        textView.setBackgroundColor(color[0])

        // 추가한 스케줄 TextView 클릭리스너
        textView.setOnClickListener{
            val builder = AlertDialog.Builder(this)

            with(builder) {
                setTitle("스케줄 정보")
                setMessage("\n모두가 모이기 불가능한 시간입니다.\n")

                // 취소 선택시
                setNegativeButton("취소"){dialog,which->
                    dialog.cancel()
                }
                show()
            }
        }
        gridLayout.addView(textView, layoutParams)

    }
    private fun setTextViewTrue(week: String, startTime: String, endTime: String) {
        val gridLayout = binding.gridlayout
        val textView = TextView(this)
        val layoutParams = GridLayout.LayoutParams()
        val textViewId = week + startTime + endTime  // ex) mon0909
        textView.text = "가능"
        textView.textSize = 10f
        layoutParams.width = 0
        layoutParams.height = 0
        val col = weekToNumber(week)
        val row = startTime.toInt() - 8
        val span = endTime.toInt() - startTime.toInt()
        layoutParams.columnSpec = GridLayout.spec(col)
        layoutParams.rowSpec = GridLayout.spec(row, span)
        layoutParams.setGravity(Gravity.FILL)
        textView.setBackgroundColor(color[4])

        // 추가한 스케줄 TextView 클릭리스너
        textView.setOnClickListener{
            val builder = AlertDialog.Builder(this)

            with(builder) {
                setTitle("스케줄 정보")
                setMessage("\n"  +
                        week + " " + startTime + ":00~" + endTime + ":00\n"
                        + "가능한 가장 가까운 시간을 가족일정에 추가하시겠습니까?")

                setPositiveButton("추가") { dialog, _ ->

                    // 현재 날짜와 시간을 포함하는 Calendar 인스턴스 생성
                    val calendar = Calendar.getInstance()
                    val stateweek = calendar.get(Calendar.DAY_OF_WEEK)
                    val finalweek = weekCalculate(week)
                    Log.d("stateweek",stateweek.toString())
                    Log.d("finalweek",finalweek.toString())
                    Log.d("cal",((finalweek - stateweek)+7).toString())
                    if (stateweek >= finalweek)
                        calendar.add(Calendar.DAY_OF_MONTH, ((finalweek - stateweek)+7))
                    else
                        calendar.add(Calendar.DAY_OF_MONTH, finalweek - stateweek)
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH) + 1
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    // 요일을 문자열로 변환
                    val dayOfWeekString = when (dayOfWeek) {
                        Calendar.SUNDAY -> "일요일"
                        Calendar.MONDAY -> "월요일"
                        Calendar.TUESDAY -> "화요일"
                        Calendar.WEDNESDAY -> "수요일"
                        Calendar.THURSDAY -> "목요일"
                        Calendar.FRIDAY -> "금요일"
                        Calendar.SATURDAY -> "토요일"
                        else -> ""
                    }
                    Toast.makeText(this@CalendarActivity2,"가장가까운날짜는 ${year}년 ${month}월 ${day}일, ${dayOfWeekString} 입니다.",Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, CalendarInsert::class.java)
                    intent.putExtra("year", year)
                    intent.putExtra("month", month)
                    intent.putExtra("day", day)
                    intent.putExtra("start_time","${startTime}:00")
                    intent.putExtra("end_time","${endTime}:00")
                    intent.putExtra("event","가족일정")
                    startActivity(intent)
                    dialog.dismiss()
                }
                // 취소 선택시
                setNegativeButton("취소"){dialog,which->
                    dialog.cancel()
                }
                show()
            }
        }
        gridLayout.addView(textView, layoutParams)


    }
}