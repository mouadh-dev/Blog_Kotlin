package com.example.Blog.Activity

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.Blog.Fragments.LoginFragment
import com.example.Blog.Fragments.RegisterFragment
import com.example.Blog.R
import com.example.Blog.databinding.ActivityAuthBinding

class ActivityAuth : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, loginFragment).commit()
    }



}