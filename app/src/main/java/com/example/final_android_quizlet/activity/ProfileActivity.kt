package com.example.final_android_quizlet.activity


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.ChangePassword
import com.example.final_android_quizlet.auth.Logout
import com.example.final_android_quizlet.common.ActionTransition
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private val actionTransition: ActionTransition = ActionTransition(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val backButton = findViewById<ImageView>(R.id.backButton)

        val user = FirebaseAuth.getInstance().currentUser

        backButton.setOnClickListener {
            finish()
        }

        if (user != null) {
            val userEmail = user.email!!
            val userName = user.displayName!!

            findViewById<TextView>(R.id.text_userName).text = userName
            findViewById<TextView>(R.id.text_userEmail).text = userEmail
        }

        val changePasswordTextView = findViewById<TextView>(R.id.et_change_pw)

        changePasswordTextView.setOnClickListener {
            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
            actionTransition.moveNextTransition()
        }

        val logout = findViewById<ImageView>(R.id.image_logout)

        logout.setOnClickListener {
            val confirmationDialog = Logout(this)
            confirmationDialog.show()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}