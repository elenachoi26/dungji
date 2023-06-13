package com.example.dungziproject.navigation

import MemoDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.Answer
import com.example.dungziproject.CommercialActivity
import com.example.dungziproject.HomeAnswerAdapter
import com.example.dungziproject.databinding.FragmentHomeBinding
import com.example.dungziproject.databinding.HomeEmotionItemBinding
import com.example.dungziproject.navigation.model.ItemDialogInterface
import com.example.dungziproject.navigation.model.Question
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), ItemDialogInterface, MemoDialog.MemoDialogListener {
    var binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    var currentUid :String? = null
    private lateinit var database: DatabaseReference
    var ans:ArrayList<Answer> = ArrayList()
    var questionCount = 0
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

        //감정 recyclerview
        binding!!.emotionRecyclerView.adapter = EmotionRecyclerViewAdapter() // 변경: 어댑터 설정
        binding!!.emotionRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        //지역축제
        binding!!.imageView2.setOnClickListener{
            val intent = Intent(context, CommercialActivity::class.java)
            startActivity(intent)
        }

        //지역축제
        binding!!.imageView2.setOnClickListener{
            val intent = Intent(context, CommercialActivity::class.java)
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
                customViewHolder.itemBinding.memoText.text = user.memo
                customViewHolder.itemBinding.memoText.setOnClickListener {
                    showMemoDialog(user)
                }
                customViewHolder.itemBinding.profileImg.setOnClickListener {
                    val dialog = EmoticonDialog(this@HomeFragment)
                    dialog.show(activity?.supportFragmentManager!!, "EmoticonDialog")
                }
            } else {
                customViewHolder.itemBinding.userId.text = user.nickname
                customViewHolder.itemBinding.memoText.text = user.memo
                customViewHolder.itemBinding.profileImg.setOnClickListener {
                    // Handle click on family member's emotionImg
                    val intent = Intent(requireContext(), FamilyProfileActivity::class.java)
                    intent.putExtra("userId", user.userId) // Pass the user ID or any other identifier
                    startActivity(intent)
                }
            }


            val profileResId = resources.getIdentifier("@raw/${user.image}", "raw", requireContext().packageName)
            customViewHolder.itemBinding.profileImg.setImageResource(profileResId)

            val emotionResId = resources.getIdentifier("@raw/${user.feeling}", "raw", requireContext().packageName)
            customViewHolder.itemBinding.emotionImg.setImageResource(emotionResId)
        }

        private fun showMemoDialog(user: User) {
            val dialog = MemoDialog(user.memo) { memo ->
                // Save the updated memo to Firebase
                val userRef = FirebaseDatabase.getInstance().getReference("user")
                userRef.child(user.userId).child("memo").setValue(memo)
                    .addOnSuccessListener {
                        notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        // Memo update failed
                    }
            }
            dialog.show(requireActivity().supportFragmentManager, "MemoDialog")
        }
    }

    override fun onItemSelected(emoticon: String) {
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

    override fun onMemoSaved(memo: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("user")
        currentUid?.let { uid ->
            val currentUserRef = userRef.child(uid)
            currentUserRef.child("memo").setValue(memo)
                .addOnSuccessListener {
                    // 이모티콘 업데이트 성공 시 수행할 작업 추가
                    binding?.emotionRecyclerView?.adapter?.notifyDataSetChanged()
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
                            binding!!.number.text = "0" + q?.questionId!!
                        else
                            binding!!.number.text = q?.questionId!!
                        binding!!.question.text = q?.question!!

                        questionCount++
                    }
                    adapter.notifyDataSetChanged()

                    // 해당 질문의 답변들 가져오기
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
                .addOnFailureListener {
                    // 이모티콘 업데이트 실패 시 수행할 작업 추가
                }
        }
    }
}

