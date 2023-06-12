package com.example.dungziproject.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dungziproject.databinding.ActivityFamilyProfileBinding
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class FamilyProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityFamilyProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout(){
        auth = Firebase.auth
        val userId = intent.getStringExtra("userId")

        databaseReference = FirebaseDatabase.getInstance().getReference("user")

        databaseReference.child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    binding.userEmail.text = it.email
                    binding.userName.text = it.name
                    binding.userNickname.text = it.nickname
                    binding.userBirthdate.text = it.birth
                    val resId = resources.getIdentifier("@raw/" + it.image, "raw", packageName)
                    binding.userProfileImg.setImageResource(resId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }
}




