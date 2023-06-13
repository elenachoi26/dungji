package com.example.dungziproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.databinding.AnswerRowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AnswerAdapter(var items:ArrayList<Answer>):RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {
    private var auth: FirebaseAuth = Firebase.auth

    interface OnItemClickListener{
        fun OnItemClick(data:Answer, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding:AnswerRowBinding): RecyclerView.ViewHolder(binding.root){
        init{
                binding.remove.setOnClickListener {
                    itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AnswerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.nickname.text = items[position].nickname
        holder.binding.answer.text = items[position].answer

        if(auth.currentUser?.uid!! == items[position].userId)
            holder.binding.remove.visibility = View.VISIBLE
        else
            holder.binding.remove.visibility = View.GONE
    }
}