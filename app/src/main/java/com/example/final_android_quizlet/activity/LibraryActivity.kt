package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.LibraryAdapter
import com.example.final_android_quizlet.fragments.Fragment_HocPhan
import com.example.final_android_quizlet.fragments.Fragment_LopHoc
import com.example.final_android_quizlet.fragments.Fragment_ThuMuc
import com.google.android.material.tabs.TabLayout

class LibraryActivity : AppCompatActivity() {
    private lateinit var tabLibrary: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        tabLibrary = findViewById(R.id.tabLibrary)
        viewPager = findViewById(R.id.viewPager)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_library)
        setSupportActionBar(toolbar)

        tabLibrary.setupWithViewPager(viewPager)

        val libraryAdapter = LibraryAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        libraryAdapter.addFragment(Fragment_HocPhan(), "Học Phần")
        libraryAdapter.addFragment(Fragment_ThuMuc(), "Thư Mục")
        libraryAdapter.addFragment(Fragment_LopHoc(), "Lớp Học")
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