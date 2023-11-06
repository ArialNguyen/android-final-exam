package com.example.final_android_quizlet.mapper

import android.util.Log
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.models.Topic
import com.google.firebase.firestore.DocumentSnapshot
import org.modelmapper.ModelMapper

class FolderMapper {
    private val mapper = ModelMapper()

    fun convertToFolder(folder: Any): Folder {
        return mapper.map(folder, Folder::class.java)
    }
    fun convertToFolders(folders: List<DocumentSnapshot>): List<Folder> {
        return folders.map {
            return@map convertToFolder(it.data!!)
        }
    }
}