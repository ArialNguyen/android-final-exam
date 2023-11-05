package com.example.final_android_quizlet.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R

class MainQuizActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_quiz)


        Handler().postDelayed({
            val exerciseType = intent.getStringExtra("exercise_type")

            if (exerciseType == "quiz") {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("exercise_type", "quiz")
                startActivity(intent)
            } else if (exerciseType == "write") {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("exercise_type", "write")
                startActivity(intent)
            }
        }, 1500)

    }
}