package com.example.final_android_quizlet.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.AddTopicFolderAdapter
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.fragments.FragmentAddTopic_CreateFolder
import com.example.final_android_quizlet.fragments.FragmentAddTopic_LearnFolder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AddTopic_FolderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtopic_folder_infolder)

        val tabAddFolder = findViewById<TabLayout>(R.id.tabAddTopic_AddTopic_FolderActivity)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager_AddTopic_FolderActivity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_AddTopic_FolderActivity)
        val imgBack = findViewById<ImageView>(R.id.imgBack_AddTopic_FolderActivity)
        setSupportActionBar(toolbar)

        val adapter = AddTopicFolderAdapter(this)
        val fragmentCreate = FragmentAddTopic_CreateFolder(this, object :
            GetBackAdapterFromViewPager{})
        val fragmentLearn = FragmentAddTopic_LearnFolder()

        adapter.addFragment(fragmentCreate, "Đã tạo")
        adapter.addFragment(fragmentLearn, "Đã học")

        viewPager.adapter = adapter

        TabLayoutMediator(tabAddFolder, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}