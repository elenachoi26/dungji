package com.example.dungziproject.navigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.dungziproject.LoginActivity
import com.example.dungziproject.Question
import com.example.dungziproject.QuestionActivity
import com.example.dungziproject.TimeTableActivity
import com.example.dungziproject.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment :Fragment() {
    var binding: FragmentHomeBinding?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    val number:ArrayList<Int> = ArrayList()
    val questionCount = 28

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        database = Firebase.database.reference

        showQuestion()

        binding!!.logoutBtn.setOnClickListener {
            auth.signOut()
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding!!.linearlayout.setOnClickListener{
            val intent = Intent(context, QuestionActivity::class.java)
            startActivity(intent)
        }

        return binding!!.root
    }

    private fun showQuestion() {
        var i = 0
        while(true){
            val num = Random().nextInt(questionCount) + 1
            if(!number.contains(num)) {
                number.add(num)
                i++
            }
            if(i == 3) break
        }
        number.sort()

        database.child("question").child(number[0].toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ques = snapshot.getValue(Question::class.java)
                    binding!!.n1.text = "#" + ques?.questionId!!
                    binding!!.t1.text = ques?.question!!
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        database.child("question").child(number[1].toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ques = snapshot.getValue(Question::class.java)
                    binding!!.n2.text = "#" + ques?.questionId!!
                    binding!!.t2.text = ques?.question!!
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        database.child("question").child(number[2].toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ques = snapshot.getValue(Question::class.java)
                    binding!!.n3.text = "#" + ques?.questionId!!
                    binding!!.t3.text = ques?.question!!
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}