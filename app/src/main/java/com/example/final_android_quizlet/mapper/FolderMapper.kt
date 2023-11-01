package com.example.final_android_quizlet.mapper

import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.models.Topic
import org.modelmapper.ModelMapper

class FolderMapper {
    private val mapper = ModelMapper()

    fun convertToFolder(folder: Any): Folder {
        return mapper.map(folder, Folder::class.java)
    }
}