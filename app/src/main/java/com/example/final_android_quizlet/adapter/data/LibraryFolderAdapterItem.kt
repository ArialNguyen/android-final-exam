package com.example.final_android_quizlet.adapter.data

import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.models.User

data class LibraryFolderAdapterItem(
    val folder: Folder,
    val totalTopic: Number,
    val user: User
)