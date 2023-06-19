package com.example.dungziproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dungziproject.databinding.ActivityEditBinding
import com.example.dungziproject.databinding.DialogReloginBinding
import com.example.dungziproject.navigation.model.ItemDialogInterface
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

@Suppress("DEPRECATION")
class EditActivity : AppCompatActivity(), ItemDialogInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var binding: ActivityEditBinding
    private var setImage:String? = "grandmother"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)

        initLayout()
        setContentView(binding.root)
    }

    private fun initLayout() {
        auth = Firebase.auth
        database = Firebase.database.reference

        GlobalScope.launch(Dispatchers.Main) {
            initTextView()
        }

        // 이미지 선택 선택시
        binding.editProfileImg.setOnClickListener{

            val dialog = ProfileImageDialog(this, true)

            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "EmoticonDialog")
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        //저장 버튼 클릭시
        binding.saveBtn.setOnClickListener {
            var currentUserId = auth.currentUser?.uid
            var email = binding.emailEdit.text.toString()

            var name = binding.nameEdit.text.toString()
            var year = binding.yearSpinner.selectedItem.toString()
            var month = binding.monthSpinner.selectedItem.toString()
            var day = binding.daySpinner.selectedItem.toString()
            var birth = year + month + day
            var nickname = binding.nicknameEdit.text.toString()
            var image = setImage!!

            val usersRef = FirebaseDatabase.getInstance().getReference("user")
            if (currentUserId != null) {
                usersRef.child(currentUserId).get().addOnSuccessListener { dataSnapshot ->
                    val previousFeeling = dataSnapshot.child("feeling").getValue(String::class.java)
                    val previousMemo = dataSnapshot.child("memo").getValue(String::class.java)

                    val updateUser = User(currentUserId, email, name, birth, nickname, image, previousFeeling!!, previousMemo!!)

                    reAuthentication(updateUser)

                }.addOnFailureListener { exception ->
                    // 사용자 정보 가져오기 실패
                    Toast.makeText(this, "Failed to get user information: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun reCalendar(name: String, birth: String) {
        //캘린더의 원래 생일 삭제 및 업데이트된 생일 추가
        // Firebase 데이터베이스 업데이트
        val firestore = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val updatebirthmonth = birth.substring(4,6).toInt()
        val updatebirthday = birth.substring(6,8).toInt()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val usersRef = FirebaseDatabase.getInstance().getReference("user")
        var currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            usersRef.child(currentUserId).get().addOnSuccessListener { dataSnapshot ->
                val currentname = dataSnapshot.child("name").getValue(String::class.java)
                val query = firestore?.collection("calendars")
                    ?.whereEqualTo("event", "${currentname}님의 생일")
                    ?.whereEqualTo("start_time", "00:00")
                    ?.whereEqualTo("end_time", "23:59")
                    ?.whereEqualTo("place", "모두들 축하해주세요!")

                query?.get()
                    ?.addOnSuccessListener { querySnapshot ->
                        for (documentSnapshot in querySnapshot.documents) {
                            val documentId = documentSnapshot.id
                            firestore?.collection("calendars")?.document(documentId)
                                ?.update("month", updatebirthmonth)
                            firestore?.collection("calendars")?.document(documentId)
                                ?.update("day", updatebirthday)
                            firestore?.collection("calendars")?.document(documentId)
                                ?.update("event","${name}님의 생일")
                        }

                    }
                    ?.addOnFailureListener { exception ->
                        // 삭제 작업 실패 시 에러 처리
                    }
            }.addOnFailureListener { exception ->
                // 사용자 정보 가져오기 실패
                Toast.makeText(this, "Failed to get user information: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun reAuthentication(updatingUser :User) {

        val currentUser = FirebaseAuth.getInstance().currentUser

        val dialogBinding = DialogReloginBinding.inflate(layoutInflater)
        val emailEditText = dialogBinding.emailEditText
        val passwordEditText = dialogBinding.passwordEditText

        AlertDialog.Builder(this)
            .setTitle("Reauthenticate")
            .setMessage("프로필 수정을 위해 기존에 사용하던 이메일과 비밀번호를 입력해주세요.")
            .setView(dialogBinding.root)
            .setPositiveButton("OK") { dialog, _ ->
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                val credentials = EmailAuthProvider.getCredential(email, password)
                currentUser!!.reauthenticate(credentials)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            reCalendar(updatingUser.name,updatingUser.birth)
                            updateInfo(updatingUser)
                        } else {
                            Toast.makeText(this, "Reauthentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private suspend fun initTextView() {
        val usersRef = FirebaseDatabase.getInstance().getReference("user")
        val dataSnapshot = usersRef.child(auth.currentUser!!.uid).get().await()
        binding.nicknameEdit.setText(dataSnapshot.child("nickname").getValue(String::class.java).toString())
        binding.emailEdit.setText(dataSnapshot.child("email").getValue(String::class.java).toString())
        binding.nameEdit.setText(dataSnapshot.child("name").getValue(String::class.java).toString())
        var imgsrc = dataSnapshot.child("image").getValue(String::class.java).toString()
        val imagesrc = resources.getIdentifier(imgsrc, "raw", packageName)
        if (imagesrc != 0) {
            binding.editProfileImg.setImageResource(imagesrc)
            setImage = imgsrc
        }

        var birth = dataSnapshot.child("birth").getValue(String::class.java).toString()
        binding.yearSpinner.setSelection(birth.chunked(4)[0].toInt()-1900)
        binding.monthSpinner.setSelection(birth.chunked(2)[2].toInt()-1)
        binding.daySpinner.setSelection(birth.chunked(2)[3].toInt()-1)
    }

    private fun updateInfo(user:User) {
        database.child("user").child(user.userId).setValue(user)

        val currentUser = auth.currentUser
        currentUser?.updateEmail(user.email)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "프로필 변경 완료", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.i("error", task.exception.toString())
                Toast.makeText(this, "프로필 변경 실패", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onItemSelected(item: String) {
        var resId = resources.getIdentifier("@raw/" + item, "raw", packageName)
        setImage = item
        binding.editProfileImg.setImageResource(resId)
    }

}