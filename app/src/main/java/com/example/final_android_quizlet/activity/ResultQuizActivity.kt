package com.example.final_android_quizlet.activity
import android.app.Notification.Action
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.models.QuizWrite
import com.example.final_android_quizlet.models.Topic
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class ResultQuizActivity : AppCompatActivity() {
    private val actionTransition: ActionTransition = ActionTransition(this)

    private lateinit var tvTrue: TextView
    private lateinit var tvFalse: TextView
    private lateinit var tvTotalTrue: TextView
    private lateinit var tvTotalFalse: TextView
    private lateinit var btnNewTest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_quiz)

        tvTrue = findViewById(R.id.tvTrue_ResultQuizActivity)
        tvFalse = findViewById(R.id.tvFalse_ResultQuizActivity)
        tvTotalTrue = findViewById(R.id.tvTotalTrue_ResultQuizActivity)
        tvTotalFalse = findViewById(R.id.tvTotalFalse_ResultQuizActivity)
        btnNewTest = findViewById(R.id.btn_exam_ResultQuizActivity)

        val overall = intent.getIntExtra("overall", 0)
        val totalItems = intent.getIntExtra("totalItems", 0)

        val totalCorrect = overall
        val totalIncorrect = totalItems - totalCorrect

        tvTotalTrue.text = totalCorrect.toString()
        tvTotalFalse.text = totalIncorrect.toString()

        btnNewTest.setOnClickListener {
            actionTransition.moveNextTransition()
            finish()
        }

    }
}