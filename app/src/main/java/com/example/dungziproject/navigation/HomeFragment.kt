package com.example.dungziproject.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.example.dungziproject.*
import com.example.dungziproject.EmoticonDialog
import com.example.dungziproject.databinding.FragmentHomeBinding
import com.example.dungziproject.databinding.HomeEmotionItemBinding
import com.example.dungziproject.navigation.model.EmoticonDialogInterface
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.checkerframework.checker.units.qual.A
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), EmoticonDialogInterface {
    var binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    var currentUid :String? = null
    private lateinit var database: DatabaseReference
    var ans:ArrayList<Answer> = ArrayList()
    var questionCount = 28
    lateinit var adapter: HomeAnswerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        userRef = FirebaseDatabase.getInstance().getReference("user")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        currentUid = FirebaseAuth.getInstance().currentUser?.uid
        database = Firebase.database.reference

        initData()
        initRecyclerView()

        //감정 recyclerview
        binding!!.emotionRecyclerView.adapter = EmotionRecyclerViewAdapter() // 변경: 어댑터 설정
        binding!!.emotionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // 가족백과
        binding!!.linearlayout.setOnClickListener{
            val intent = Intent(context, QuestionActivity::class.java)
            startActivity(intent)
        }

        return binding!!.root
    }

    inner class EmotionRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val userList: ArrayList<User> = arrayListOf()

        init {
            val usersRef = FirebaseDatabase.getInstance().getReference("user")
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userList.clear()
                    if (currentUid != null) {
                        val currentUserSnapshot = dataSnapshot.child(currentUid!!)
                        val currentUserData = currentUserSnapshot.getValue(User::class.java)
                        currentUserData?.let {
                            userList.add(it)
                        }
                    }

                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            if (currentUid == null || it.userId != currentUid) {
                                userList.add(it)
                            }
                        }
                    }

                    notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        inner class CustomViewHolder(val itemBinding: HomeEmotionItemBinding) : RecyclerView.ViewHolder(itemBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemBinding = HomeEmotionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val user = userList[position]
            val customViewHolder = holder as CustomViewHolder

            if (user.userId == currentUid) {
                customViewHolder.itemBinding.userId.text = "나"
                customViewHolder.itemBinding.emotionImg.setOnClickListener {
                    val dialog = EmoticonDialog(this@HomeFragment)
                    dialog.isCancelable = false
                    dialog.show(activity?.supportFragmentManager!!, "EmoticonDialog")
                }
            } else {
                customViewHolder.itemBinding.userId.text = user.nickname
            }


            val profileResId = resources.getIdentifier("@raw/${user.image}", "raw", requireContext().packageName)
            customViewHolder.itemBinding.profileImg.setImageResource(profileResId)

            val emotionResId = resources.getIdentifier("@raw/${user.feeling}", "raw", requireContext().packageName)
            customViewHolder.itemBinding.emotionImg.setImageResource(emotionResId)
        }
    }

    override fun onEmoticonSelected(emoticon: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("user")
        currentUid?.let { uid ->
            val currentUserRef = userRef.child(uid)
            currentUserRef.child("feeling").setValue(emoticon)
                .addOnSuccessListener {
                    // 이모티콘 업데이트 성공 시 수행할 작업 추가
                }
                .addOnFailureListener {
                    // 이모티콘 업데이트 실패 시 수행할 작업 추가
                }
        }
    }

    // DB에서 질문, 답변 가져오고 가장 최신꺼 화면에 출력
    private fun initData() {
        database.child("question")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val q = postSnapshot.getValue(Question::class.java)
                        if(q?.questionId!!.toInt() < 10)
                            binding!!.number.text = "#0" + q?.questionId!!
                        else
                            binding!!.number.text = "#" + q?.questionId!!
                        binding!!.question.text = q?.question!!
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        database.child("answer").child(questionCount.toString())
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
        binding!!.RecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter= HomeAnswerAdapter(ans)
        adapter.itemClickListener = object : HomeAnswerAdapter.OnItemClickListener {
            override fun OnItemClick(data: Answer, position: Int) {
                val intent = Intent(context, QuestionActivity::class.java)
                startActivity(intent)
            }
        }
        binding!!.RecyclerView.adapter = adapter
    }
}