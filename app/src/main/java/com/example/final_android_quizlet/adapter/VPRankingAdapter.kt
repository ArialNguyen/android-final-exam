package com.example.final_android_quizlet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.adapter.data.RankingItem
import com.example.final_android_quizlet.common.AdapterAndItems
import com.example.final_android_quizlet.models.User

class VPRankingAdapter(activity: FragmentActivity): FragmentStateAdapter(activity)  {
    private val fragmentArrayList: MutableList<Fragment> = ArrayList()
    private val fragmentTitleList: MutableList<String> = ArrayList()
    private val adaptersChild: MutableList<AdapterAndItems> = ArrayList()

    fun addFragment(fragment: Fragment, title: String) {
        fragmentArrayList.add(fragment)
        fragmentTitleList.add(title)
    }

    fun getAdapter(position: Int): AdapterAndItems {
        return adaptersChild[position]
    }

    fun addAdapterForChild(items: MutableList<RankingItem>, adapter: RankingAdapter) {
        adaptersChild.add(AdapterAndItems(items, null, adapter))
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