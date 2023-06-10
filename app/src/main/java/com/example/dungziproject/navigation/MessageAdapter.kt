package com.example.dungziproject

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.databinding.MessageBinding
import com.example.dungziproject.navigation.model.Message

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
            holder.binding.receiveMessageImg.visibility = View.GONE
            holder.binding.sendMessageTime.text = messageList[position].sendTime
            holder.binding.receiveMessageTime.visibility = View.GONE
            holder.binding.linear.gravity = Gravity.END
//            holder.binding.linear.marginTop = 0

            val layoutParams = holder.binding.linear.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = 0
            holder.binding.linear.layoutParams = layoutParams
        } else{ //상대방 채팅
            holder.binding.receiveMessageText.setBackgroundResource(R.drawable.receive_background)
            holder.binding.receiveMessageSender.text = messageList[position].senderNickname
            holder.binding.receiveMessageText.text = messageList[position].message
            holder.binding.receiveMessageTime.text = messageList[position].sendTime
            holder.binding.sendMessageTime.visibility = View.GONE
            val imagesrc = holder.itemView.context.resources.getIdentifier(messageList[position].senderImg, "raw", holder.itemView.context.packageName)
            if (imagesrc != 0) {
                holder.binding.receiveMessageImg.setImageResource(imagesrc)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}

private fun ImageView.clipToOutline(b: Boolean) {

}
