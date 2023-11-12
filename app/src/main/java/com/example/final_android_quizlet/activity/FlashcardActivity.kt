package com.example.final_android_quizlet.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.final_android_quizlet.R

class FlashcardActivity : AppCompatActivity() {

    private lateinit var cardFront: CardView
    private lateinit var cardBack: CardView
    private lateinit var tvFront: TextView
    private lateinit var tvBack: TextView
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        cardFront = findViewById(R.id.cardFront_flashcard)
        cardBack = findViewById(R.id.cardBack_flashcard)
        tvFront = findViewById(R.id.tvFront_Flashcard)
        tvBack = findViewById(R.id.tvBack_Flashcard)

        // Animate
        var scale = applicationContext.resources.displayMetrics.density

        cardFront.cameraDistance = 8000 * scale
        cardBack.cameraDistance = 8000 * scale

        val frontAnimation = AnimatorInflater.loadAnimator(applicationContext, R.anim.front_animator_horizontal) as AnimatorSet
        val backAnimation = AnimatorInflater.loadAnimator(applicationContext, R.anim.back_animator_horizontal) as AnimatorSet

        cardFront.setOnClickListener {
            flipCard(frontAnimation, backAnimation)
        }
        cardBack.setOnClickListener {
            flipCard(frontAnimation, backAnimation)
        }
    }
    private fun flipCard(frontAnimation: AnimatorSet, backAnimation: AnimatorSet){
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