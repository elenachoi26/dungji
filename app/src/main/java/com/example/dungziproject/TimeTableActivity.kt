package com.example.dungziproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.dungziproject.databinding.ActivityTimeTableBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TimeTableActivity : AppCompatActivity() {
    lateinit var binding: ActivityTimeTableBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var id:String
    private var spinnerKey = 0
    private var hashMap = HashMap<String, String>()
    private lateinit var schedule: Array<IntArray>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference
        schedule = Array(13){IntArray(7)}

        initHashMap()
        initSchedule()

        id = auth.currentUser?.uid!!

        // 시간표에 처음 들어갔을때
        if(intent.getStringExtra("id") != null)
            id = intent?.getStringExtra("id")!!

        var nickname = ""
        // 닉네임 받아오기
        if(intent.getStringExtra("nickname") != null)
            nickname = intent?.getStringExtra("nickname")!!

        // 누구의 스케줄인지
        if(auth.currentUser?.uid!! == id){
            binding.whosschedule.text = "나의 스케줄"
        }else{
            binding.whosschedule.text = nickname + "의 스케줄"
        }

        // 자신의 시간표가 아니면 추가, 삭제 버튼 안보임
        if(id == auth.currentUser?.uid!!){
            binding.add.visibility = View.VISIBLE
            binding.sub.visibility = View.VISIBLE
        }else{
            binding.add.visibility = View.GONE
            binding.sub.visibility = View.GONE
        }

        drawTimeTable()
        addSpinner()

        // 스케줄 추가 버튼
        binding.add.setOnClickListener {
            showAddTime()
        }

        // 스케줄 삭제 버튼
        binding.sub.setOnClickListener {
            showSubTime()
        }

        // 다른사람 스케줄 Spinner 리스너
        binding.others.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(spinnerKey == 1) {
                    val nick = binding.others.selectedItem.toString()
                    val intent = Intent(this@TimeTableActivity, TimeTableActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("id", hashMap.get(nick))
                    intent.putExtra("nickname", nick)
                    startActivity(intent)
                    finish()
                }
                spinnerKey = 1
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initSchedule() {
        database.child("timetable").child(auth.currentUser?.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val time = postSnapshat.getValue(TimeTable::class.java)
                        val startTime = time?.startTime!!.toInt()
                        val endTime = time?.endTime!!.toInt()
                        val span = endTime - startTime
                        val column = weekToNumber(time?.week!!) - 1
                        for(i:Int in 0 until span){
                            schedule[startTime-9+i][column] = 1
                        }
                    }
                    for(i:Int in 0 until 13){
                        print("$i : ")
                        for(j:Int in 0 until 7){
                            print(schedule[i][j])
                        }
                        println()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // hashMap 초기화
    private fun initHashMap() {
        database.child("user")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val user = postSnapshat.getValue(User::class.java)
                        hashMap.put(user?.nickname!!, user?.userId!!)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // spinner에 목록 추가
    private fun addSpinner() {
        database.child("user")
            .get().addOnSuccessListener {
                val adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    ArrayList<String>())
                adapter.add("선택")
                for (postSnapshat in it.children) {
                    val others = postSnapshat.getValue(User::class.java)
                    if(id != others?.userId!!)
                        adapter.add(others?.nickname!!)
                }
                binding.others.adapter = adapter
            }.addOnFailureListener {
            }
    }

    // 시간표 추가 다이얼로그
    private fun showAddTime() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_time, null)

        with(builder) {
            setTitle("스케줄 추가")
            setPositiveButton("추가"){dialog, which->
                val title = dialogLayout.findViewById<EditText>(R.id.title).text.toString()
                val week = dialogLayout.findViewById<Spinner>(R.id.weekSpinner).selectedItem.toString()
                val startTime = dialogLayout.findViewById<Spinner>(R.id.startTimeSpinner).selectedItem.toString().substring(0,2)
                val endTime = dialogLayout.findViewById<Spinner>(R.id.endTimeSpinner).selectedItem.toString().substring(0,2)
                val timeTableId = weekToEnglish(week) + startTime + endTime

                var isWrong = false
                val span = endTime.toInt() - startTime.toInt()
                for(i:Int in 0 until span){
                    println("schedule[${startTime.toInt()-9+i}][${weekToNumber(week)-1}] = " + schedule[startTime.toInt()-9+i][weekToNumber(week)])
                    if(schedule[startTime.toInt()-9+i][weekToNumber(week)-1] == 1)
                        isWrong = true
                }

                if(span <= 0){
                    Toast.makeText(this@TimeTableActivity, "시간을 잘못 설정했습니다.", Toast.LENGTH_SHORT).show()
                }
                else if(isWrong) {
                    Toast.makeText(this@TimeTableActivity, "해당 시간에 스케줄이 겹칩니다.", Toast.LENGTH_SHORT).show()

                }else{
                    database.child("timetable").child(auth.currentUser?.uid!!).child(timeTableId)
                        .setValue(TimeTable(timeTableId, title, week, startTime, endTime))
                    drawTimeTable()
                    Toast.makeText(this@TimeTableActivity, "스케줄이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("취소"){dialog,which->
                dialog.cancel()
            }
            setView(dialogLayout)
            show()
        }
    }

    // 스케줄 삭제
    private fun showSubTime() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.sub_time, null)
        val subSpinner = dialogLayout.findViewById<Spinner>(R.id.subSpinner)
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            ArrayList<String>())
        val timeId = ArrayList<String>()

        database.child("timetable").child(auth.currentUser?.uid!!)
            .get().addOnSuccessListener {
                for (postSnapshat in it.children) {
                    val time = postSnapshat.getValue(TimeTable::class.java)
                    adapter.add(time?.title + " (" + time?.week + "요일 " + time?.startTime + ":00~" + time?.endTime + ":00)")
                    timeId.add(time?.timeTableId!!)
                }
                subSpinner.adapter = adapter
            }.addOnFailureListener {
            }

        with(builder) {
            setTitle("삭제할 스케줄")
            setPositiveButton("삭제"){dialog, which->
                if(subSpinner.selectedItem.toString() != ""){
                    val position = subSpinner.selectedItemPosition
                    database.child("timetable").child(auth.currentUser?.uid!!).child(timeId[position]).removeValue()

                    val intent = Intent(this@TimeTableActivity, TimeTableActivity::class.java)
                    intent.putExtra("id", auth.currentUser?.uid!!)
                    startActivity(intent)
                    finish()
                }
            }
            setNegativeButton("취소"){dialog,which->
                dialog.cancel()
            }
            setView(dialogLayout)
            show()
        }
    }

    // TextView 설정하고 GridLayout에 추가
    private fun setTextView(title: String, week: String, startTime: String, endTime: String) {
        val gridLayout = binding.gridlayout
        val textView = TextView(this)
        val layoutParams = GridLayout.LayoutParams()
        textView.text = title
        textView.textSize = 10f
        layoutParams.width = 0
        layoutParams.height = 0
        val col = weekToNumber(week)
        val row = startTime.toInt() - 8
        val span = endTime.toInt() - startTime.toInt()
        layoutParams.columnSpec = GridLayout.spec(col)
        layoutParams.rowSpec = GridLayout.spec(row, span)
        layoutParams.setGravity(Gravity.FILL)
        textView.setBackgroundColor(Color.YELLOW)
        gridLayout.addView(textView, layoutParams)
    }

    // DB에 있는 것을 시간표에 그리기
    private fun drawTimeTable(){
        database.child("timetable").child(id)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val time = postSnapshat.getValue(TimeTable::class.java)
                        setTextView(time?.title!!,  time?.week!!, time?.startTime!!, time?.endTime!!)
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
            "월" -> 1
            "화" -> 2
            "수" -> 3
            "목" -> 4
            "금" -> 5
            "토" -> 6
            "일" -> 7
            else -> 0
        }
    }

    override fun onPause() {
        super.onPause()

        overridePendingTransition(0, 0)
    }
}