package com.example.final_android_quizlet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.RecyclerViewAdapter_library_folder
import com.example.final_android_quizlet.models.Library_folder

class Fragment_ThuMuc : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__thu_muc, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.thuMucRecyclerView)

        val items = listOf(
            Library_folder(
                R.drawable.ic_baseline_folder_30,
                "Class six",
                R.drawable.ic_baseline_profile,
                "quizlette23254363"
            ),
        )

        val adapter = RecyclerViewAdapter_library_folder(items)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }
}