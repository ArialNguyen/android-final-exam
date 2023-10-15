package com.example.final_android_quizlet

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import com.example.final_android_quizlet.db.UserService
import com.example.final_android_quizlet.models.User

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope


class MainActivity : AppCompatActivity() {
    private val userService = UserService()
    private val myScope = CoroutineScope(Dispatchers.Main) // create coroutine Scope in MainThread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Launch a coroutine within the scope
        // All these thing run only in this scope
        myScope.launch {
            Log.i("Users1: ", "users.toString()")
            val btn_click = findViewById<Button>(R.id.btn_click)
            var users: List<User> = userService.getUsers()
            Log.i("Users: ", users.toString())

            // Add
            btn_click.setOnClickListener {
                lifecycleScope.launch {
                    val user = userService.addUser(User("Hung", "adsad", "", ""))
                    Log.i("inner launch: ", user.toString())
                }
            }
            Log.i("Enmd: ", users.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myScope.cancel() // Avoid memory Leaks
    }
}