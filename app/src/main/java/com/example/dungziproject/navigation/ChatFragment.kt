package com.example.dungziproject.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dungziproject.Message
import com.example.dungziproject.MessageAdapter

import com.example.dungziproject.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment :Fragment() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var chatRoomId: String
    private lateinit var currentUserId: String

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
//        val intent = Intent(activity, ChatActivity::class.java) // Intent 객체를 생성하고 ChatActivity를 대상으로 지정합니다.
//        startActivity(intent) // 생성한 Intent를 사용하여 ChatActivity를 시작합니다.

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initlayout()
        loadMessage()
        sending()
    }

    private fun sending() {
        binding.sendBtn.setOnClickListener {
            Toast.makeText(getActivity(), "send message", Toast.LENGTH_SHORT).show()
            val messageText = binding.messageEdit.text.toString()
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val message = Message(messageText, currentUserId, currentTime)
            sendMessage(message)
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
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        currentUserId = mAuth.currentUser?.uid ?: ""
        chatRoomId= "YOUR_GROUP_ID"

        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList, currentUserId)

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
}
