package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.common.MyFBQuery
import com.example.final_android_quizlet.common.MyFBQueryMethod
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.FlashCardMapper
import com.example.final_android_quizlet.models.Enum.ETermList
import com.example.final_android_quizlet.models.FlashCard
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FlashCardService {
    private val db = Firebase.firestore
    private val flashCardMapper: FlashCardMapper = FlashCardMapper()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    suspend fun createFlashCard(flashCard: FlashCard): ResponseObject {
        val res = ResponseObject()
        try {
            val fetch1 =  db.collection("flashcard")
                .add(flashCard).await()
            val fetch2 = fetch1.get().await()
            if (!fetch2.exists()){
                throw Exception("Some thing wrong when create FlashCard --Unknown_Reason")
            }
            res.flashCard = flashCardMapper.convertToFlashCard(fetch2.data!!)
            res.status = true
        }catch (e: Exception){
            Log.i("TAG", "ERROR: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun findFlashCardByTopicIdAndTermType(topicId: String, termType: ETermList): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("flashcard")
                .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                .whereEqualTo("topicId", topicId)
                .whereEqualTo("etermList", termType.name)
                .get().await()
            res.status = true
            if (data.documents.size == 0) {
                throw Exception("Not Found FlashCard")
            } else {
                res.flashCard = flashCardMapper.convertToFlashCard(data.documents[0].data!!)
            }
        } catch (e: Exception) {
            Log.i("TAG", "findFlashCardByTopicId: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun updateFlashCard(uid: String, flashCard: FlashCard): ResponseObject {
        val res = ResponseObject()
        try {
            val documentId = db.collection("flashcard")
                .whereEqualTo("uid", uid)
                .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                .get().await().documents[0].id
            db.collection("flashcard").document(documentId).update(hashMapOf(
                "currentTermId" to flashCard.currentTermId,
                "termsLearning" to flashCard.termsLearning,
                "termsKnew" to flashCard.termsKnew
            )).await()
            res.status = true
        }catch (e: Exception){
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun resetFlashCard(uid: String): ResponseObject {
        val res = ResponseObject()
        try {
            val documentId = db.collection("flashcard")
                .whereEqualTo("uid", uid)
                .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                .get().await().documents[0].id
            db.collection("flashcard").document(documentId).update(hashMapOf(
                "currentTermId" to "",
                "termsLearning" to listOf<Term>(),
                "termsKnew" to listOf<Term>()
            )).await()
            res.status = true
        }catch (e: Exception){
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun findFlashCardByUserId(userId: String): ResponseObject {
        val res = ResponseObject()
        try {
            val data = db.collection("flashcard")
                .whereEqualTo("userId", userId)
                .get().await()
            res.status = true
            if (data.documents.size == 0) {
                res.flashCard = null
            } else {
                res.flashCard = flashCardMapper.convertToFlashCard(data.documents[0].data!!)
            }
        } catch (e: Exception) {
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
}