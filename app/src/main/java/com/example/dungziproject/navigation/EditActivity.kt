package com.example.dungziproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.dungziproject.databinding.ActivityCalendar1Binding
import com.example.dungziproject.databinding.ActivityEditBinding
import com.example.dungziproject.databinding.DialogReloginBinding
import com.example.dungziproject.navigation.ProfileFragment
import com.example.dungziproject.navigation.model.ItemDialogInterface
import com.example.dungziproject.navigation.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.checkerframework.checker.units.qual.s

@Suppress("DEPRECATION")
class EditActivity : AppCompatActivity(), ItemDialogInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var binding: ActivityEditBinding
    private var setImage:String? = "grandmother"
    private var feeling = ""
    private var memo = ""


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
            val dialog = ProfileImageDialog(this)
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

            var updateUser = User(currentUserId!!,email,name, birth, nickname, image, feeling, memo)

            reAuthentication(updateUser)
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