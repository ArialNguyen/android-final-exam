package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.models.EAnswer
import com.example.final_android_quizlet.models.Enum.ETermList
import com.example.final_android_quizlet.models.OptionExamData
import com.example.final_android_quizlet.service.FlashCardService
import com.example.final_android_quizlet.service.MultipleChoiceService
import com.example.final_android_quizlet.service.QuizWriteService
import com.example.final_android_quizlet.service.TopicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class MainQuizActivity : AppCompatActivity() {

    private val choiceService: MultipleChoiceService = MultipleChoiceService()
    private val topicService: TopicService = TopicService()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val flashCardService: FlashCardService = FlashCardService()
    private val writeQuizService: QuizWriteService = QuizWriteService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_quiz)

        val exerciseType = intent.getStringExtra("classDestination")
        val optionData = intent.getSerializableExtra("data") as OptionExamData

        val typeTerm = intent.getSerializableExtra("typeTerm") as ETermList? ?: ETermList.NORMAL_TERMS
        if (exerciseType == ChoiceTest::class.simpleName) {
            choiceTest(typeTerm, optionData)
        } else if (exerciseType == WriteQuizActivity::class.simpleName) {
            writingTest(typeTerm, optionData)
        } else {
            flashCard(typeTerm, optionData)
        }
    }

    private fun writingTest(typeTerm: ETermList, optionData: OptionExamData) {
        if (intent.getStringExtra("topicId").isNullOrEmpty() || intent.getSerializableExtra("data") == null) {
            Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        val topicId = intent.getStringExtra("topicId")!!
        val intent = Intent(this, WriteQuizActivity::class.java)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fetchTopic = topicService.getTopicById(topicId)
                if (!fetchTopic.status) {
                    Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG)
                        .show()
                    finish()
                    actionTransition.rollBackTransition()
                }
                val fetchWT = writeQuizService.WTForUserLogged().findWritingTestByTopicId(topicId, typeTerm)
                if (fetchWT.status) {
                    intent.putExtra("quizWrite", fetchWT.quizWrite)
                }
                runOnUiThread {
                    // AnswerType
                    val terms =
                        if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms.toMutableList() else fetchTopic.topic!!.starList.toMutableList()

                    // Number Question
                    val tmpList = terms.take(optionData.numberQues)
                    terms.clear()
                    terms.addAll(tmpList)

                    // Shuffle Option
                    if (optionData.shuffle) {
                        terms.shuffle()
                    }
                    intent.putExtra("terms", terms as Serializable)
                    // Option Exam
                    intent.putExtra("optionExam", optionData)

                    intent.putExtra("topic", fetchTopic.topic!!)
                    intent.putExtra("termType", typeTerm)
                    startActivity(intent)
                    finish()
                    actionTransition.moveNextTransition()
                }
            }
        }
    }

    private fun choiceTest(typeTerm: ETermList, optionData: OptionExamData) {
        if (intent.getStringExtra("topicId").isNullOrEmpty() || intent.getSerializableExtra("data") == null) {
            Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        val topicId = intent.getStringExtra("topicId")!!
        val intentDes = Intent(this, ChoiceTest::class.java)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fetchTopic = topicService.getTopicById(topicId)
                if (!fetchTopic.status) {
                    Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG)
                        .show()
                    finish()
                    actionTransition.rollBackTransition()
                }
                val fetchChoice = choiceService.MPForUserLogged().findChoiceTestByTopicIdAndTermType(topicId, typeTerm)
                if (fetchChoice.status) {
                    intentDes.putExtra("choice", fetchChoice.testChoice)
                }
                runOnUiThread {
                    // Handle Option Data
                    val question =
                        if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms.toMutableList() else fetchTopic.topic!!.starList.toMutableList()
                    val answers =
                        if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms.map { it.definition }
                            .toMutableList() else fetchTopic.topic!!.starList.map { it.definition }.toMutableList()
                    // Answer Type
                    if (optionData.answer == EAnswer.TERM) {
                        answers.addAll(question.map { it.term })
                    }

                    // Get number question
                    val tmpList = question.take(optionData.numberQues)
                    question.clear()
                    question.addAll(tmpList)

                    // shuffle
                    if (optionData.shuffle) {
                        question.shuffle()
                    }

                    // Option Exam
                    intentDes.putExtra("optionExam", optionData)

                    intentDes.putExtra("topic", fetchTopic.topic!!)
                    intentDes.putExtra("termType", typeTerm)
                    intentDes.putExtra("answers", answers as Serializable)
                    intentDes.putExtra("terms", question as Serializable)

                    startActivity(intentDes)
                    actionTransition.moveNextTransition()
                    finish()
                }
            }
        }
    }

    private fun flashCard(typeTerm: ETermList, optionData: OptionExamData) {
        val intent = Intent(this, FlashcardActivity::class.java)
        val topicId = this.intent.getStringExtra("topicId")
        if (topicId.isNullOrEmpty()) {
            Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        Log.i("TAG", "MAIN topicId: $topicId")
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fetchTopic = topicService.getTopicById(topicId!!)
                if (!fetchTopic.status) {
                    Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG)
                        .show()
                    finish()
                    actionTransition.rollBackTransition()
                }
                val fetchFlashCard =
                    flashCardService.findFlashCardByTopicIdAndTermType(fetchTopic.topic!!.uid, typeTerm)
                // Get remain terms learning
                val listTermIdKnew = mutableListOf<String>()
                val remainTerms = if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms.toMutableList() else fetchTopic.topic!!.starList.toMutableList()
                if (fetchFlashCard.status) {
                    listTermIdKnew.addAll(fetchFlashCard.flashCard!!.termsKnew.map { it.uid })

                    if (listTermIdKnew.isNotEmpty() && (listTermIdKnew.size != remainTerms.size)) { // not learned yet or  Learned all
                        remainTerms.clear()
                        remainTerms.addAll(
                            if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms.filter {
                                !listTermIdKnew.contains(
                                    it.uid
                                )
                            }
                            else fetchTopic.topic!!.starList.filter { !listTermIdKnew.contains(it.uid) }
                        )
                    } else {
                        remainTerms.clear()
                        remainTerms.addAll(
                            if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms
                            else fetchTopic.topic!!.starList
                        )
                    }
                    intent.putExtra("flashcard", fetchFlashCard.flashCard)
                }

                if (optionData.shuffle) {
                    remainTerms.shuffle()
                }
                val answers =
                    if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms.map { it.definition }
                        .toMutableList()
                    else fetchTopic.topic!!.starList.map { it.definition }.toMutableList()

                runOnUiThread {
                    if (optionData.answer == EAnswer.TERM) {
                        answers.addAll(
                            if (typeTerm.name == ETermList.NORMAL_TERMS.name) fetchTopic.topic!!.terms.map { it.term }
                            else fetchTopic.topic!!.starList.map { it.term }
                        )
                    }
                    intent.putExtra("topic", fetchTopic.topic!!)
                    intent.putExtra("remainTerms", remainTerms as Serializable?)
                    intent.putExtra("termType", typeTerm)
                    intent.putExtra("optionExam", optionData)

                    startActivity(intent)
                    finish()
                    actionTransition.moveNextTransition()
                }
            }
        }
    }
}