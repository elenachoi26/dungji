package com.example.dungziproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dungziproject.databinding.ActivityAnswerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AnswerActivity : AppCompatActivity() {
    lateinit var binding: ActivityAnswerBinding
    lateinit var adapter: AnswerAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var data:ArrayList<Answer> = ArrayList()
    private lateinit var questionId:String
    private lateinit var question:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
        initRecyclerView()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference

        questionId = intent.getStringExtra("questionId")!!
        question = intent.getStringExtra("question")!!
        binding.question.setText(question)

        showData(questionId)

        // 답변 추가
        binding.answerBtn.setOnClickListener {
            val answer = binding.answerText.text.toString()

            database.child("user").child(auth.currentUser?.uid!!)
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(Answer::class.java)
                        database.child("answer").child(questionId).child(auth.currentUser?.uid!!).
                            setValue(Answer(user?.nickname!!, answer, auth.currentUser?.uid!!))
                        val intent = Intent(this@AnswerActivity, AnswerActivity::class.java)
                        intent.putExtra("questionId", questionId)
                        intent.putExtra("question", question)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)
                        finish()
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            binding.answerText.text.clear()
        }
    }

    private fun showData(questionId:String){
        database.child("answer").child(questionId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val ans = postSnapshat.getValue(Answer::class.java)
                        data.add(Answer(ans?.nickname!!, ans?.answer!!, ans?.userId!!))
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= AnswerAdapter(data)
        adapter.itemClickListener = object : AnswerAdapter.OnItemClickListener {
            override fun OnItemClick(data: Answer, position: Int) {
                database.child("answer").child(questionId).child(auth.currentUser?.uid!!).removeValue()
                val intent = Intent(this@AnswerActivity, AnswerActivity::class.java)
                intent.putExtra("questionId", questionId)
                intent.putExtra("question", question)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
        }
        binding.RecyclerView.adapter = adapter
    }

    // 엑티비티 변환시 에니메이션 제거
    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}