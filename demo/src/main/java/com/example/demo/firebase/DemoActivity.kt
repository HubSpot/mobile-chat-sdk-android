package com.example.demo.firebase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.databinding.ActivityDemoBinding
import com.example.demo.firebase.DemoFirebaseMessagingService.Companion.NOTIFICATION_DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textViewDemo.text = intent?.extras?.getString(NOTIFICATION_DATA)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        binding.textViewDemo.text = intent?.extras?.getString(NOTIFICATION_DATA)
    }
}