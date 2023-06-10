package com.example.dungziproject.navigation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dungziproject.LoginActivity
import com.example.dungziproject.databinding.ActivityTutorialBinding

class TutorialActivity : AppCompatActivity() {

    lateinit var binding: ActivityTutorialBinding
    lateinit var adapter: TutorialAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        init()
        setContentView(binding.root)
    }

    private fun init() {
        if(hasSeenTutorial()){
            //intent to main activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else {
            initLayout()
            binding.tutorialCompleteBtn.setOnClickListener {
                setTutorialSeen()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initLayout() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
        adapter = TutorialAdapter()
        binding.recyclerView.adapter = adapter

        val simpleCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val myViewHolder = viewHolder as TutorialAdapter.ViewHolder
                adapter.switchImg(myViewHolder)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    // 튜토리얼을 본 사용자인지 확인하는 함수
    private fun hasSeenTutorial(): Boolean {
        val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("hasSeenTutorial", false)
    }

    // 튜토리얼을 본 사용자로 플래그 설정
    private fun setTutorialSeen() {
        val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("hasSeenTutorial", true)
        editor.apply()
    }

}