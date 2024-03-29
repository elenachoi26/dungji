package com.example.dungziproject.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.R
import com.example.dungziproject.databinding.TutorialimgBinding

class TutorialAdapter: RecyclerView.Adapter<TutorialAdapter.ViewHolder>()  {
    private val itemList = listOf(
        R.drawable.tutorial1, R.drawable.tutorial2,R.drawable.tutorial3, R.drawable.tutorial4
    , R.drawable.tutorial5, R.drawable.tutorial6, R.drawable.tutorial7, R.drawable.tutorial8, R.drawable.tutorial9) // 항목의 이미지 리스트
    inner class ViewHolder(val binding: TutorialimgBinding): RecyclerView.ViewHolder(binding.root){
        init{
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TutorialimgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.binding.tutorialImg.setImageResource(currentItem)
    }

    fun switchImg(holder: ViewHolder){
        val currentPosition = holder.adapterPosition
        val nextPosition = currentPosition + 1

        if (nextPosition < itemList.size) {
            holder.binding.tutorialImg.setImageResource(itemList[nextPosition])
            notifyItemChanged(nextPosition)
        }
    }
}