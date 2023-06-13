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
    private var setImage:String? = ""
    private var feeling = ""
    private var memo = ""
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
        binding.profileImg.setOnClickListener{
//            val intent = Intent(this, ImageActivity::class.java)
//            startActivityForResult(intent, 0)
            val dialog = ProfileImageDialog(this, true)
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

            if (email == ""){
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (!(email.contains('@') && email.contains('.'))) {
                Toast.makeText(this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT).show()
            } else if (password == "") {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "비밀번호는 6자 이상입니다..", Toast.LENGTH_SHORT).show()
            } else if (name == "") {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (nickname == "") {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if(dupNick.contains(binding.nicknameEdit.text.toString())) {
                Toast.makeText(this, "이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show()
            } else {
                signUp(email, password, name, birth, nickname, image)
            }
        }
    }

    // 닉네임 받아오기
    private fun getNickname() {
        database.child("user")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshat in snapshot.children){
                        val user = postSnapshat.getValue(User::class.java)
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
                    addUserToDatabase(auth.currentUser?.uid!!, email, name, birth, nickname, image, feeling, memo)
                    finish()
                } else {                    // 회원가입 실패
                    Toast.makeText(this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show()
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