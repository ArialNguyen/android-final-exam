package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionDialog
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.service.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class MainQuizActivity : AppCompatActivity() {

    private val choiceService: MultipleChoiceService = MultipleChoiceService()
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val flashCardService: FlashCardService = FlashCardService()
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_quiz)

        val exerciseType = intent.getStringExtra("exercise_type")

        if (exerciseType == "choice") {
            choiceTest()
        } else if (exerciseType == "write") {
//            val intent = Intent(this, QuizActivity::class.java)
//            intent.putExtra("exercise_type", "write")
//            startActivity(intent)
        }else{
            flashCard()
        }

    }
    private fun choiceTest(){
        val intent = Intent(this, ChoiceTest::class.java)
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
                val fetchChoice = choiceService.MPForUserLogged().findChoiceTestByTopicId(topicId)
                if(fetchChoice.status){
                    intent.putExtra("choice", fetchTopic.testChoice)
                }
                runOnUiThread {
                    intent.putExtra("topic", fetchTopic.topic!!)
                    intent.putExtra("answers", fetchTopic.topic!!.terms.map { it.definition } as Serializable)
                    startActivity(intent)
                    finish()
                    actionTransition.moveNextTransition()
                }
            }
        }
    }

    private fun flashCard(){
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
                runOnUiThread {
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