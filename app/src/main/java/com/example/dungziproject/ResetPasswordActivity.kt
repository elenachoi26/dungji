package com.example.dungziproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dungziproject.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var binding:ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {

        // 이메일 보내기 버튼
        binding.sendEmailBtn.setOnClickListener {
            val emailAddress = binding.editText.text.toString()

            Firebase.auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {    // 이메일 보내기 성공
                        Toast.makeText(this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{                      // 이메일 보내기 실패
                        Toast.makeText(this, "이메일을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}