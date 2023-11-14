package com.example.final_android_quizlet.mapper

import com.example.final_android_quizlet.models.FlashCard
import com.example.final_android_quizlet.models.Folder
import com.google.firebase.firestore.DocumentSnapshot
import org.modelmapper.ModelMapper

class FlashCardMapper {
    private val mapper = ModelMapper()

    fun convertToFlashCard(folder: Any): FlashCard {
        return mapper.map(folder, FlashCard::class.java)
    }
    fun convertToFlashCards(folders: List<DocumentSnapshot>): List<FlashCard> {
        return folders.map {
            return@map convertToFlashCard(it.data!!)
        }
    }
}