package com.example.final_android_quizlet.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.final_android_quizlet.R
import me.relex.circleindicator.CircleIndicator3

class Detail_HocPhan_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hocphan)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_detail_hocphan)
        val textVocabulary = findViewById<TextView>(R.id.text_vocabulary)
        val indicator = findViewById<CircleIndicator3>(R.id.indicator)
        setSupportActionBar(toolbar)

        textVocabulary.setOnClickListener{
            val anime_1 = ObjectAnimator.ofFloat(textVocabulary, "scaleX", 1f, 0f)
            val anime_2 = ObjectAnimator.ofFloat(textVocabulary, "scaleX", 0f, 1f)

            anime_1.interpolator = DecelerateInterpolator()
            anime_2.interpolator = DecelerateInterpolator()

            anime_1.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (textVocabulary.text == "Xin chào") {
                        textVocabulary.text = "Hello"
                    } else {
                        textVocabulary.text = "Xin chào"
                    }
                    anime_2.start()
                }
            })
            anime_1.start()
        }

        indicator;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail_hocphan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_inFolder -> {
                Toast.makeText(this, "Add In Folder", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.fix_hocphan -> {
                Toast.makeText(this, "Sửa Học Phần", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.remove_hocphan -> {
                Toast.makeText(this, "Xóa Học Phần", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}