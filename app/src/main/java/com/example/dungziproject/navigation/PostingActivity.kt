package com.example.dungziproject.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dungziproject.R
import com.example.dungziproject.databinding.ActivityPostingBinding
import com.example.dungziproject.navigation.model.ContentDTO
import com.example.dungziproject.navigation.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class PostingActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostingBinding
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage?=null
    var photoUri : Uri? = null
    private lateinit var auth: FirebaseAuth
    var firestore : FirebaseFirestore?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout(){
        storage = FirebaseStorage.getInstance()
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        binding.uploadCancelBtn.setOnClickListener{
            finish()
        }

        binding.addphotoUploadBtn.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                binding.uploadImageview.setImageURI(photoUri)
            }else{
                finish()
            }
        }
    }

    fun contentUpload() {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            val contentDTO = ContentDTO()

            contentDTO.imgUrl = uri.toString()
            contentDTO.userId = auth.currentUser?.uid
            contentDTO.explain = binding.addphotoEditEdittext.text.toString()
            contentDTO.timestamp = System.currentTimeMillis()

            val userRef = FirebaseDatabase.getInstance().getReference("user").child(auth.currentUser?.uid ?: "")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    val nickname = user?.nickname

                    contentDTO.nickname = nickname
                    Log.d("ContentUpload", "User nickname: $nickname") // 로그 출력

                    firestore?.collection("images")?.document()?.set(contentDTO)
                        ?.addOnSuccessListener {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}