package com.example.dungziproject.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dungziproject.AnswerActivity
import com.example.dungziproject.databinding.ActivityQuestionBinding
import com.example.dungziproject.navigation.model.Question
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class QuestionActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuestionBinding
    private var ques:ArrayList<Question> = ArrayList()
    lateinit var adapter: QuestionAdapter
    var nickname = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
        initData()
        getNickname()
        initRecyclerView()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    private fun initData() {
        database.child("question")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val q = postSnapshot.getValue(Question::class.java)
                        ques.add(Question(q?.questionId!!, q?.question!!))
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // 닉네임 가져오기
    private fun getNickname() {
        database.child("user").child(auth.currentUser?.uid!!)
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        nickname = user?.nickname!!
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= QuestionAdapter(ques)
        adapter.itemClickListener=object : QuestionAdapter.OnItemClickListener {
            override fun OnItemClick(data: Question, position: Int) {
                val intent = Intent(this@QuestionActivity, AnswerActivity::class.java)
                intent.putExtra("questionId", data.questionId)
                intent.putExtra("question", data.question)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
            }

        }
        binding.RecyclerView.adapter = adapter
    }
}