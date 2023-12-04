package com.example.final_android_quizlet.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.TopicAdapter
import com.example.final_android_quizlet.adapter.UserAdapter
import com.example.final_android_quizlet.adapter.VPCommunityAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.EOrientationRecyclerView
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.fragments.DefaultFragmentRv
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User
import com.example.final_android_quizlet.service.TopicService
import com.example.final_android_quizlet.service.UserService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchCommunity : AppCompatActivity() {
    // Service
    private val userService: UserService = UserService()
    private val topicService: TopicService = TopicService()
    private val actionTransition: ActionTransition = ActionTransition(this)


    // View
    private lateinit var searchView: SearchView

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    // Adapter
    private lateinit var adapterVP: VPCommunityAdapter
    private lateinit var topicAdapter: TopicAdapter
    private lateinit var userAdapter: UserAdapter

    private val topicItems: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private val userItems: MutableList<User> = mutableListOf()


    // Hard data
    private val allTopic: MutableList<Topic> = mutableListOf()
    private val allUser: MutableList<User> = mutableListOf()
    private lateinit var keywordIntent: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_community)

        // Hand postData
        if (intent.getStringExtra("keyword") == null) {
            Toast.makeText(this, "Oops something wrong, try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        keywordIntent = intent.getStringExtra("keyword").toString()

        // get View
        searchView = findViewById(R.id.searchView_community)
        tabLayout = findViewById(R.id.tab_community)
        viewPager = findViewById(R.id.viewPager_community)

        // LoadView
        searchView.clearFocus()
        // TabLayout
        val tab1 = tabLayout.newTab()
        tab1.setText("Học phần")
        val tab2 = tabLayout.newTab()
        tab2.setText("Người dùng")
        tabLayout.addTab(tab1)
        tabLayout.addTab(tab2)
        // Adapter
        adapterVP = VPCommunityAdapter(this)
        userAdapter = UserAdapter(userItems)
        topicAdapter = TopicAdapter(EOrientationRecyclerView.VERTICAL, topicItems)

        val topicFragment = DefaultFragmentRv(object : GetBackAdapterFromViewPager {
            override fun onActionBack(view: View) {
                actionOnTopicFM(view)
            }
        })
        val userFragment = DefaultFragmentRv(object : GetBackAdapterFromViewPager {
            override fun onActionBack(view: View) {
                actionOnUserFM(view)
            }
        })
        adapterVP.addFragment(topicFragment, "Học phần")

        adapterVP.addFragment(userFragment, "Người dùng")

        viewPager.adapter = adapterVP

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapterVP.getTabTitle(position)
        }.attach() // Connect viewPager and Tab

        // Handle Event
        searchView.setOnCloseListener {
            Log.i("TAG", "setOnCloseListener: ")
            topicItems.clear()
            userItems.clear()
            topicItems.addAll(
                allTopic.map { topic ->
                    LibraryTopicAdapterItem(
                        topic, allUser.firstOrNull { topic.userId == it.uid }
                    )
                }
            )
            userItems.addAll(allUser)
            topicAdapter.notifyDataSetChanged()
            userAdapter.notifyDataSetChanged()
            searchView.clearFocus()
            false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.isNotEmpty()) {
                    // Topic
                    topicItems.clear()
                    userItems.clear()
                    topicItems.addAll(allTopic.filter { it.title.contains(query, ignoreCase = true) }.map { topic ->
                        LibraryTopicAdapterItem(
                            topic, allUser.firstOrNull { topic.userId == it.uid }
                        )
                    })
                    userItems.addAll(allUser.filter { it.name!!.contains(query, ignoreCase = true) })
                    topicAdapter.notifyDataSetChanged()
                    userAdapter.notifyDataSetChanged()
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun actionOnTopicFM(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewUser_defaultFG)
        recyclerView.adapter = topicAdapter
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fetchTopics = topicService.getPublicTopic()
                if (allUser.isEmpty()) {
                    allUser.addAll(userService.getUsers().users!!)
                }
                if (fetchTopics.status) {
                    Log.i("TAG", "fetchTopics: ${fetchTopics.topics!!.size}")
                    allTopic.addAll(fetchTopics.topics!!)
                    topicItems.addAll(allTopic.filter { it.title.contains(keywordIntent, ignoreCase = true) }
                        .map { topic ->
                            LibraryTopicAdapterItem(
                                topic, allUser.firstOrNull { topic.userId == it.uid }
                            )
                        })
                    runOnUiThread {
                        topicAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun actionOnUserFM(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewUser_defaultFG)
        userAdapter = UserAdapter(userItems, allTopic)
        recyclerView.adapter = userAdapter
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (allUser.isEmpty()) {
                    allUser.addAll(userService.getUsers().users!!)
                }
                userItems.clear()
                userItems.addAll(allUser)
                runOnUiThread {
                    userAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}