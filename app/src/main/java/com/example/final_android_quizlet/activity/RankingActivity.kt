package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.*
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.adapter.data.RankingItem
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.fragments.RankingFragment
import com.example.final_android_quizlet.models.QuizWrite
import com.example.final_android_quizlet.models.User
import com.example.final_android_quizlet.service.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingActivity: AppCompatActivity() {
    // Service
    private val userService: UserService = UserService()
    private val topicService: TopicService = TopicService()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val mpService: MultipleChoiceService = MultipleChoiceService()
    private val wTService: QuizWriteService = QuizWriteService()
    // View
    private lateinit var avatarTop: CircleImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    // Adapter
    private lateinit var adapterVP: VPRankingAdapter
    private lateinit var mpRankingAdapter: RankingAdapter
    private lateinit var wTRankingAdapter: RankingAdapter

    private val mpRankingItem: MutableList<RankingItem> = mutableListOf()
    private val wtRankingItem: MutableList<RankingItem> = mutableListOf()

    // Hard data
    private lateinit var topicId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        // Handle Intent
        if(intent.getStringExtra("topicId") == null){
            Toast.makeText(this, "Oops, something wrong. Try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        topicId = intent.getStringExtra("topicId")!!

        // Get View
        avatarTop = findViewById(R.id.imgAvatarTop_RankingActivity)
        tabLayout = findViewById(R.id.tab_ranking)
        viewPager = findViewById(R.id.viewPager_ranking)
        val imgBack = findViewById<ImageView>(R.id.imgBack_RankingActivity)

        // Add TabLayout
        val tab1 = tabLayout.newTab()
        tab1.setText("Multiple Choice")
        val tab2 = tabLayout.newTab()
        tab2.setText("Writing Test")
        tabLayout.addTab(tab1)
        tabLayout.addTab(tab2)
        // Adapter
        adapterVP = VPRankingAdapter(this)
        mpRankingAdapter = RankingAdapter(mpRankingItem)
        wTRankingAdapter = RankingAdapter(wtRankingItem)
        // Handle Click Adapter

        // Init Fragment
        val mpFragment = RankingFragment(object : GetBackAdapterFromViewPager{
            override fun onActionBack(view: View) {
                actionOnMpFG(view)
            }
        })
        val wTFragment = RankingFragment(object : GetBackAdapterFromViewPager{
            override fun onActionBack(view: View) {
                actionOnWtFG(view)
            }
        })

        // Add Fragment to VP
        adapterVP.addFragment(mpFragment, "Multiple Choice")
        adapterVP.addFragment(wTFragment, "Writing Test")

        viewPager.adapter = adapterVP

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapterVP.getTabTitle(position)
        }.attach() // Connect viewPager and Tab

        // Handle Event
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun actionOnMpFG(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_ranking)
        recyclerView.adapter = mpRankingAdapter
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fetchMp = mpService.findChoiceTestByTopicId(topicId)
                if(fetchMp.status){
                    val topicsPassed = fetchMp.testChoices!!.filter {
                        it.optionExam.numberQues == it.totalQuestion && it.overall.toInt() > 0
                    }.map { it.userId }
                    if(topicsPassed.isNotEmpty()){
                        val fetchUsers = userService.getUsersInListUserId(topicsPassed)
                        if(fetchUsers.status){
                            mpRankingItem.addAll(
                                fetchMp.testChoices!!.map {
                                    RankingItem(fetchUsers.users!!.first { user -> user.uid == it.userId }, it, null)
                                }
                            )
                            runOnUiThread {
                                mpRankingAdapter.notifyDataSetChanged()
                            }
                        }else{
                            runOnUiThread {
                                Toast.makeText(this@RankingActivity, fetchUsers.data.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@RankingActivity, fetchMp.data.toString(), Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }
    fun actionOnWtFG(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_ranking)
        recyclerView.adapter = wTRankingAdapter
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fetchWrite = wTService.findWritingTestByTopicId(topicId)
                if(fetchWrite.status){
                    val topicsPassed = fetchWrite.quizWrites!!.filter {
                        it.optionExam.numberQues == it.totalQuestion && it.overall.toInt() > 0
                    }.map { it.userId }
                    if(topicsPassed.isNotEmpty()){
                        val fetchUsers = userService.getUsersInListUserId(topicsPassed)
                        if(fetchUsers.status){
                            wtRankingItem.addAll(
                                fetchWrite.quizWrites!!.map {
                                    RankingItem(fetchUsers.users!!.first { user -> user.uid == it.userId }, null, it)
                                }
                            )
                            runOnUiThread {
                                wTRankingAdapter.notifyDataSetChanged()
                            }
                        }else{
                            runOnUiThread {
                                Toast.makeText(this@RankingActivity, fetchUsers.data.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }else{
                    runOnUiThread {
                        Toast.makeText(this@RankingActivity, fetchWrite.data.toString(), Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }


}