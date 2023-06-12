package com.example.dungziproject


import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.databinding.MessageBinding
import com.example.dungziproject.navigation.model.Message

import java.io.InputStream

class MessageAdapter (val messageList: ArrayList<Message>, val currentUserId: String):
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MessageBinding): RecyclerView.ViewHolder(binding.root){
        init{

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]

        //내가 보낸 메세지
        if (message.sendId == currentUserId) {
            holder.binding.receiveMessageText.text = message.message
            //보낸 사람(=나)의 닉네임과 아이콘은 보일 필요 없음.
            holder.binding.receiveMessageSender.visibility = View.INVISIBLE
            holder.binding.receiveMessageImg.visibility = View.GONE
            //보낸 시각(메세지 좌측) 표시.
            holder.binding.sendMessageTime.text = message.sendTime
            holder.binding.sendMessageTime.visibility = View.VISIBLE
            //보낸 시각(메세지 우측) 보일 필요 X
            holder.binding.receiveMessageTime.visibility = View.GONE
            //내가 보낸 메세지는 우측에 표시, 마진도 줄이기.
            holder.binding.linear.gravity = Gravity.END
            val layoutParams = holder.binding.linear.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = 0
            holder.binding.linear.layoutParams = layoutParams
            holder.binding.receiveMessageText.setBackgroundResource(R.drawable.send_background)

            //메세지가 일반 string인 경우.
            if (message.type) {
                //emoji 자리 Image 제거.
                holder.binding.receiveMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            } else { //emoji를 보낸 경우.
                //메세지 비워주기.
                holder.binding.receiveMessageText.text = ""

                //emoji 집어넣기.
                val imagesrc = holder.itemView.context.resources.getIdentifier(message.message, "raw", holder.itemView.context.packageName)
                if (imagesrc != 0) {
                    val inputStream: InputStream = holder.itemView.context.resources.openRawResource(imagesrc)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    drawable?.setBounds(0, 0, 300, 300)
                    holder.binding.receiveMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
                }
            }

        } else { //남이 보낸 message 인 경우.
            holder.binding.receiveMessageText.text = message.message
            holder.binding.receiveMessageSender.text = message.senderNickname
            holder.binding.receiveMessageTime.text = message.sendTime
            holder.binding.sendMessageTime.visibility = View.GONE
            holder.binding.receiveMessageText.setBackgroundResource(R.drawable.receive_background)

            val imagesrc = holder.itemView.context.resources.getIdentifier(message.senderImg, "raw", holder.itemView.context.packageName)
            if (imagesrc != 0) {
                holder.binding.receiveMessageImg.setImageResource(imagesrc)
            }

            if (message.type) { //string message인 경우.
                holder.binding.receiveMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            } else {

                holder.binding.receiveMessageText.text=""
                val imagesrc = holder.itemView.context.resources.getIdentifier(message.message, "raw", holder.itemView.context.packageName)
                if (imagesrc != 0) {
                    val inputStream: InputStream = holder.itemView.context.resources.openRawResource(imagesrc)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    drawable?.setBounds(0, 0, 100, 100)
                    holder.binding.receiveMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}
