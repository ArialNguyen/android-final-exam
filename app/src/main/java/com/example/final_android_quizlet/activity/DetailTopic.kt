package com.example.final_android_quizlet.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.DetailTopicHoriAdapter
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.HorizontalSpaceItemDecoration
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator2
import me.relex.circleindicator.CircleIndicator3


class DetailTopic : AppCompatActivity() {

    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()


    private var toolbar: Toolbar? = null
    private var tvTerm: TextView? = null
    private var indicator2: CircleIndicator2? = null
    private var cvFlashCard: CardView? = null
    private var cvChoice: CardView? = null
    private var cvWriteText: CardView? = null
    private var tvTopicName: TextView? = null
    private var tvUserName: TextView? = null
    private var avatarUser: CircleImageView? = null
    private var tvDecription: TextView? = null
    private var tvTotalTerm: TextView? = null

    // Adapter
    private var recyclerViewHorizontal: RecyclerView? = null
    private val items: MutableList<Term> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hocphan)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
            actionTransition.moveNextTransition()
        }


        toolbar = findViewById(R.id.toolbar_detail_hocphan)
        tvTerm = findViewById(R.id.tv_Term_TopicDetail)
        recyclerViewHorizontal = findViewById(R.id.recyclerView_DetailTopic)
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

        // Recycler View
        val adapter = DetailTopicHoriAdapter(items)
        recyclerViewHorizontal = findViewById(R.id.recyclerView_DetailTopic)
        recyclerViewHorizontal!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHorizontal!!.adapter = adapter
        val horizontalSpaceItemDecoration = HorizontalSpaceItemDecoration()
        recyclerViewHorizontal!!.addItemDecoration(horizontalSpaceItemDecoration)
        // CircleIndicator2
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerViewHorizontal)

        indicator2 = findViewById(R.id.indicator2_DetailTopic)
        indicator2!!.attachToRecyclerView(recyclerViewHorizontal!!, pagerSnapHelper)
        adapter.registerAdapterDataObserver(indicator2!!.adapterDataObserver); // Need to have this line to update data


        cvFlashCard!!.setOnClickListener {
            val intent = Intent(this, FlashcardActivity::class.java)
            startActivity(intent)
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
            items.addAll(topic.terms)
            adapter.notifyDataSetChanged()
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