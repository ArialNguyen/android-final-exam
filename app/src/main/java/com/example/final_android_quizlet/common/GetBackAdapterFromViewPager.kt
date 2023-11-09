package com.example.final_android_quizlet.common

import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.LibraryTopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem

interface GetBackAdapterFromViewPager {
    fun onResult(items:  MutableList<LibraryFolderAdapterItem>, adapter: LibraryFolderAdapter){}
    fun onResult(items:  MutableList<LibraryTopicAdapterItem>, adapter: LibraryTopicAdapter){}

}