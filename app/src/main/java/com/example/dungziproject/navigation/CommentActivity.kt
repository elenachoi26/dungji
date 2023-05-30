package com.example.dungziproject.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dungziproject.databinding.ActivityCommentBinding
import com.example.dungziproject.databinding.AlbumCommentItemBinding
import com.example.dungziproject.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentBinding
    var contentUid : String?= null
    var contentDTO: ContentDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout(){
        contentUid = intent.getStringExtra("contentUid")

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
                        val writerId = contentDTO!!.userId
                        binding.writerIdTV.text = writerId

                        val timestamp = contentDTO?.timestamp
                        val uploadTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                            timestamp?.let { Date(it) })
                        binding.ImgUploadTimeTV.text = uploadTime
                    }
            }


        //comment
        binding.commentRecyclerview.adapter = CommentRecyclerViewAdapter()
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.sendBtn.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = binding.commentEdittext.text.toString()
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
            binding.commentEdittext.setText("")
        }
    }

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
            customViewHolder.itemBinding.profileTextview.text = comment.userId

            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comment.uid!!)
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        var url = task.result!!["image"]
                        Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop()).into(customViewHolder.itemBinding.profileImageview)
                    }
                }

        }



    }

}