package com.example.dungziproject.navigation

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dungziproject.databinding.ActivityPostingBinding
import com.example.dungziproject.navigation.model.ContentDTO
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EditContentActivity: AppCompatActivity() {
    lateinit var binding: ActivityPostingBinding
    var contentUid: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentUid = intent.getStringExtra("contentUid")

        loadContent()

        binding.addphotoUploadBtn.setOnClickListener {
            updateContent()
        }
    }

    private fun loadContent(){
        FirebaseFirestore.getInstance()
            .collection("images")
            .document(contentUid!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val content = documentSnapshot.toObject(ContentDTO::class.java)
                content?.let {
                    binding.addphotoEditEdittext.setText(it.explain)
                    Glide.with(this)
                        .load(content.imgUrl)
                        .into(binding.uploadImageview)
                }
            }
    }

    private fun updateContent(){
        val editContent = binding.addphotoEditEdittext.text.toString()

        val contentMap = hashMapOf<String, Any>(
            "explain" to editContent
        )

        FirebaseFirestore.getInstance()
            .collection("images")
            .document(contentUid!!)
            .update(contentMap)
            .addOnSuccessListener {
                finish()
            }
    }

}