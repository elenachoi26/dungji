package com.example.dungziproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dungziproject.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {
    lateinit var binding:ActivityImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            val intent = Intent()

            grandmother.setOnClickListener{
                intent.putExtra("image", "grandmother")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            grandfather.setOnClickListener{
                intent.putExtra("image", "grandfather")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            woman.setOnClickListener{
                intent.putExtra("image", "woman")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            man.setOnClickListener{
                intent.putExtra("image", "man")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            boy.setOnClickListener{
                intent.putExtra("image", "boy")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            girl.setOnClickListener{
                intent.putExtra("image", "girl")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}