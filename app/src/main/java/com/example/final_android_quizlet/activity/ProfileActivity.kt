package com.example.final_android_quizlet.activity


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val backButton = findViewById<ImageView>(R.id.backButton)

        val user = FirebaseAuth.getInstance().currentUser

        backButton.setOnClickListener {
            finish()
        }

        if (user != null) {
            val userEmail = user.email
            val userName = user.displayName

            findViewById<TextView>(R.id.text_userName).text = userName
            findViewById<TextView>(R.id.text_userEmail).text = userEmail
        }
    }

    override fun onBackPressed() {
        finish()
    }
}