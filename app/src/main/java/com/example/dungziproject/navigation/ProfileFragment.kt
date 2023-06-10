package com.example.dungziproject.navigation

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.dungziproject.EditActivity
import com.example.dungziproject.LoginActivity
import com.example.dungziproject.R
import com.example.dungziproject.databinding.DialogReloginBinding
import com.example.dungziproject.databinding.FragmentProfileBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.checkerframework.checker.units.qual.A
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment :Fragment() {
    var binding: FragmentProfileBinding?=null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        initLayout()
        return binding!!.root
    }

    private fun initLayout() {
        binding!!.logoutBtn.setOnClickListener {
            auth.signOut()
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding!!.editBtn.setOnClickListener{
            //edit profile activity로 넘어가는 intent 구현
            val intent = Intent(requireContext(), EditActivity::class.java)
            startActivity(intent)
        }

        binding!!.deleteBtn.setOnClickListener{
            deleteId()
        }


        GlobalScope.launch(Dispatchers.Main) {
            var userInfo = getUserInfo()
            binding!!.userName.text = userInfo[0]
            binding!!.userNickname.text = userInfo[1]
            binding!!.userEmail.text = userInfo[2]

            val birthdate = userInfo[3]
            val formattedBirthdate = formatBirthdate(birthdate)
            binding!!.userBirthdate.text = formattedBirthdate

            val imagesrc = resources.getIdentifier(userInfo[4], "raw", requireContext().packageName)
            if (imagesrc != 0) {
                binding!!.userProfileImg.setImageResource(imagesrc)
            }
        }
    }

    private fun goToLoginScreen() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)

        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    private fun formatBirthdate(birthdate: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        val date = inputFormat.parse(birthdate)
        return outputFormat.format(date)
    }


    private fun deleteId(){
        val user = FirebaseAuth.getInstance().currentUser

        val dialogBinding = DialogReloginBinding.inflate(layoutInflater)
        val emailEditText = dialogBinding.emailEditText
        val passwordEditText = dialogBinding.passwordEditText

        AlertDialog.Builder(requireContext())
            .setTitle("Reauthenticate")
            .setMessage("계정삭제를 위해 이메일과 비밀번호를 입력해주세요.")
            .setView(dialogBinding.root)
            .setPositiveButton("OK") { dialog, _ ->
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                val credentials = EmailAuthProvider.getCredential(email, password)
                user!!.reauthenticate(credentials)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            showDeleteAccountDialog()
                        } else {
                            Toast.makeText(requireContext(), "Reauthentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("계정탈퇴")
            .setMessage("계정탈퇴는 복구할 수 없습니다. 정말로 계정을 삭제하시겠습니까?")
            .setPositiveButton("ok") { dialog, _ ->
                val user = FirebaseAuth.getInstance().currentUser
                user?.delete()
                    ?.addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            FirebaseDatabase.getInstance().getReference("user").child(user.uid).setValue(null)
                            Toast.makeText(requireContext(), "그동안 둥지를 이용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show()
                            goToLoginScreen()
                        } else {
                            Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
    //dialog 띄우기.

    private suspend fun getUserInfo(): List<String> {
        val usersRef = FirebaseDatabase.getInstance().getReference("user")
        val dataSnapshot = usersRef.child(auth.currentUser!!.uid).get().await()
        var currentNickname = dataSnapshot.child("nickname").getValue(String::class.java).toString()
        var currentName = dataSnapshot.child("name").getValue(String::class.java).toString()
        var currentEmail = dataSnapshot.child("email").getValue(String::class.java).toString()
        var currentBirth = dataSnapshot.child("birth").getValue(String::class.java).toString()
        var currentImg = dataSnapshot.child("image").getValue(String::class.java).toString()
        var userInfo = listOf(currentName, currentNickname, currentEmail,currentBirth , currentImg)

        return userInfo
    }
}