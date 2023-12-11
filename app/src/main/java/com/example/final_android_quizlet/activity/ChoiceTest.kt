package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.ChoiceTestResultAdapter
import com.example.final_android_quizlet.adapter.data.ResultChoiceAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.DialogClickedEvent
import com.example.final_android_quizlet.fragments.dialog.DialogFeedBackChoiceTest
import com.example.final_android_quizlet.models.*
import com.example.final_android_quizlet.models.Enum.ETermList
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.MultipleChoiceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ChoiceTest : AppCompatActivity() {
    // Service
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val choiceService: MultipleChoiceService = MultipleChoiceService()

    // ToolBar
    private lateinit var imgExit: ImageView
    private lateinit var tvProgress: TextView
    private lateinit var progressBarTerm: ProgressBar

    // Layout Exam
    private lateinit var layoutExam: ConstraintLayout
    private lateinit var tvTermTitle: TextView
    private val answersView: MutableList<TextView> = mutableListOf()

    // Layout Result
    private lateinit var layoutResult: ConstraintLayout
    private lateinit var tvFeedBack: TextView
    private lateinit var tvTotalTrue: TextView
    private lateinit var tvTotalFalse: TextView
    private lateinit var btnExam: Button
    private lateinit var btnLearning: Button
    private lateinit var recyclerView: RecyclerView

    // Adapter
    private lateinit var resultChoiceAdapter: ChoiceTestResultAdapter
    private val itemsAdapter: MutableList<ResultChoiceAdapterItem> = mutableListOf()

    // Hard Data
    private lateinit var answerType: EAnswer
    private lateinit var optionExam: OptionExamData
    private lateinit var topicIntent: Topic
    private var showAnswerIntent: Boolean = false
    private val choiceTest: MultipleChoice = MultipleChoice()
    private var choiceDB: MultipleChoice? = null
    private var items: MutableList<Term> = mutableListOf()
    private val answers: MutableList<String> = mutableListOf()
    private var currentTermIndex = 0
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_test)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
            actionTransition.moveNextTransition()
        }
        val userId = authService.getCurrentUser().uid
        // get Intent
        if (intent.getSerializableExtra("topic") == null ||
            intent.getSerializableExtra("answers") == null ||
            intent.getSerializableExtra("terms") == null ||
            intent.getSerializableExtra("optionExam") == null ||
            intent.getSerializableExtra("termType") == null
        ) {
            finish()
            Toast.makeText(this, "Oops, something wrong. Try again!!!", Toast.LENGTH_LONG).show()
            actionTransition.rollBackTransition()
        }
        if (intent.getSerializableExtra("choice") != null) {
            choiceDB = intent.getSerializableExtra("choice") as MultipleChoice
        }
        val termType = intent.getSerializableExtra("termType") as ETermList
        optionExam = intent.getSerializableExtra("optionExam") as OptionExamData
        showAnswerIntent = optionExam.showAns

        topicIntent = intent.getSerializableExtra("topic") as Topic
        items.addAll(intent.getSerializableExtra("terms") as List<Term>)
        answers.addAll(intent.getSerializableExtra("answers") as List<String>)
        answerType = optionExam.answer
        Log.i("TAG", "terms: $items")
        Log.i("TAG", "answers: $answers")




        // Initialize ChoiceTest
        choiceTest.uid = UUID.randomUUID().toString()
        choiceTest.optionExam = optionExam
        choiceTest.userId = userId
        choiceTest.topicId = topicIntent.uid
        choiceTest.totalQuestion = topicIntent.terms.size
        choiceTest.termType = termType

        // Link view
        imgExit = findViewById(R.id.img_exit_choiceTest)
        tvProgress = findViewById(R.id.tvProgress_choiceTest)
        progressBarTerm = findViewById(R.id.progressBarTerm_choiceTest)
        // Link view -> Layout Result
        layoutExam = findViewById(R.id.layout_exam_choiceTest)
        tvTermTitle = findViewById(R.id.tvTermTitle_choiceTest)
        items.forEachIndexed { index, term ->
            when (index) {
                0 -> answersView.add(findViewById(R.id.tvTermDefinition1_choiceTest))

                1 -> answersView.add(findViewById(R.id.tvTermDefinition2_choiceTest))

                2 -> answersView.add(findViewById(R.id.tvTermDefinition3_choiceTest))

                3 -> answersView.add(findViewById(R.id.tvTermDefinition4_choiceTest))

                else -> return@forEachIndexed
            }
        }

        // Link view -> Layout Result
        layoutResult = findViewById(R.id.layout_result_choiceTest)
        tvFeedBack = findViewById(R.id.tvFeedback_choiceTest)
        tvTotalTrue = findViewById(R.id.tvTotalTrue_choiceTest)
        tvTotalFalse = findViewById(R.id.tvTotalFalse_choiceTest)
        btnExam = findViewById(R.id.btn_exam_choiceTest)
        btnLearning = findViewById(R.id.btn_learning_choiceTest)
        recyclerView = findViewById(R.id.recyclerView_choiceTest)

        // Speech

        textToSpeech = TextToSpeech(this){ status ->
            Log.i("TAG", "textToSpeech: ")
            if (status == TextToSpeech.SUCCESS){
                val result = textToSpeech.setLanguage(Locale.forLanguageTag(if (answerType.name == EAnswer.DEFINITION.name) topicIntent.termLanguage!! else topicIntent.definitionLanguage!!))
                // Load View
                loadExamView()
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(this, "Oops language not Supported!!!", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Recycler View
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultChoiceAdapter = ChoiceTestResultAdapter(itemsAdapter)
        recyclerView.adapter = resultChoiceAdapter
        // Click Event
        answersView.forEach {
            it.visibility = View.VISIBLE
            it.setOnClickListener { view ->
                handleClickOnAnswer(view)
            }
        }

        imgExit.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()

    }

    private fun updateProgress() {
        if (currentTermIndex < items.size) {
            tvProgress.text = "${currentTermIndex + 1} / ${items.size}"
        }
        val percent = ((currentTermIndex) * 100) / items.size
        progressBarTerm.progress = percent
    }

    private fun loadRandomAnswers(): List<String> {
        answers.shuffle()
        val answersRes = (if (items.size > 4) answers.take(4) else answers.take(items.size)).toMutableList()
        if (!answersRes.contains(items[currentTermIndex].definition) && answerType.name == EAnswer.DEFINITION.name) {
            answersRes[0] = items[currentTermIndex].definition
        }
        if (!answersRes.contains(items[currentTermIndex].term) && answerType.name == EAnswer.TERM.name) {
            answersRes[0] = items[currentTermIndex].term
        }
        answersRes.shuffle()
        return answersRes
    }

    private fun loadExamView() {
        if (currentTermIndex == items.size) {
            // Handle Finish Exam
            updateProgress()
            choiceTest.overall = choiceTest.answers.filter { it.result }.size

            tvTotalTrue.text = choiceTest.overall.toString()
            tvTotalFalse.text = (items.size - choiceTest.overall as Int).toString()
            btnExam.setOnClickListener {
                val intent = Intent(this, DetailTopic::class.java)
                intent.putExtra("action", "openExam")
                intent.putExtra("topicId", topicIntent.uid) // Need to convert to whole topic not id
                startActivity(intent)
                finish()
                actionTransition.rollBackTransition()
            }
            btnLearning.setOnClickListener {
                val intent = Intent(this, MainQuizActivity::class.java)
                intent.putExtra("exercise_type", "flashcard")
                startActivity(intent)
                finish()
                actionTransition.moveNextTransition()
            }
            itemsAdapter.addAll(choiceTest.answers.map {
                ResultChoiceAdapterItem(
                    it.term.term,
                    it.term.definition,
                    it.answer,
                    it.result
                )
            })
            resultChoiceAdapter.notifyDataSetChanged()

            progressBarTerm.setBackgroundColor(getColor(R.color.colorBackground))

            layoutExam.visibility = View.GONE
            layoutResult.visibility = View.VISIBLE
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    Log.i("TAG", "choiceDB: $choiceDB")
                    if (choiceDB != null) {
                        choiceService.MPForUserLogged().deleteChoiceTest(choiceDB!!.uid)
                    }
                    val createChoice = choiceService.createChoiceTest(choiceTest)
                    if (!createChoice.status) {
                        runOnUiThread {
                            Toast.makeText(this@ChoiceTest, createChoice.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } else {
            tvTermTitle.text =
                if (answerType.name == EAnswer.TERM.name) items[currentTermIndex].definition else items[currentTermIndex].term
            // handle random answer
            Log.i("TAG", "term Title: ${tvTermTitle.text}")
            val randomAnswers = loadRandomAnswers()
            answersView.forEachIndexed { index, view ->
                view.text = randomAnswers[index]
            }
            updateProgress()
            if(optionExam.autoSpeak){
                textToSpeech.speak(tvTermTitle.text.trim().toString(), TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    private fun handleClickOnAnswer(view: View) {
        val answerChosen = (view as TextView).text.toString()
        var result = false
        var ques: String = if (answerType.name == EAnswer.DEFINITION.name) items[currentTermIndex].term else items[currentTermIndex].definition
        var ans: String = if (answerType.name == EAnswer.DEFINITION.name) items[currentTermIndex].definition else items[currentTermIndex].term
        if (items[currentTermIndex].definition == answerChosen && answerType.name == EAnswer.DEFINITION.name) {
            result = true
            ques = items[currentTermIndex].term
            ans = items[currentTermIndex].definition
        }
        if (items[currentTermIndex].term == answerChosen && answerType.name == EAnswer.TERM.name) {
            result = true
            ques = items[currentTermIndex].definition
            ans = items[currentTermIndex].term
        }
        val answer = AnswerChoice(items[currentTermIndex], answerChosen, result)
        choiceTest.answers.add(answer)
        currentTermIndex++
        if(showAnswerIntent){
            val dialogFeedBackChoiceTest =
                DialogFeedBackChoiceTest(result, ques, ans, answerChosen, object : DialogClickedEvent.FeedBackChoiceTest {
                    override fun setSuccessButton() {
                        loadExamView()
                    }
                })
            dialogFeedBackChoiceTest.show(supportFragmentManager, DialogFeedBackChoiceTest::class.simpleName)
        }else{
            loadExamView()
        }

    }
}