package com.example.dungziproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dungziproject.databinding.ActivityQuestionBinding
import com.google.android.play.integrity.internal.x
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class QuestionActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuestionBinding
    private var data:ArrayList<Question> = ArrayList()
    lateinit var adapter: QuestionAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRecyclerView()
    }

    private fun initData() {
        database = Firebase.database.reference

        database.child("question")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val ques = postSnapshat.getValue(Question::class.java)
                        data.add(Question(ques?.questionId!!, ques?.question!!))
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun initRecyclerView() {
        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter= QuestionAdapter(data)
        adapter.itemClickListener=object : QuestionAdapter.OnItemClickListener{
            override fun OnItemClick(data: Question, position: Int) {
                val intent = Intent(this@QuestionActivity, AnswerActivity::class.java)
                intent.putExtra("questionId", data.questionId)
                intent.putExtra("question", data.question)
                startActivity(intent)
            }

        }
        binding.RecyclerView.adapter = adapter
    }
}