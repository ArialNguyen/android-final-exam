package com.example.final_android_quizlet.common

import android.view.View
import com.example.final_android_quizlet.adapter.AddTopicToFolderApdater_CreatedAndLearned
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.TopicAdapter
import com.example.final_android_quizlet.adapter.UserAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.models.User

interface GetBackAdapterFromViewPager {
    fun onResult(view: View, items:  MutableList<LibraryFolderAdapterItem>, adapter: LibraryFolderAdapter){}
    fun onResult(view: View, items:  MutableList<LibraryTopicAdapterItem>, adapter: TopicAdapter){}
    fun onResult(view: View, items: MutableList<LibraryTopicAdapterItem>, itemsChosen: MutableList<Int>, adapter: AddTopicToFolderApdater_CreatedAndLearned) {}

    fun onResult(view: View, items:  MutableList<User>, adapter: UserAdapter){}

    fun onActionBack(view: View){}

}