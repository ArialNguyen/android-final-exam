package com.example.final_android_quizlet.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.DetailTopic
import com.example.final_android_quizlet.adapter.LibraryTopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.mapper.TopicMapper
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import kotlinx.coroutines.launch

class FragmentTopicLibrary(val ctx: Context) : Fragment() {
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private val itemsTemp: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private val authService: AuthService = AuthService()
    private val topicService: TopicService = TopicService()
    private val topicMapper: TopicMapper = TopicMapper()

    private var etSearchTopic: EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter = LibraryTopicAdapter(items)
        val view = inflater.inflate(R.layout.fragment__hoc_phan, container, false)
        etSearchTopic = view.findViewById(R.id.etFilterTopic_library)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_library)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { item ->
            val intent = Intent(context, DetailTopic::class.java)
            intent.putExtra("topicId", item.topic.uid)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val user = authService.getUserLogin().user!!
            val topics = topicService.getTopicsByUserId(user.uid).topics!!
            val list =  topics.map {
                LibraryTopicAdapterItem(it, user)
            }.toMutableList()
            itemsTemp.addAll(list)
            items.addAll(list)
            adapter.notifyDataSetChanged()
        }

        etSearchTopic?.doOnTextChanged { text, start, before, count ->
            items.clear()
            items.addAll(itemsTemp.filter { it.topic.title.contains(text!!, ignoreCase = true) }.toMutableList())
            adapter.notifyDataSetChanged()
        }
        return view
    }
}