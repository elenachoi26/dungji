package com.example.dungziproject.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dungziproject.MessageAdapter

import com.example.dungziproject.databinding.FragmentChatBinding
import com.example.dungziproject.navigation.model.Message
import com.google.android.play.integrity.internal.c
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment :Fragment() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var chatRoomId: String
    private lateinit var currentUserId: String
    private lateinit var currentUserNickname: String
    private lateinit var currentUserImg: String


    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        loadMessage()
    }

    private fun sending() {
        binding.sendBtn.setOnClickListener {
            val messageText = binding.messageEdit.text.toString()
            val currentTime = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())

            // currentUserNickname 값이 null인 경우 메시지 전송하지 않음
            if (currentUserNickname.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "닉네임을 가져오는 중입니다. 잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val message = Message(messageText,currentUserId, currentTime,currentUserNickname, currentUserImg)
            sendMessage(message)

            hideKeyboard()
        }
    }

    private fun loadMessage() {

        mDbRef.child("chats").child(chatRoomId).child("messages")
            .addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let{
                        messageList.add(it)
                        messageAdapter.notifyItemInserted(messageList.size -1)
                        scrollToBottom()
                        //Keyboard()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        GlobalScope.launch(Dispatchers.Main) {
            var userInfo = getUserInfo()
            currentUserNickname = userInfo[0]
            currentUserImg = userInfo[1]

            sending()
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.messageEdit.windowToken, 0)
    }

    private suspend fun getUserInfo(): ArrayList<String> {
        val usersRef = FirebaseDatabase.getInstance().getReference("user")
        val dataSnapshot = usersRef.child(currentUserId).get().await()
        val userInfo = ArrayList<String>()
        userInfo.add(dataSnapshot.child("nickname").getValue(String::class.java).toString())
        userInfo.add(dataSnapshot.child("image").getValue(String::class.java).toString())

        return userInfo
    }


    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        currentUserId = mAuth.currentUser?.uid ?: ""
        chatRoomId= "YOUR_GROUP_ID"

        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList, currentUserId)

        initlayout()

    }

    private fun initlayout() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.layoutManager = layoutManager
        binding.chatRecyclerView.adapter = messageAdapter
    }

    private fun sendMessage(messageObject: Message) {

        mDbRef.child("chats").child(chatRoomId).child("messages").push().
        setValue(messageObject)
        binding.messageEdit.setText("")

    }

    private fun scrollToBottom() {
        binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }
}
