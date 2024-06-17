package com.example.sunnyweather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sunnyweather.databinding.ActivityMainBinding
import com.hjq.permissions.XXPermissions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    private fun checkPermissions() {
        if (!XXPermissions.isGranted(this, "android.permission.INTERNET")) {
        }
    }
}