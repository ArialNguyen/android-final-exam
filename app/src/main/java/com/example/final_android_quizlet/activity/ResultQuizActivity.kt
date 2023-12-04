package com.example.final_android_quizlet.activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class ResultQuizActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_quiz)

        val overall = intent.getIntExtra("overall", 0)
        val totalItems = intent.getIntExtra("totalItems", 0)

        val txResult = findViewById<TextView>(R.id.txResult_quiz)
        txResult.text = "$overall/$totalItems"

        val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
        circularProgressBar.setProgressWithAnimation(overall.toFloat()) // Số câu đúng
        circularProgressBar.progressMax = totalItems.toFloat() // Tổng số câu
    }
}