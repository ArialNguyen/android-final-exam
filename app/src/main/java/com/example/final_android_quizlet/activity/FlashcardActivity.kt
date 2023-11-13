package com.example.final_android_quizlet.activity

import CustomDragShadowBuilder
import TypeFlashCard
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.DRAG_FLAG_OPAQUE
import android.view.ViewGroup
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
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.example.final_android_quizlet.service.TopicService
import java.util.*

class FlashcardActivity : AppCompatActivity() {

    // Service
    private val folderService: FolderService = FolderService()
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)

    private lateinit var cardBackground: CardView
    private lateinit var cardFront: CardView
    private lateinit var cardBack: CardView
    private lateinit var tvFront: TextView
    private lateinit var tvBack: TextView
    private lateinit var tvBackground: TextView
    private lateinit var tvAboveBG: TextView
    private lateinit var layoutCard: ConstraintLayout
    private lateinit var areaLearning: ConstraintLayout
    private lateinit var areaKnew: ConstraintLayout

    private var isFront = true

    // Hard Data
    private lateinit var topic: Topic
    private lateinit var items: List<Term>

    // Drag
    private val uid: UUID = UUID.randomUUID()

    // Shadow
    private var dragShadowBuilder: CustomDragShadowBuilder? = null


    val dragListener = View.OnDragListener { view, event ->
        val viewDestination = view as ConstraintLayout
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                tvFront.visibility = View.INVISIBLE
                dragShadowBuilder!!.updateDragShadow(TypeFlashCard.START)
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.i("TAG", "ACTION_DRAG_ENTERED: ")
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
//                view.invalidate()
                val currentX = event.x
                val widthFixed = viewDestination.width.toFloat()
                val halfWidth = widthFixed / 2;
                Log.i("TAG", "halfWidth: $halfWidth")
                Log.i("TAG", "ACTION_DRAG_LOCATION. currentX = $currentX")
                Log.i("TAG", "ACTION_DRAG_LOCATION. WidthX = ${viewDestination.width}")
                if (resources.getResourceName(viewDestination.id).contains("areaLearning", ignoreCase = true)) {
                    // Handle areaLearning
                    dragShadowBuilder!!.updateWithPosition(TypeFlashCard.LEARNING,  currentX, widthFixed)

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
                val item = event.clipData.getItemAt(0)
                val text = item.text
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
//                view.invalidate()
                val viewBeforeDrag = event.localState as View
                val owner = viewBeforeDrag.parent as ViewGroup

                val viewDestination = view as ConstraintLayout
                Log.i("TAG", "viewDestination: ${resources.getResourceName(viewDestination.id)}")
                if (resources.getResourceName(viewDestination.id).contains("areaLearning", ignoreCase = true)) {
                    // Handle areaLearning
                } else {
                    // Handle areaKnew
                }
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
//                view.invalidate()
                tvFront.visibility = View.VISIBLE
                dragShadowBuilder!!.updateDragShadow(TypeFlashCard.END)
                true
            }

            else -> {
                false
            }
        }
    }
    fun rollBackDragShadowBegin(){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
        }

        if (intent.getSerializableExtra("topic") != null) {
            topic = intent.getSerializableExtra("topic") as Topic
            items = topic.terms
            Log.i("TAG", "Topic received FlashCard: $topic")
        }

        layoutCard = findViewById(R.id.layoutCard_Flashcard)
        cardFront = findViewById(R.id.cardFront_flashcard)
        cardBack = findViewById(R.id.cardBack_flashcard)
        cardBackground = findViewById(R.id.cardBackground_flashcard)
        tvFront = findViewById(R.id.tvFront_Flashcard)
        tvBack = findViewById(R.id.tvBack_Flashcard)
        tvBackground = findViewById(R.id.tvBackground_Flashcard)
        areaLearning = findViewById(R.id.areaLearning)
        areaKnew = findViewById(R.id.areaKnew)
        // Load data
        items = listOf(Term("IT", "Information Technology"))

        tvFront.text = items[0].term
        tvBack.text = items[0].definition
        tvBackground.text = items[0].term
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

        // Handle Drag
        areaLearning.setOnDragListener(dragListener)
        areaKnew.setOnDragListener(dragListener)

        layoutCard.setOnLongClickListener {
            val clipText = "some thing"
            val items = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, items)
            dragShadowBuilder = CustomDragShadowBuilder(cardBackground)
            it.startDragAndDrop(data, dragShadowBuilder, it, DRAG_FLAG_OPAQUE)
            true
        }
        // Call Back
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