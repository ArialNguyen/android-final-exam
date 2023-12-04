package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
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

        // Get View
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
                    val fetchUsers = userService.getUsersInListUserId(fetchMp.testChoices!!.map { it.userId })
                    if(fetchUsers.status){

                    }else{
                        runOnUiThread {
                            Toast.makeText(this@RankingActivity, fetchUsers.data.toString(), Toast.LENGTH_LONG).show()
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

    }


}