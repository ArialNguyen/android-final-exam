package com.example.final_android_quizlet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RankingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragmentArrayList: MutableList<Fragment> = ArrayList()
    private val fragmentTitleList: MutableList<String> = ArrayList()

    fun addFragment(fragment: Fragment, title: String) {
        fragmentArrayList.add(fragment)
        fragmentTitleList.add(title)
    }

    override fun getItemCount(): Int {
        return fragmentArrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    public fun getTabTitle(position: Int): String {
        return fragmentTitleList[position]
    }

}