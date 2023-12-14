package com.example.final_android_quizlet.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.AddTopicFolderActivity
import com.example.final_android_quizlet.adapter.AddTopicToFolderApdater_CreatedAndLearned
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.common.MyFBQuery
import com.example.final_android_quizlet.common.MyFBQueryMethod
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FlashCardService
import com.example.final_android_quizlet.service.TopicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentAddTopic_LearnFolder(private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager?): Fragment() {
    constructor(): this(null)
    private val authService: AuthService = AuthService()
    private val topicService: TopicService = TopicService()
    private val flashCardService: FlashCardService = FlashCardService()

    // Adapter
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private var itemsChosen: MutableList<Int> = mutableListOf()


    // View
    private var recyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_add_topic__learn_folder, container, false)
        val view = inflater.inflate(R.layout.fragment_add_topic__learn_folder, container, false)
        // Get date from intent
        val folder = (context as AddTopicFolderActivity).folder!!
        // View
        recyclerView = view.findViewById(R.id.recyclerView_FragmentAddTopic_LearnFolder)

        // Adapter
        val adapter = AddTopicToFolderApdater_CreatedAndLearned(items, itemsChosen, requireContext())
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        recyclerView!!.adapter = adapter

        val activity = (context as Activity)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val user = authService.getUserLogin().user!!

                val flashCardResponse = flashCardService.findFlashCardByUserId(user.uid)
                if (flashCardResponse.status) {
                    val flashCard = flashCardResponse.flashCard

                    flashCard?.let { loggedInUserFlashCard ->
                        val topicId = loggedInUserFlashCard.topicId
                        val fetchTopics = topicService.TopicForUserLogged().getTopicsByQuerys(
                            mutableListOf(
                                MyFBQuery("userId", user.uid, MyFBQueryMethod.EQUAL),
                                MyFBQuery("uid", topicId, MyFBQueryMethod.EQUAL)
                            )
                        )

                        if (fetchTopics.status) {
                            if(fetchTopics.topics!!.isNotEmpty()){
                                val positionStart: Int = items.size
                                fetchTopics.topics!!.forEachIndexed { index, topic ->
                                    if(folder.topics.contains(topic.uid)) itemsChosen.add(items.size + index)
                                }
                                items.addAll(fetchTopics.topics!!.map { LibraryTopicAdapterItem(it, user) })

                                activity.runOnUiThread {
                                    adapter.notifyItemRangeInserted(positionStart, items.size)
                                }
                            }
                        } else {
                            activity.runOnUiThread {
                                Toast.makeText(context, fetchTopics.data.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
        getBackAdapterFromViewPager?.onResult(view, items, itemsChosen, adapter)
        return view
    }
}