package com.example.dungziproject

import android.app.Activity
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dungziproject.databinding.ActivitySignupBinding
import com.example.dungziproject.navigation.model.ItemDialogInterface
import com.example.dungziproject.ProfileImageDialog
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() , ItemDialogInterface {
    lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var setImage:String? = "grandmother"
    private var feeling = ""
    private var memo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference


        // 이미지 선택 선택시
        binding.profileImg.setOnClickListener{
//            val intent = Intent(this, ImageActivity::class.java)
//            startActivityForResult(intent, 0)
            val dialog = ProfileImageDialog(this)
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "EmoticonDialog")
        }


        // 회원가입 버튼 클릭시
        binding.signupBtn.setOnClickListener {
            var email = binding.emailEdit.text.toString()
            var password = binding.passwordEdit.text.toString()
            var name = binding.nameEdit.text.toString()
            var year = binding.yearSpinner.selectedItem.toString()
            var month = binding.monthSpinner.selectedItem.toString()
            var day = binding.daySpinner.selectedItem.toString()
            var birth = year + month + day
            var nickname = binding.nicknameEdit.text.toString()

            var image = setImage!!

            signUp(email, password, name, birth, nickname, image)
        }
    }

    // 회원가입 기능
    private fun signUp(email: String, password: String, name: String, birth: String, nickname: String, image: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {    // 회원가입 성공
                    Toast.makeText(this, "회원가입 완료. 로그인 해주세요!", Toast.LENGTH_SHORT).show()
                    addUserToDatabase(auth.currentUser?.uid!!, email, name, birth, nickname, image, feeling, memo)
                    finish()
                } else {                    // 회원가입 실패
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }

            }
    }

    // DB user 테이블에 회원가입 정보 저장
    private fun addUserToDatabase(
        userId: String,
        email: String,
        name: String,
        birth: String,
        nickname: String,
        image: String,
        feeling: String,
        memo: String
    ) {
        database.child("user").child(userId).setValue(User(userId, email, name, birth, nickname, image, feeling, memo))
    }

    // ImageActivity에서 이미지 String 받아오기

    override fun onItemSelected(item: String) {
        var resId = resources.getIdentifier("@raw/" + item, "raw", packageName)
        binding.profileImg.setImageResource(resId)
    }
}