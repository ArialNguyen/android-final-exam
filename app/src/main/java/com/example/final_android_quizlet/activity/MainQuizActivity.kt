package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.*
import com.example.final_android_quizlet.models.EAnswer
import com.example.final_android_quizlet.models.OptionExamData
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.service.*
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
        val optionData = intent.getSerializableExtra("data") as OptionExamData // Lấy OptionExamData từ intent

        if (exerciseType == ChoiceTest::class.simpleName) {
            choiceTest()
        } else if (exerciseType == WriteQuizActivity::class.simpleName) {
            writingTest()
        }else{
            flashCard(optionData)
        }
    }
    private fun writingTest(){
        if(intent.getStringExtra("topicId").isNullOrEmpty() || intent.getSerializableExtra("data") == null){
            Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        val topicId = intent.getStringExtra("topicId")!!
        val optionData = intent.getSerializableExtra("data") as OptionExamData
        val intent = Intent(this, WriteQuizActivity::class.java)
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val fetchTopic = topicService.getTopicById(topicId)
                if(!fetchTopic.status){
                    Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
                    finish()
                    actionTransition.rollBackTransition()
                }
                val fetchWT = writeQuizService.WTForUserLogged().findWritingTestByTopicId(topicId)
                if(fetchWT.status){
                    intent.putExtra("quizWrite", fetchWT.quizWrite)
                }
                runOnUiThread {
                    // AnswerType
                    val terms = fetchTopic.topic!!.terms.toMutableList()

                    // Number Question
                    val tmpList = terms.take(optionData.numberQues)
                    terms.clear()
                    terms.addAll(tmpList)

                    // Shuffle Option
                    if(optionData.shuffle){
                        terms.shuffle()
                    }
                    intent.putExtra("terms", terms as Serializable)
                    // Option Exam
                    intent.putExtra("optionExam", optionData)

                    intent.putExtra("topic", fetchTopic.topic!!)
                    startActivity(intent)
                    finish()
                    actionTransition.moveNextTransition()
                }
            }
        }
    }
    private fun choiceTest(){
        if(intent.getStringExtra("topicId").isNullOrEmpty() || intent.getSerializableExtra("data") == null){
            Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        val topicId = intent.getStringExtra("topicId")!!
        val optionData = intent.getSerializableExtra("data") as OptionExamData
        val intentDes = Intent(this, ChoiceTest::class.java)
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val fetchTopic = topicService.getTopicById(topicId)
                if(!fetchTopic.status){
                    Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
                    finish()
                    actionTransition.rollBackTransition()
                }
                val fetchChoice = choiceService.MPForUserLogged().findChoiceTestByTopicId(topicId)
                if(fetchChoice.status){
                    Log.i("TAG", "choiceTest: ${fetchChoice.testChoice}")
                    intentDes.putExtra("choice", fetchChoice.testChoice)
                }
                runOnUiThread {
                    // Handle Option Data
//                    val fetchAnswers = fetchTopic.topic!!.terms.map { it.definition } as Serializable
                    val question = fetchTopic.topic!!.terms.toMutableList()
                    val answers = fetchTopic.topic!!.terms.map { it.definition }.toMutableList()
                    // Answer Type
                    if (optionData.answer == EAnswer.TERM){
                        answers.addAll(fetchTopic.topic!!.terms.map { it.term })
                    }

                    // Get number question
                    val tmpList = question.take(optionData.numberQues)
                    question.clear()
                    question.addAll(tmpList)

                    // shuffle
                    if (optionData.shuffle){
                        question.shuffle()
                    }

                    // Option Exam
                    intentDes.putExtra("optionExam", optionData)

                    intentDes.putExtra("topic", fetchTopic.topic!!)
                    intentDes.putExtra("answers", answers as Serializable)
                    intentDes.putExtra("terms", question as Serializable)

                    startActivity(intentDes)
                    actionTransition.moveNextTransition()
                    finish()
                }
            }
        }
    }

    private fun flashCard(optionData: OptionExamData){
        val intent = Intent(this, FlashcardActivity::class.java)
        val topicId = this.intent.getStringExtra("topicId")
        if(topicId.isNullOrEmpty()){
            Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        Log.i("TAG", "MAIN topicId: $topicId")
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val fetchTopic = topicService.getTopicById(topicId!!)
                if(!fetchTopic.status){
                    Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
                    finish()
                    actionTransition.rollBackTransition()
                }
                val fetchFlashCard = flashCardService.findFlashCardByTopicId(fetchTopic.topic!!.uid)
                if(!fetchFlashCard.status ){
                    Toast.makeText(this@MainQuizActivity, "Error for move page, please try again!!!", Toast.LENGTH_LONG).show()
                    finish()
                    actionTransition.rollBackTransition()
                }
                // Get remain terms learning
                val isExistFlashCard = fetchFlashCard.flashCard != null
                val listTermIdKnew = mutableListOf<String>()
                val remainTerms = mutableListOf<Term>()
                if(isExistFlashCard){ // true
                    listTermIdKnew.addAll(fetchFlashCard.flashCard!!.termsKnew.map { it.uid })
                    if(listTermIdKnew.isNotEmpty() && (listTermIdKnew.size != fetchTopic.topic!!.terms.size)){ // not learned yet or  Learned all
                        remainTerms.addAll(fetchTopic.topic!!.terms.filter { !listTermIdKnew.contains(it.uid) })
                    }else{
                        remainTerms.addAll(fetchTopic.topic!!.terms)
                    }
                    intent.putExtra("flashcard", fetchFlashCard.flashCard)
                }else{
                    remainTerms.addAll(fetchTopic.topic!!.terms)
                }
                if (optionData.shuffle) {
                    remainTerms.shuffle()
                }
                val answers = fetchTopic.topic!!.terms.map { it.definition }.toMutableList()
                runOnUiThread {
                    if (optionData.answer == EAnswer.TERM){
                        answers.addAll(fetchTopic.topic!!.terms.map { it.term })
                    }
                    intent.putExtra("topic", fetchTopic.topic!!)
                    intent.putExtra("remainTerms", remainTerms as Serializable?)
                    startActivity(intent)
                    finish()
                    actionTransition.moveNextTransition()
                }
            }
        }
    }
}