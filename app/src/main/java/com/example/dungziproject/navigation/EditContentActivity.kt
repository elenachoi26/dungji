package com.example.dungziproject.navigation

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dungziproject.databinding.ActivityEditcommentBinding
import com.example.dungziproject.navigation.model.ContentDTO
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EditContentActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditcommentBinding
    var contentUid: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditcommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentUid = intent.getStringExtra("contentUid")

        loadContent()

        binding.saveBtn.setOnClickListener {
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
                    binding.editContentEdittext.setText(it.explain)
                }
            }
    }

    private fun updateContent(){
        val editContent = binding.editContentEdittext.text.toString()

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