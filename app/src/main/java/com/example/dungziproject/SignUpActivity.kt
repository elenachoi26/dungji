package com.example.dungziproject

import android.app.Activity
import android.content.Intent
import android.view.View
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.dungziproject.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var setImage:String? = "grandmother"
    private var feeling = ""
    private var dupNick:ArrayList<String> = ArrayList()
    
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
        binding.imageView.setOnClickListener{
            val intent = Intent(this, ImageActivity::class.java)
            startActivityForResult(intent, 0)
        }

        getNickname()

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

            if (email == "") {
                binding.nullEmailText.visibility = View.VISIBLE
                binding.wrongEmailText.visibility = View.GONE
                binding.nullPasswordText.visibility = View.GONE
                binding.wrongPasswordText.visibility = View.GONE
                binding.nullNameText.visibility = View.GONE
                binding.nullNicknameText.visibility = View.GONE
                binding.duplicateNicknameText.visibility = View.GONE
            } else if (!email.contains('@')) {
                binding.nullEmailText.visibility = View.GONE
                binding.wrongEmailText.visibility = View.VISIBLE
                binding.nullPasswordText.visibility = View.GONE
                binding.wrongPasswordText.visibility = View.GONE
                binding.nullNameText.visibility = View.GONE
                binding.nullNicknameText.visibility = View.GONE
                binding.duplicateNicknameText.visibility = View.GONE
            } else if (password == "") {
                binding.nullEmailText.visibility = View.GONE
                binding.wrongEmailText.visibility = View.GONE
                binding.nullPasswordText.visibility = View.VISIBLE
                binding.wrongPasswordText.visibility = View.GONE
                binding.nullNameText.visibility = View.GONE
                binding.nullNicknameText.visibility = View.GONE
                binding.duplicateNicknameText.visibility = View.GONE
            } else if (password.length < 6) {
                binding.nullEmailText.visibility = View.GONE
                binding.wrongEmailText.visibility = View.GONE
                binding.nullPasswordText.visibility = View.GONE
                binding.wrongPasswordText.visibility = View.VISIBLE
                binding.nullNameText.visibility = View.GONE
                binding.nullNicknameText.visibility = View.GONE
                binding.duplicateNicknameText.visibility = View.GONE
            } else if (name == "") {
                binding.nullEmailText.visibility = View.GONE
                binding.wrongEmailText.visibility = View.GONE
                binding.nullPasswordText.visibility = View.GONE
                binding.wrongPasswordText.visibility = View.GONE
                binding.nullNameText.visibility = View.VISIBLE
                binding.nullNicknameText.visibility = View.GONE
                binding.duplicateNicknameText.visibility = View.GONE
            } else if (nickname == "") {
                binding.nullEmailText.visibility = View.GONE
                binding.wrongEmailText.visibility = View.GONE
                binding.nullPasswordText.visibility = View.GONE
                binding.wrongPasswordText.visibility = View.GONE
                binding.nullNameText.visibility = View.GONE
                binding.nullNicknameText.visibility = View.VISIBLE
                binding.duplicateNicknameText.visibility = View.GONE
            } else if(dupNick.contains(binding.nicknameEdit.text.toString())) {
                binding.nullEmailText.visibility = View.GONE
                binding.wrongEmailText.visibility = View.GONE
                binding.nullPasswordText.visibility = View.GONE
                binding.wrongPasswordText.visibility = View.GONE
                binding.nullNameText.visibility = View.GONE
                binding.nullNicknameText.visibility = View.GONE
                binding.duplicateNicknameText.visibility = View.VISIBLE
            } else {
                binding.nullEmailText.visibility = View.GONE
                binding.wrongEmailText.visibility = View.GONE
                binding.nullPasswordText.visibility = View.GONE
                binding.wrongPasswordText.visibility = View.GONE
                binding.nullNameText.visibility = View.GONE
                binding.nullNicknameText.visibility = View.GONE
                binding.duplicateNicknameText.visibility = View.GONE
                signUp(email, password, name, birth, nickname, image)
            }
        }
    }

    private fun getNickname() {
        database.child("user")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
//                        val user = postSnapshat.getValue(User::class.java)
                        val user = postSnapshat.getValue(com.example.dungziproject.User::class.java)
                        dupNick.add(user?.nickname!!)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // 회원가입 기능
    private fun signUp(email: String, password: String, name: String, birth: String, nickname: String, image: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {    // 회원가입 성공
                    Toast.makeText(this, "회원가입 완료. 로그인 해주세요!", Toast.LENGTH_SHORT).show()
                    addUserToDatabase(
                        auth.currentUser?.uid!!,
                        email,
                        name,
                        birth,
                        nickname,
                        image,
                        feeling
                    )

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
        feeling: String
    ) {
        database.child("user").child(userId).setValue(User(userId, email, name, birth, nickname, image, feeling))
    }

    // ImageActivity에서 이미지 String 받아오기

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0) {
            if(resultCode == Activity.RESULT_OK) {
                setImage = data?.getStringExtra("image")

                var resId = resources.getIdentifier("@raw/" + setImage, "raw", packageName)
                binding.imageView.setImageResource(resId)
            }
        }
    }
}