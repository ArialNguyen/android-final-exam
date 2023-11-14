package com.example.final_android_quizlet.service

import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.QuizWriteMapper
import com.example.final_android_quizlet.models.QuizWrite
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class QuizWriteService {
    private val db = Firebase.firestore
    private val quizWriteMapper = QuizWriteMapper()

    suspend fun saveUserAnswer(quizWrite: QuizWrite): ResponseObject {
        val res = ResponseObject()
        try {
            val fetch1 = db.collection("writing_test")
                .add(quizWrite).await()

            val fetch2 = fetch1.get().await()
            if (fetch2.exists()) {
                res.quizWrite = quizWriteMapper.convertToQuizWrite(fetch2.data!!)
                res.status = true
            } else {
                throw Exception("Something went wrong when saving user answer")
            }
        } catch (e: Exception) {
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
}