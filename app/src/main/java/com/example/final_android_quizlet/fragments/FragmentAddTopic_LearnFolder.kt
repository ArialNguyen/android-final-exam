package com.example.final_android_quizlet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager

class FragmentAddTopic_LearnFolder(private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_topic__learn_folder, container, false)
    }
}