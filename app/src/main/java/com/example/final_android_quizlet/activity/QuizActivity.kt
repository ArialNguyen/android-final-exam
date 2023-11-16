package com.example.final_android_quizlet.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.final_android_quizlet.R
//import com.example.final_android_quizlet.activity.SplashActivity.Companion.listOfQ
import com.example.final_android_quizlet.models.Quiz

class QuizActivity : AppCompatActivity() {

    private lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_quiz)

        val btnSubmitQuiz = findViewById<LinearLayout>(R.id.btnSubmit_quiz)

        val txTimer_quiz = findViewById<TextView>(R.id.txTimer_quiz)

        countDownTimer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                txTimer_quiz.text = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60)
            }

            override fun onFinish() {
                val dialog = Dialog(this@QuizActivity)
                dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                dialog.setContentView(R.layout.time_out_dialog)

                dialog.findViewById<LinearLayout>(R.id.btnAgain_quiz).setOnClickListener {
                    val intent = Intent(this@QuizActivity, DetailTopic::class.java)
                    startActivity(intent)
                }
                dialog.show()
            }
        }.start()

        btnSubmitQuiz.setOnClickListener {
//            val intent = Intent(this, ResultQuizActivity::class.java)
//            startActivity(intent)
        }

    }
}