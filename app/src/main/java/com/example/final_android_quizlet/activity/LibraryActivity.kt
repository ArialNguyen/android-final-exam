package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.final_android_quizlet.MainActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.LibraryAdapter
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.fragments.FragmentTopicLibrary
import com.example.final_android_quizlet.fragments.Fragment_LopHoc
import com.example.final_android_quizlet.fragments.FragmentFolderLibrary
import com.example.final_android_quizlet.service.AuthService
import com.google.android.material.tabs.TabLayout

class LibraryActivity : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private lateinit var tabLibrary: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)


        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
        }

        tabLibrary = findViewById(R.id.tabLibrary)
        viewPager = findViewById(R.id.viewPager)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_library)
        setSupportActionBar(toolbar)

        tabLibrary.setupWithViewPager(viewPager)

        val libraryAdapter = LibraryAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        libraryAdapter.addFragment(FragmentTopicLibrary(this), "Học Phần")
        libraryAdapter.addFragment(FragmentFolderLibrary(this), "Thư Mục")
        libraryAdapter.addFragment(Fragment_LopHoc(this), "Lớp Học")
        viewPager.adapter = libraryAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_library, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_Algorithm -> {
                val intent = Intent(this, CreateTermActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}