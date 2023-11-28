package com.example.final_android_quizlet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.UserService

class SearchCommunity : AppCompatActivity() {
    // Service
    private val userService: UserService = UserService()
    private val authService: AuthService = AuthService()
    private val actionTransition: ActionTransition = ActionTransition(this)


    // Hard data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_community)

        // Hand postData
        if(intent.getStringExtra("keyword") == null){
            Toast.makeText(this, "Oops something wrong, try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        val keyword = intent.getStringExtra("keyword")
        Log.i("TAG", "keyword: $keyword")
    }
}