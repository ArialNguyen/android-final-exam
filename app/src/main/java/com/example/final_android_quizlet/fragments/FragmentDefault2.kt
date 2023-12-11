package com.example.final_android_quizlet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager

class FragmentDefault2(
    private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager?
) : Fragment() {
    constructor(): this(null)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_default2, container, false)
        getBackAdapterFromViewPager?.onActionBack(view)
        return view
    }
}