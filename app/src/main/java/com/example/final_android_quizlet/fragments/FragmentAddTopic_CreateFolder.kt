package com.example.final_android_quizlet.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager


class FragmentAddTopic_CreateFolder(val ctx: Context, private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager) : Fragment() {
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_topic__create_folder, container, false)
    }
}