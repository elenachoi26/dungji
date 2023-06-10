package com.example.dungziproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dungziproject.databinding.ActivityMainBinding
import com.example.dungziproject.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout(){
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        binding.bottomNavigation.selectedItemId = R.id.action_home
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_home ->{
                var homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,homeFragment).commit()
                return true
            }
            R.id.action_calendar ->{
                var calendarFragment = CalendarFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,calendarFragment).commit()
                return true
            }
            R.id.action_album ->{
                var albumFragment = AlbumFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,albumFragment).commit()
                return true
            }
            R.id.action_chat ->{
                var chatFragment = ChatFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,chatFragment).commit()
                return true
            }
            R.id.action_profile ->{
                var profileFragment = ProfileFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,profileFragment).commit()
                return true
            }
        }
        return false
    }

}