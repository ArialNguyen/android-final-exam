package com.example.final_android_quizlet.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.example.final_android_quizlet.service.TopicService.TopicForUserLogged
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator3
import kotlin.math.log

class DetailTopic : AppCompatActivity() {

    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()


    private var toolbar: Toolbar? = null
    private var textVocabulary: TextView? = null
    private var indicator: CircleIndicator3? = null
    private var cvFlashCard: CardView? = null
    private var cvChoice: CardView? = null
    private var cvWriteText: CardView? = null
    private var tvTopicName: TextView? = null
    private var tvUserName: TextView? = null
    private var avatarUser: CircleImageView? = null
    private var tvDecription: TextView? = null
    private var tvTotalTerm: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hocphan)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
            actionTransition.moveNextTransition()
        }


        toolbar = findViewById(R.id.toolbar_detail_hocphan)
        textVocabulary = findViewById(R.id.text_vocabulary)
        indicator = findViewById(R.id.indicator)
        cvFlashCard = findViewById(R.id.cardview_flashcard)
        cvChoice = findViewById(R.id.cardview_choice)
        cvWriteText = findViewById(R.id.cardview_writeText)

        tvTopicName = findViewById(R.id.tvTopicName_DetailTopic)
        tvDecription = findViewById(R.id.tvDescriptionTopic_DetailTopic)
        tvTotalTerm = findViewById(R.id.tvTotalTerm_DetailTopic)
        avatarUser = findViewById(R.id.imgAvatarIcon_DetailTopic)
        tvUserName = findViewById(R.id.tvUserName_DetailTopic)

        if(intent.getStringExtra("topicId") == null || intent.getStringExtra("topicId")!!.isEmpty()){
            Toast.makeText(this, "Something error... Try again!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }


        lifecycleScope.launch {
            val topicId = intent.getStringExtra("topicId")!!
            val topic = topicService.TopicForUserLogged().getTopicById(topicId).topic!!
            val user = authService.getUserLogin().user!!
            tvTopicName!!.text = topic.title
            tvDecription!!.text = topic.description
            tvTotalTerm!!.text = "${topic.terms.size} thuật ngữ"
            tvUserName!!.text = user.name
            Picasso.get().load(user.avatar).into(avatarUser)
        }


        cvChoice!!.setOnClickListener {
            val intent = Intent(this, MainQuizActivity::class.java)
            intent.putExtra("exercise_type", "quiz")
            startActivity(intent)
        }

        cvWriteText!!.setOnClickListener {
            val intent = Intent(this, MainQuizActivity::class.java)
            intent.putExtra("exercise_type", "write")
            startActivity(intent)
        }
        setSupportActionBar(toolbar)

        textVocabulary!!.setOnClickListener {
            val anime_1 = ObjectAnimator.ofFloat(textVocabulary, "scaleX", 1f, 0f)
            val anime_2 = ObjectAnimator.ofFloat(textVocabulary, "scaleX", 0f, 1f)

            anime_1.interpolator = DecelerateInterpolator()
            anime_2.interpolator = DecelerateInterpolator()

            anime_1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (textVocabulary!!.text == "Xin chào") {
                        textVocabulary!!.text = "Hello"
                    } else {
                        textVocabulary!!.text = "Xin chào"
                    }
                    anime_2.start()
                }
            })
            anime_1.start()
        }

        cvFlashCard!!.setOnClickListener {
            val intent = Intent(this, FlashcardActivity::class.java)
            startActivity(intent)
        }

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