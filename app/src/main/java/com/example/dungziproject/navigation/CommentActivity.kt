package com.example.dungziproject.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dungziproject.R
import com.example.dungziproject.databinding.ActivityCommentBinding
import com.example.dungziproject.databinding.AlbumCommentItemBinding
import com.example.dungziproject.navigation.model.ContentDTO
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentBinding
    var contentUid : String?= null
    var contentDTO: ContentDTO? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout(){
        auth = Firebase.auth
        contentUid = intent.getStringExtra("contentUid")

        // Load content
        FirebaseFirestore.getInstance()
            .collection("images")
            .document(contentUid!!)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    contentDTO = task.result?.toObject(ContentDTO::class.java)
                    if (contentDTO != null) {
                        Glide.with(this).load(contentDTO!!.imgUrl)
                            .apply(RequestOptions().centerCrop())
                            .into(binding.contentImg)
                    }
                }
                if(contentDTO == null){
                    contentDTO = ContentDTO()
                }
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(contentUid!!)
                    .get()
                    .addOnSuccessListener { userSnapshot ->
                        binding.writerIdTV.text = contentDTO!!.nickname
                        val userRef =
                            FirebaseDatabase.getInstance().getReference("user").child(contentDTO?.userId ?: "")
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val user = dataSnapshot.getValue(User::class.java)
                                var resId = resources.getIdentifier("@drawable/" + user?.image, "drawable", packageName)
                                binding.profileImageview.setImageResource(resId)
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // 처리할 작업을 추가하세요
                            }

                        })
                        val timestamp = contentDTO?.timestamp
                        val uploadTime = SimpleDateFormat("yy.MM.dd HH:mm", Locale.getDefault()).format(
                            timestamp?.let { Date(it) })
                        binding.ImgUploadTimeTV.text = uploadTime

                        if (auth.currentUser?.uid == contentDTO?.userId) {
                            // 현재 사용자와 게시물 업로드 사용자가 같은 경우에만 팝업 메뉴 보여주기
                            binding.contentPopup.visibility = View.VISIBLE
                            binding.contentPopup.setImageResource(R.drawable.ic_popupmenu)
                            binding.contentPopup.setOnClickListener {
                                showPopup(it)
                            }
                        } else {
                            // 다른 경우 팝업 메뉴 비활성화
                            binding.contentPopup.visibility = View.GONE
                        }
                    }
            }


        //Comment
        binding.commentRecyclerview.adapter = CommentRecyclerViewAdapter()
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.sendBtn.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.uid

            comment.comment = binding.commentEdittext.text.toString()
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
            binding.commentEdittext.setText("")
        }
    }

    private fun showPopup(view : View) {
        val popupMenu = PopupMenu(this@CommentActivity, view)
        popupMenu.inflate(R.menu.content_popup)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {

                }
                R.id.action_delete -> {
                    deleteContent()
                }
            }
            false
        }
        popupMenu.show()
    }

    fun deleteContent(){
        FirebaseFirestore.getInstance()
            .collection("images")
            .document(contentUid!!)
            .delete()
            .addOnSuccessListener {
                // 삭제 성공
                finish() // 액티비티 종료 또는 필요한 작업 수행
            }
    }


    //Comment RecyclerView
    inner class CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()
        init {
            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFilestoreException ->
                    comments.clear()
                    if(querySnapshot == null) return@addSnapshotListener

                    for(snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    updateCommentCount(comments.size)
                    notifyDataSetChanged()
                }
        }

        private fun updateCommentCount(count: Int) {
            contentDTO?.commentCount = count
            contentDTO?.let {
                FirebaseFirestore.getInstance()
                    .collection("images")
                    .document(contentUid!!)
                    .set(it)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemBinding = AlbumCommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(itemBinding)
        }

        private inner class CustomViewHolder(val itemBinding: AlbumCommentItemBinding) : RecyclerView.ViewHolder(itemBinding.root)


        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val customViewHolder = holder as CustomViewHolder
            val comment = comments[position]

            customViewHolder.itemBinding.messageTextview.text = comment.comment
            val userRef =
                FirebaseDatabase.getInstance().getReference("user").child(comment.userId ?: "")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    customViewHolder.itemBinding.profileTextview.text = user?.nickname // 수정된 부분
                    var resId = resources.getIdentifier("@drawable/" + user?.image, "drawable", packageName)
                    customViewHolder.itemBinding.profileImageview.setImageResource(resId)
                }
                override fun onCancelled(error: DatabaseError) {
                    // 처리할 작업을 추가하세요
                }
            })
        }
    }
}