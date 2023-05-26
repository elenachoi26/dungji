package com.example.dungziproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dungziproject.databinding.ActivitySeekPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var binding:ActivitySeekPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeekPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.seekBtn.setOnClickListener {
            val emailAddress = binding.editText.text.toString()

            Firebase.auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this, "이메일을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}