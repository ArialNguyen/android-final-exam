package com.example.final_android_quizlet.activity

//import com.example.final_android_quizlet.activity.SplashActivity.Companion.listOfQ
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.models.Answer
import com.example.final_android_quizlet.models.QuizWrite
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.QuizWriteService
import com.example.final_android_quizlet.service.TopicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class WriteQuizActivity : AppCompatActivity() {
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val topicService: TopicService = TopicService()
    private val authService: AuthService = AuthService()
    private val quizWriteService = QuizWriteService()

    private var currentIndex = 0

    // Hard Data
    private lateinit var quizWrite: QuizWrite
    private lateinit var topicIntent: Topic
    private lateinit var items: List<Term>

    private lateinit var txCard: TextView
    private lateinit var edWrite: EditText
    private lateinit var txNext: TextView
    private lateinit var txRightQuiz: TextView
    private lateinit var imBackQuiz: ImageView
    private lateinit var etQuit: TextView

    // hard data
    private var writingTestDb: QuizWrite? = null
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
        val userId = authService.getCurrentUser().uid
        if (intent.getSerializableExtra("topic") == null) {
            finish()
            Toast.makeText(this, "Oops something wrong. Try again!!!", Toast.LENGTH_LONG).show()
            actionTransition.rollBackTransition()
        }
        topicIntent = intent.getSerializableExtra("topic") as Topic

        if (intent.getSerializableExtra("quizWrite") != null) {
            Log.i("TAG", "quizWrite INtent : $")
            writingTestDb = intent.getSerializableExtra("quizWrite") as QuizWrite
        }

        items = topicIntent.terms

        quizWrite = QuizWrite()
        quizWrite.uid = UUID.randomUUID().toString()
        quizWrite.topicId = topicIntent.uid
        quizWrite.userId = userId

        updateUIWithTerm()
        updatePageNumber()

        txNext.setOnClickListener {
            insertData()
        }

        imBackQuiz.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateUIWithTerm()
                updatePageNumber()
            }
        }

        etQuit.setOnClickListener {
            onBackPressed()
        }

    }

    private fun backAction() {
        if (quizWrite.answers.isNotEmpty()) {
            quizWrite.answers.removeLast()
            currentIndex--
            updateUIWithTerm()
            updatePageNumber()
        }
    }

    private fun insertData() {
        val answerText = edWrite.text.toString()
        quizWrite.answers.add(
            Answer(
                items[currentIndex], answerText, answerText == items[currentIndex].definition
            )
        )
        currentIndex++
        updateUIWithTerm()
        updatePageNumber()
    }

    private fun updateUIWithTerm() {
        txCard.text = items[currentIndex].definition
        edWrite.text.clear()


        if (currentIndex == items.size - 1) {
            txNext.text = "SUBMIT"
            txNext.setOnClickListener {

                val answerText = edWrite.text.toString()
                quizWrite.answers.add(
                    Answer(
                        items[currentIndex], answerText, answerText == items[currentIndex].definition
                    )
                )
                //                val intent = Intent(this, ResultQuizActivity::class.java)

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if (writingTestDb != null) {
                            Log.i("TAG", "writingTestDb: $writingTestDb")
                            quizWriteService.WTForUserLogged().deleteWritingTestById(writingTestDb!!.uid)
                        }
                        val createQuiz = quizWriteService.saveUserAnswer(quizWrite)
                        if (createQuiz.status) {
                            intent.putExtra("result", quizWrite)
                            startActivity(intent)
                        }
                    }
                }
            }
        } else {
            txNext.text = "NEXT"
            txNext.setOnClickListener {
                insertData()
            }
        }
    }

    private fun updatePageNumber() {
        txRightQuiz.text = "${currentIndex + 1} / ${items.size}"
    }


    override fun onBackPressed() {
        super.onBackPressed()
        backAction()
    }
}