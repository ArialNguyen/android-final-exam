package com.example.final_android_quizlet.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.final_android_quizlet.R
import android.view.animation.DecelerateInterpolator

class FlashcardActivity : AppCompatActivity() {

    private lateinit var cardDTFlashcard: CardView
    private lateinit var cardDNFlashcard: CardView
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        cardDTFlashcard = findViewById(R.id.cardDT_flashcard)
        cardDNFlashcard = findViewById(R.id.cardDN_flashcard)

        // Gán sự kiện click cho các thẻ
        cardDTFlashcard.setOnClickListener { onCardClick(cardDTFlashcard) }
        cardDNFlashcard.setOnClickListener { onCardClick(cardDNFlashcard) }
    }

    private fun onCardClick(cardView: CardView) {
        val textInsideCard = cardView.findViewById<TextView>(R.id.textInsideCard) // Chỉnh lại ID của TextView trong layout XML của thẻ
        val scaleAnime = ObjectAnimator.ofFloat(textInsideCard, "scaleX", 1f, 0f)
        scaleAnime.duration = 500 // Thời gian của hiệu ứng (milliseconds)
        scaleAnime.interpolator = DecelerateInterpolator()

        scaleAnime.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Xử lý khi bắt đầu animation (nếu cần)
            }

            override fun onAnimationEnd(animation: Animator) {
                // Xử lý khi animation kết thúc, thay đổi nội dung và thực hiện hiệu ứng scale ngược lại
                if (isFront) {
                    textInsideCard.text = "Xin chào"
                } else {
                    textInsideCard.text = "Hello"
                }
                isFront = !isFront

                val scaleAnimeReverse = ObjectAnimator.ofFloat(textInsideCard, "scaleX", 0f, 1f)
                scaleAnimeReverse.duration = 500
                scaleAnimeReverse.interpolator = DecelerateInterpolator()
                scaleAnimeReverse.start()
            }

            override fun onAnimationCancel(animation: Animator) {
                // Xử lý khi animation bị hủy (nếu cần)
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Xử lý khi animation lặp lại (nếu cần)
            }
        })

        // Khởi chạy hiệu ứng scale
        scaleAnime.start()
    }
}