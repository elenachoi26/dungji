package com.example.dungziproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.databinding.QuestionRowBinding

class QuestionAdapter(var items:ArrayList<Question>):RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(data:Question, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding:QuestionRowBinding): RecyclerView.ViewHolder(binding.root){
        init{
                binding.question.setOnClickListener{
                    itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = QuestionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.number.text = "#" + items[position].questionId
        holder.binding.question.text = items[position].question
    }
}