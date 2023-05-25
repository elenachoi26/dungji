package com.example.dungziproject

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.databinding.MessageBinding

class MessageAdapter (val messageList: ArrayList<Message>, val currentUserId: String):
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MessageBinding): RecyclerView.ViewHolder(binding.root){
        init{

        }
    }
    //아 헷갈려 참고하자: https://velog.io/@24hyunji/AndroidKotlin-RecyclerView-사용해보기
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(messageList[position].sendId == currentUserId){
            holder.binding.receiveMessageText.setBackgroundResource(R.drawable.send_background)
            holder.binding.receiveMessageSender.visibility = View.INVISIBLE
            holder.binding.receiveMessageText.text = messageList[position].message
            holder.binding.receiveMessageTime.text = messageList[position].sendTime
            holder.binding.root.gravity = Gravity.END
            holder.binding.receiveMessageTime.gravity = Gravity.END
        }
        else{ //상대방 채팅
            holder.binding.receiveMessageText.setBackgroundResource(R.drawable.receive_background)
            holder.binding.receiveMessageSender.text = messageList[position].senderNickname
            holder.binding.receiveMessageText.text = messageList[position].message
            holder.binding.receiveMessageTime.text = messageList[position].sendTime
            holder.binding.root.gravity = Gravity.START
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}
