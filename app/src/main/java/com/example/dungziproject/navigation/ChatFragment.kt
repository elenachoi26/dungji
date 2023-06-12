package com.example.dungziproject.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dungziproject.MessageAdapter
import com.example.dungziproject.ProfileImageDialog
import com.example.dungziproject.databinding.FragmentChatBinding
import com.example.dungziproject.navigation.model.ItemDialogInterface
import com.example.dungziproject.navigation.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class ChatFragment :Fragment(), ItemDialogInterface {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var chatRoomId: String
    private lateinit var currentUserId: String
    private lateinit var currentUserNickname: String
    private lateinit var currentUserImg: String
    private var currentType: Boolean = true


    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var binding: FragmentChatBinding
    private var setEmoji:String? = "grandma"


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

        binding.goToEmoji.setOnClickListener{
            val dialog = ProfileImageDialog(this, false)
            dialog.isCancelable = false
            dialog.show(requireFragmentManager(), "EmoticonDialog")

//            val intent = Intent(requireContext(), ChatEmojiActivity::class.java)
//            startActivityForResult(intent, 0)
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
            val userInfo = getUserInfo()
            currentUserNickname = userInfo[0]
            currentUserImg = userInfo[1]

            sending()
        }
    }

    private fun sending() {
        binding.sendBtn.setOnClickListener {
            val messageText = binding.messageEdit.text.toString()
            currentType = true
            val currentTime = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())
            val message: Message

            // currentUserNickname 값이 null인 경우 메시지 전송하지 않음
            if (currentUserNickname.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "닉네임을 가져오는 중입니다. 잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //type이 string message 인 경우 -> 지금 문제. image를 한번 보내고 나서 다시 string으로 돌아가질 못한다.
            message = Message(messageText,currentUserId, currentTime,currentUserNickname, currentUserImg, true)
            sendMessage(message)
            hideKeyboard()
        }
    }

    private fun sendingEmoji() {
        val currentTime = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date())
        val message: Message

        // currentUserNickname 값이 null인 경우 메시지 전송하지 않음
        if (currentUserNickname.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "닉네임을 가져오는 중입니다. 잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        //type이 image message 인 경우
        message = Message(setEmoji,currentUserId, currentTime,currentUserNickname, currentUserImg, false)
        hideKeyboard()
        currentType = true
        sendMessage(message)
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


    private fun sendMessage(messageObject: Message) {
        mDbRef.child("chats").child(chatRoomId).child("messages").push().
        setValue(messageObject)
        binding.messageEdit.setText("")
    }

    private fun scrollToBottom() {
        binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0) {
            if(resultCode == Activity.RESULT_OK) {
                setEmoji = data?.getStringExtra("image")

                var resId = resources.getIdentifier("@raw/" + setEmoji, "raw", requireContext().packageName)
//                binding.imageView.setImageResource(resId)
                currentType = false
                sendingEmoji()
                messageAdapter.notifyDataSetChanged()
                //message type이 img임을 확인
                //다른 box로 바인딩 해서 recycler view에 attach
                //recevier가 볼 때도 message type이 다르고, image를 감싼 chat 형태로 보이도록 수정.

                //data type에서 message로 string 보내는 건 그대로 -> 그거 받아서 setsource.
                //하지만 message data class 자체에 string인지, image인지 구분해주는 flag 역할이 하나 필요할듯.
            }
        }
    }

    override fun onItemSelected(item: String) {
        setEmoji = item

        var resId = resources.getIdentifier("@raw/" + setEmoji, "raw", requireContext().packageName)
//                binding.imageView.setImageResource(resId)
        currentType = false
        sendingEmoji()
        messageAdapter.notifyDataSetChanged()

    }
}
