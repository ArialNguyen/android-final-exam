package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.RankingAdapter
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.service.AuthService
import com.google.android.material.tabs.TabLayout

class RankingActivity: AppCompatActivity() {
    private val authService: AuthService = AuthService()

    private lateinit var tabRanking: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var rankingAdapter: RankingAdapter

    private var optionsAddInMenu: Number = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val imgBack = findViewById<ImageView>(R.id.imgBack_RankingActivity)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
        }

//        tabRanking = findViewById(R.id.tabRanking)
//        viewPager = findViewById(R.id.viewPager_ranking)
//        val toolbar = findViewById<Toolbar>(R.id.toolbar_RankingActivity)
//        setSupportActionBar(toolbar)
//
//        rankingAdapter = RankingAdapter(this)
//        rankingAdapter.addFragment(Fragment_ranking_choice(), "Trắc nghiệm")
//        rankingAdapter.addFragment(Fragment_ranking_writting(), "Writting")
//
//        viewPager.adapter = rankingAdapter
//
//        TabLayoutMediator(tabRanking, viewPager){tab, position ->
//            tab.text = rankingAdapter.getTabTitle(position)
//            tab.view.setOnClickListener {
//                optionsAddInMenu = position
//            }
//        }.attach()

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}