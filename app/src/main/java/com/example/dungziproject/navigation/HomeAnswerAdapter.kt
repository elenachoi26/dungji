package com.example.dungziproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.databinding.AnswerRowBinding
import com.example.dungziproject.databinding.HomeAnswerRowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class HomeAnswerAdapter(var items:ArrayList<Answer>):RecyclerView.Adapter<HomeAnswerAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(data:Answer, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding:HomeAnswerRowBinding): RecyclerView.ViewHolder(binding.root){
        init{
                binding.answer.setOnClickListener{
                    itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
                }
                binding.nickname.setOnClickListener{
                    itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = HomeAnswerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.nickname.text = items[position].nickname
        holder.binding.answer.text = items[position].answer
    }
}