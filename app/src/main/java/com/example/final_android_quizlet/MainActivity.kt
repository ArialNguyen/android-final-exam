package com.example.final_android_quizlet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.example.final_android_quizlet.service.user.UserService
import com.example.final_android_quizlet.models.User

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope


class MainActivity : AppCompatActivity() {
    private val myScope = CoroutineScope(Dispatchers.Main) // create coroutine Scope in MainThread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onDestroy() {
        super.onDestroy()
        myScope.cancel() // Avoid memory Leaks
    }
}