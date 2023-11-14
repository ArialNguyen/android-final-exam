package com.example.final_android_quizlet.mapper

import com.example.final_android_quizlet.models.QuizWrite
import com.example.final_android_quizlet.models.Answer
import com.google.firebase.firestore.DocumentSnapshot
import org.modelmapper.ModelMapper

class QuizWriteMapper {
    private val mapper = ModelMapper()

    fun convertToQuizWrite(quizWrite: Any): QuizWrite {
        return mapper.map(quizWrite, QuizWrite::class.java)
    }

    fun convertToQuizWrite(quizWrites: List<DocumentSnapshot>): List<QuizWrite> {
        return quizWrites.map {
            return@map convertToQuizWrite(it.data!!)
        }
    }
}