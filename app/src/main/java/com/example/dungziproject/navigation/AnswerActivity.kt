package com.example.dungziproject

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
    private var ans:ArrayList<Answer> = ArrayList()
    private lateinit var questionId:String
    private lateinit var question:String
    private lateinit var nickname:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
        initData()
        initRecyclerView()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference

        questionId = intent.getStringExtra("questionId")!!
        question = intent.getStringExtra("question")!!
        nickname = intent.getStringExtra("nickname")!!

        binding.question.text = question

        // 답변 추가
        binding.answerBtn.setOnClickListener {
            val answer = binding.answerText.text.toString()

            if(ans.isEmpty()){
                database.child("answer").child(questionId).child("1").
                setValue(Answer(nickname, answer, auth.currentUser?.uid!!, questionId, "1"))
            }else{
                var lastAnswerId = ans.last().answerId.toInt() + 1
                database.child("answer").child(questionId).child(lastAnswerId.toString()).
                setValue(Answer(nickname, answer, auth.currentUser?.uid!!, questionId, lastAnswerId.toString()))
            }

            binding.answerText.setText("")
        }
    }

    private fun initData() {
            database.child("answer").child(questionId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        ans.clear()
                        for (postSnapshot in snapshot.children) {
                            var a = postSnapshot.getValue(Answer::class.java)
                            ans.add(Answer(a?.nickname!!, a?.answer!!, a?.userId!!, a?.questionId!!, a?.answerId!!))
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
        adapter= AnswerAdapter(ans)
        adapter.itemClickListener = object : AnswerAdapter.OnItemClickListener {
            override fun OnItemClick(data: Answer, position: Int) {
                ans.removeAt(position)
                database.child("answer").child(questionId).child(data.answerId).removeValue()
            }
        }
        binding.RecyclerView.adapter = adapter
    }
}