package com.example.final_android_quizlet.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.AddTopicFolderActivity
import com.example.final_android_quizlet.adapter.AddTopicToFolderApdater_CreatedAndLearned
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentAddTopic_CreateFolder(private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager?) :
    Fragment() {
    constructor() : this(null)

    // Service
    private val authService: AuthService = AuthService()
    private val topicService: TopicService = TopicService()

    // Adapter
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private var itemsChosen: MutableList<Int> = mutableListOf()

    private lateinit var session: Session

    // View
    private var recyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_topic__create_folder, container, false)

        session = Session.getInstance(requireContext())
        // Get date from intent
        val folder = (context as AddTopicFolderActivity).folder!!
        // View
        recyclerView = view.findViewById(R.id.recyclerView_FragmentAddTopic)

        // Adapter
        val adapter = AddTopicToFolderApdater_CreatedAndLearned(items, itemsChosen, requireContext())
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        recyclerView!!.adapter = adapter

        val activity = (context as Activity)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val user = authService.getUserLogin().user!!
                val fetchTopics = session.topicsOfUser!!
                if (session.topicsOfUserSaved != null) {
                    if (session.topicsOfUserSaved!!.isNotEmpty()) fetchTopics.addAll(session.topicsOfUserSaved!!)
                }
                if (fetchTopics.isNotEmpty()) {
                    val positionStart: Int = items.size
                    fetchTopics.forEachIndexed { index, topic ->
                        if (folder.topics.contains(topic.uid)) itemsChosen.add(items.size + index)
                    }
                    items.addAll(fetchTopics.map { LibraryTopicAdapterItem(it, user) })

                    activity.runOnUiThread {
                        adapter.notifyItemRangeInserted(positionStart, items.size)
                    }
                }

            }
        }
        getBackAdapterFromViewPager?.onResult(view, items, itemsChosen, adapter)
        return view
    }
}