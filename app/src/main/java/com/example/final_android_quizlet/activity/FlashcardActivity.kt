package com.example.final_android_quizlet.activity

import CustomDragShadowBuilder
import TypeFlashCard
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.DRAG_FLAG_OPAQUE
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionDialog
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.models.FlashCard
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FlashCardService
import com.example.final_android_quizlet.service.FolderService
import com.example.final_android_quizlet.service.TopicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class FlashcardActivity : AppCompatActivity() {

    // Service
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val flashCardService: FlashCardService = FlashCardService()
    private lateinit var textToSpeech: TextToSpeech

    // Layout Learning
    private lateinit var imgExit: ImageView
    private lateinit var imgBackToPreTerm: ImageView
    private lateinit var layoutLearning: ConstraintLayout
    private lateinit var tvProgressBar: TextView
    private lateinit var tvTotalLearning: TextView
    private lateinit var tvTotalKnew: TextView
    private lateinit var cardBackground: CardView
    private lateinit var cardFront: CardView
    private lateinit var cardBack: CardView
    private lateinit var tvFront: TextView
    private lateinit var tvBack: TextView
    private lateinit var tvBackground: TextView
    private lateinit var layoutCard: ConstraintLayout
    private lateinit var areaLearning: ConstraintLayout
    private lateinit var areaKnew: ConstraintLayout
    private lateinit var progressTerm: ProgressBar
    private lateinit var imgSpeakerBack: ImageView
    private lateinit var imgSpeakerFront: ImageView

    // Layout Result
    private lateinit var layoutResult: ConstraintLayout
    private lateinit var tvTotalKnewResult: TextView
    private lateinit var tvTotalTermLeftResult: TextView
    private lateinit var tvTotalLearningResult: TextView
    private lateinit var tvResetFCResult: TextView
    private lateinit var btnExamResult: Button
    private lateinit var btnLearningResult: Button

    // Hard Data
    private var isFront = true

    private lateinit var topicIntent: Topic
    private lateinit var items: List<Term>
    private var flashCardIntent: FlashCard? = null
    private val listIndexLearning: MutableList<Int> = mutableListOf()
    private val listIndexKnew: MutableList<Int> = mutableListOf()
    private var indexCurrentTerm: Int = 0
    private var userId: String? = null
    // Drag

    // Shadow
    private var dragShadowBuilder: CustomDragShadowBuilder? = null


    val dragListener = View.OnDragListener { view, event ->
        val viewDestination = view as ConstraintLayout
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                dragShadowBuilder!!.updateDragShadow(TypeFlashCard.START)
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                if (resources.getResourceName(viewDestination.id).contains("areaLearning", ignoreCase = true)) {
                    // Handle areaLearning
                    dragShadowBuilder!!.updateDragShadow(TypeFlashCard.LEARNING)
                } else {
                    // Handle areaKnew
                    dragShadowBuilder!!.updateDragShadow(TypeFlashCard.KNEW)
                }
                view.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                val currentX = event.x
                val widthFixed = viewDestination.width.toFloat()
                if (resources.getResourceName(viewDestination.id).contains("areaLearning", ignoreCase = true)) {
                    // Handle areaLearning
                    dragShadowBuilder!!.updateWithPosition(TypeFlashCard.LEARNING, currentX, widthFixed)
                } else {
                    // Handle areaKnew
                    dragShadowBuilder!!.updateWithPosition(TypeFlashCard.KNEW, currentX, widthFixed)
                }
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                dragShadowBuilder!!.updateDragShadow(TypeFlashCard.EXIT)
                true
            }

            DragEvent.ACTION_DROP -> {
                val currentX = event.x
                val widthFixed = viewDestination.width.toFloat()
                if (resources.getResourceName(viewDestination.id).contains("areaLearning", ignoreCase = true)) {
                    // Handle areaLearning
                    if (currentX < (widthFixed / 2)) {
                        // Save Temp data to flashcard
                        listIndexLearning.add(indexCurrentTerm)
                        tvTotalLearning.text = listIndexLearning.size.toString()
                        indexCurrentTerm += 1
                        updateProgressBar()
                    }
                } else {
                    // Handle areaKnew
                    if (currentX > (widthFixed / 2)) {
                        // Save Temp data to flashcard
                        listIndexKnew.add(indexCurrentTerm)
                        tvTotalKnew.text = listIndexKnew.size.toString()
                        indexCurrentTerm += 1
                        updateProgressBar()
                    }
                }
                if (indexCurrentTerm == items.size) {
                    val flashCard = if(flashCardIntent == null) saveFlashCard() else saveFlashCardIntent()
                    Log.i("TAG", "flashCard: $flashCard")
                    tvTotalLearningResult.text = flashCard.termsLearning.size.toString()
                    tvTotalKnewResult.text = flashCard.termsKnew.size.toString()
                    tvTotalTermLeftResult.text =
                        (topicIntent.terms.size - (flashCard.termsLearning.size + flashCard.termsKnew.size)).toString()

                    if(flashCard.termsLearning.size == 0){
                        btnExamResult.setOnClickListener {
                            val intent = Intent(this, DetailTopic::class.java)
                            intent.putExtra("openExamChoice", "flashcard")
                            startActivity(intent)
                            finish()
                            actionTransition.rollBackTransition()
                        }
                        btnExamResult.visibility = View.VISIBLE
                        btnLearningResult.visibility = View.GONE
                    }else{
                        btnLearningResult.setOnClickListener {
                            val intent = Intent(this, MainQuizActivity::class.java)
                            intent.putExtra("exercise_type", "flashcard")
                            intent.putExtra("topicId", topicIntent.uid)
                            startActivity(intent)
                            finish()
                            actionTransition.moveNextTransition()
                        }
                        btnLearningResult.text = "Tiếp tục ôn ${flashCard.termsLearning.size} thuật ngữ"
                        btnLearningResult.visibility = View.VISIBLE
                        btnExamResult.visibility = View.GONE
                    }
                    layoutLearning.visibility = View.GONE
                    layoutResult.visibility = View.VISIBLE

                    tvResetFCResult.setOnClickListener {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO){
                                flashCardService.resetFlashCard(flashCard.uid)
                                finish()
                                actionTransition.rollBackTransition()
                                runOnUiThread {
                                    Toast.makeText(this@FlashcardActivity,"Đã đặt lại thẻ ghi nhớ", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }


                    // Update data in background
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            if (flashCardIntent != null) {

                            }
                            val saveFlashCardDb =
                                if (flashCardIntent == null) flashCardService.createFlashCard(flashCard)
//                                else if (flashCard.termsKnew.size == topicIntent.terms.size) flashCardService.resetFlashCard(flashCardIntent!!.uid)
                                else flashCardService.updateFlashCard(flashCardIntent!!.uid, flashCard)
                            if (!saveFlashCardDb.status) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@FlashcardActivity,
                                        saveFlashCardDb.data.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                finish()
                                actionTransition.rollBackTransition()
                            }
                        }
                    }
                } else {
                    dragShadowBuilder!!.updateNewItem(items[indexCurrentTerm])
                }
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                dragShadowBuilder!!.updateDragShadow(TypeFlashCard.END)
                true
            }

            else -> {
                false
            }
        }
    }

    private fun saveFlashCard(): FlashCard {
        val termsLearning = items.filterIndexed { index, term -> listIndexLearning.contains(index) }.toMutableList()
        val termsKnew = items.filterIndexed { index, term -> listIndexKnew.contains(index) }.toMutableList()
        val currentTermId = items[indexCurrentTerm - 1].uid
        return FlashCard(
            UUID.randomUUID().toString(), termsLearning,
            termsKnew, currentTermId, topicIntent.uid, userId!!
        )
    }
    private fun saveFlashCardIntent(): FlashCard {
        // add learning term or leftTerm to knew term
        val termsKnew = items.filterIndexed { index, term ->  listIndexKnew.contains(index)  }
        val termsLearning = items.filterIndexed { index, term -> listIndexLearning.contains(index) }
        val listIdKnewDb = flashCardIntent!!.termsKnew.map { x -> x.uid }
        val list = termsKnew.filter {
            !listIdKnewDb.contains(it.uid)
        }
        flashCardIntent!!.termsKnew.addAll(list)
        flashCardIntent!!.termsLearning = flashCardIntent!!.termsLearning.filter {
            !termsKnew.map { x -> x.uid }.contains(it.uid)
        }.toMutableList()

        // add to learn
        val listIdLearnDb = flashCardIntent!!.termsLearning.map { x -> x.uid }
        val listTemp = termsLearning.filter {
            !listIdLearnDb.contains(it.uid)
        }
        flashCardIntent!!.termsLearning.addAll(listTemp)
        flashCardIntent!!.termsKnew = flashCardIntent!!.termsKnew.filter {
            !termsLearning.map { x -> x.uid }.contains(it.uid)
        }.toMutableList()
        return this.flashCardIntent!!
    }

    private fun updateProgressBar() {
        if (indexCurrentTerm < items.size) {
            tvProgressBar.text = "${indexCurrentTerm + 1} / ${items.size}"
        }
        val percent =
            (((listIndexLearning.size.toFloat() + listIndexKnew.size.toFloat()) / items.size.toFloat()) * 100).toInt()
        progressTerm.setProgress(percent, true)
    }


    fun moveBackTerm() {
        indexCurrentTerm -= 1
        dragShadowBuilder!!.updateNewItem(items[indexCurrentTerm])
        if (listIndexLearning.contains(indexCurrentTerm)) {
            listIndexLearning.remove(indexCurrentTerm)
            tvTotalLearning.text = listIndexLearning.size.toString()
        }
        else {
            listIndexKnew.remove(indexCurrentTerm)
            tvTotalKnew.text = listIndexKnew.size.toString()
        }
        updateProgressBar()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
        }

        if (intent.getSerializableExtra("topic") == null || intent.getSerializableExtra("remainTerms") == null) {
            finish()
            actionTransition.rollBackTransition()
            Toast.makeText(this, "Some thing error, please try again!!!", Toast.LENGTH_LONG).show()

            Log.i("TAG", "Topic received FlashCard: $topicIntent")
        }
        // Learning layout
        imgExit = findViewById(R.id.img_exit_flashcard)
        layoutLearning = findViewById(R.id.layoutStudy_flashcard)
        imgBackToPreTerm = findViewById(R.id.img_BackToPreTerm_flashcard)
        tvProgressBar = findViewById(R.id.tvProgress_flashcard)
        tvTotalLearning = findViewById(R.id.tvTotalLearning_flashcard)
        tvTotalKnew = findViewById(R.id.tvTotalKnew_flashcard)
        layoutCard = findViewById(R.id.layoutCard_Flashcard)
        cardFront = findViewById(R.id.cardFront_flashcard)
        cardBack = findViewById(R.id.cardBack_flashcard)
        cardBackground = findViewById(R.id.cardBackground_flashcard)
        tvFront = findViewById(R.id.tvFront_Flashcard)
        tvBack = findViewById(R.id.tvBack_Flashcard)
        tvBackground = findViewById(R.id.tvBackground_Flashcard)
        areaLearning = findViewById(R.id.areaLearning)
        areaKnew = findViewById(R.id.areaKnew)
        progressTerm = findViewById(R.id.progressBarTerm_flashcard)
        imgSpeakerFront = findViewById(R.id.imgSpeakerFront_flashCard)
        imgSpeakerBack = findViewById(R.id.imgSpeakerBack_flashCard)


        // Result layout
        layoutResult = findViewById(R.id.layoutResult_flashcard)
        tvTotalTermLeftResult = findViewById(R.id.tvTotalTermLeftResult_flashcard)
        tvTotalLearningResult = findViewById(R.id.tvTotalLearningResult_flashcard)
        tvTotalKnewResult = findViewById(R.id.tvTotalKnewResult_flashcard)
        tvResetFCResult = findViewById(R.id.tv_resetFCResult_flashcard)
        btnExamResult = findViewById(R.id.btn_examResult_flashcard)
        btnLearningResult = findViewById(R.id.btn_LearningResult_flashcard)
        // Load data
        dragShadowBuilder = CustomDragShadowBuilder(cardBackground)

        // TextToSpeech
        textToSpeech = TextToSpeech(this){ status ->
            if (status == TextToSpeech.SUCCESS){
                val result = textToSpeech.setLanguage(Locale.forLanguageTag(topicIntent.termLanguage!!))
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(this, "Oops language not Supported!!!", Toast.LENGTH_LONG).show()
                }
            }
        }
        imgSpeakerFront.setOnClickListener {
            speak()
        }
        imgSpeakerBack.setOnClickListener {
            speak()
        }

        topicIntent = intent.getSerializableExtra("topic") as Topic
        items = intent.getSerializableExtra("remainTerms") as List<Term>
        if (intent.getSerializableExtra("flashcard") != null) {
            flashCardIntent = intent.getSerializableExtra("flashcard") as FlashCard
        }
        Log.i("TAG", "onCreate: items: $items")
        userId = authService.getCurrentUser().uid

        // Load view
        dragShadowBuilder!!.updateNewItem(items[0])


        // Animate
        var scale = applicationContext.resources.displayMetrics.density

        cardFront.cameraDistance = 8000 * scale
        cardBack.cameraDistance = 8000 * scale

        val frontAnimation =
            AnimatorInflater.loadAnimator(applicationContext, R.anim.front_animator_horizontal) as AnimatorSet
        val backAnimation =
            AnimatorInflater.loadAnimator(applicationContext, R.anim.back_animator_horizontal) as AnimatorSet

        layoutCard.setOnClickListener {
            flipCard(frontAnimation, backAnimation)
        }

        // Handle Event
        areaLearning.setOnDragListener(dragListener)
        areaKnew.setOnDragListener(dragListener)

        tvProgressBar.text = "${indexCurrentTerm + 1} / ${items.size}"

        layoutCard.setOnLongClickListener {
            it.startDragAndDrop(null, dragShadowBuilder, it, DRAG_FLAG_OPAQUE)
            true
        }

        imgBackToPreTerm.setOnClickListener {
            moveBackTerm()
        }

        imgExit.setOnClickListener {
            finish()
            actionTransition.rollBackTransition()
        }
        // Call Back
    }
    fun speak(){
        if(isFront){
            textToSpeech.setLanguage(Locale.forLanguageTag(topicIntent.termLanguage!!))
            textToSpeech.speak(tvFront.text.trim().toString(), TextToSpeech.QUEUE_FLUSH, null)
        }else{
            textToSpeech.setLanguage(Locale.forLanguageTag(topicIntent.definitionLanguage!!))
            textToSpeech.speak(tvBack.text.trim().toString(), TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun flipCard(frontAnimation: AnimatorSet, backAnimation: AnimatorSet) {
        if (isFront) {
            frontAnimation.setTarget(cardFront);
            backAnimation.setTarget(cardBack);
            frontAnimation.start()
            backAnimation.start()
            isFront = false

        } else {
            frontAnimation.setTarget(cardBack)
            backAnimation.setTarget(cardFront)
            backAnimation.start()
            frontAnimation.start()
            isFront = true
        }
    }
}