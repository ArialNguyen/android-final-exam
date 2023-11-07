package com.example.final_android_quizlet.fragments

import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap.Fold
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.DetailTopic
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.LibraryTopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.mapper.TopicMapper
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.example.final_android_quizlet.service.TopicService
import kotlinx.coroutines.launch

class FragmentFolderLibrary(ctx: Context) : Fragment() {
    private var items: MutableList<LibraryFolderAdapterItem> = mutableListOf()
    private val authService: AuthService = AuthService()
    private val folderService: FolderService = FolderService()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = LibraryFolderAdapter(items)
        val view = inflater.inflate(R.layout.fragment__thu_muc, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.folderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            val user = authService.getUserLogin().user!!
            val fetch1 = folderService.FolderForUserLogged().getFolders()
            val folders = mutableListOf<Folder>()
            if(fetch1.status) {
                folders.addAll(fetch1.folders!!)
            }else{
                Toast.makeText(activity, fetch1.data.toString(), Toast.LENGTH_LONG).show()
            }
            Log.i("TAG", "folders: $folders")
            val list =  folders.map {
                LibraryFolderAdapterItem(it, it.topics.size, user)
            }.toMutableList()
            items.addAll(list)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener { item ->
            val intent = Intent(context, DetailTopic::class.java)
//            intent.putExtra("topicName", item.topicTitle)
//            intent.putExtra("totalTerm", item.totalTerm)
//            intent.putExtra("userName", item.userName)
//            intent.putExtra("avatar", item.avatarUser)
            startActivity(intent)
        }
        return view
    }
}