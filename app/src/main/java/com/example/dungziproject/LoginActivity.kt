package com.example.dungziproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.dungziproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth

        if(auth.currentUser?.uid != null && auth.currentUser?.uid != "Pa6tQ8eYcIWkWVuuEEBACxxgPxn2"){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 로그인 버튼 클릭시
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()


            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!email.contains('@') || !email.contains('.')) {
                Toast.makeText(this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6){
                Toast.makeText(this, "비밀번호는 6자 이상입니다.", Toast.LENGTH_SHORT).show()
            }else {
                login(email, password)
            }
        }

        // 비밀번호 재설정 선택시
        binding.resetPassword.setOnClickListener{
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
            clearInput()
        }

        // 회원가입 선택시
        binding.signup.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)

            startActivity(intent)
            clearInput()
        }
    }


    // 로그인 기능
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {    // 로그인 성공
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {                    // 로그인 실패
                    Toast.makeText(this, "                   로그인 실패\n아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }

            }
    }


    // 로그인 editText 비우기
    fun clearInput(){
        binding.apply{
            emailEdit.text.clear()
            passwordEdit.text.clear()
        }
    }
}