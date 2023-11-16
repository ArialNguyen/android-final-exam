package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.models.Answer
import com.example.final_android_quizlet.models.QuizWrite
//import com.example.final_android_quizlet.activity.SplashActivity.Companion.listOfQ
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.example.final_android_quizlet.service.QuizWriteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class WriteQuizActivity : AppCompatActivity() {
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val topicService: TopicService = TopicService()
    private val authService: AuthService = AuthService()
    private val quizWriteService = QuizWriteService()

    private var currentIndex = 0

    // Hard Data
    private lateinit var topic: Topic
    private lateinit var items: List<Term>

    private lateinit var txCard: TextView
    private lateinit var edWrite: EditText
    private lateinit var txNext: TextView
    private lateinit var txRightQuiz: TextView
    private lateinit var imBackQuiz: ImageView
    private lateinit var etQuit: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_write)

        txCard = findViewById(R.id.txCard_write)
        edWrite = findViewById(R.id.edWrite_write)
        txNext = findViewById(R.id.txNext_write)
        txRightQuiz = findViewById(R.id.txRight_quiz)
        imBackQuiz = findViewById(R.id.imBack_quiz)
        etQuit = findViewById(R.id.txExit_quiz)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
        }

        if (intent.getSerializableExtra("topic") != null) {
            topic = intent.getSerializableExtra("topic") as Topic
            items = topic.terms
            Log.i("TAG", "Topic received Write: $items")
        }

        updateUIWithTerm(items[currentIndex])
        updatePageNumber()

        txNext.setOnClickListener {
            val answerText = edWrite.text.toString()

            if (currentIndex < items.size - 1) {
                saveUserAnswer(answerText)
                currentIndex++
                updateUIWithTerm(items[currentIndex])
                updatePageNumber()
            } else {

            }
        }

        imBackQuiz.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateUIWithTerm(items[currentIndex])
                updatePageNumber()
            } else {

            }
        }

        etQuit.setOnClickListener {
            onBackPressed()
        }

    }

    private fun updateUIWithTerm(term: Term) {
        txCard.text = term.definition
        edWrite.text = null

        if (currentIndex == items.size - 1) {
            txNext.text = "SUBMIT"
            txNext.setOnClickListener {
//                val intent = Intent(this, ResultQuizActivity::class.java)
//                startActivity(intent)
            }
        } else {
            txNext.text = "NEXT"

            txNext.setOnClickListener {
                currentIndex++
                updateUIWithTerm(items[currentIndex])
                updatePageNumber()
            }
        }
    }

    private fun saveUserAnswer(answerText: String) {
        val termId = items[currentIndex].uid
        val result = answerText.equals(items[currentIndex].term, ignoreCase = true)

        val answer = Answer(UUID.randomUUID().toString(), answerText, termId, result)

        val quizWrite = QuizWrite(
            UUID.randomUUID().toString(),
            topic.uid,
            listOf(answer),
            0.0f
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val response = quizWriteService.saveUserAnswer(quizWrite)
            withContext(Dispatchers.Main) {
                if (response.status) {

                } else {

                }
            }
        }
    }

    private fun updatePageNumber() {
        txRightQuiz.text = "${currentIndex + 1} / ${items.size}"
    }

    override fun onBackPressed() {
        if (currentIndex > 0) {
            val answerText = edWrite.text.toString()
            saveUserAnswer(answerText)
            currentIndex--
            updateUIWithTerm(items[currentIndex])
            updatePageNumber()
        } else {
            super.onBackPressed()
        }
    }
}