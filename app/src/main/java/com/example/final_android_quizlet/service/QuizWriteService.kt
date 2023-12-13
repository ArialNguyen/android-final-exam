package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.QuizWriteMapper
import com.example.final_android_quizlet.models.Enum.ETermList
import com.example.final_android_quizlet.models.QuizWrite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.reflect.full.memberProperties

class QuizWriteService {
    private val db = Firebase.firestore
    private val quizWriteMapper = QuizWriteMapper()
    inline fun <reified T : Any> T.asMap() : MutableMap<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) } as MutableMap<String, Any?>
    }
    suspend fun saveUserAnswer(quizWrite: QuizWrite): ResponseObject {
        val res = ResponseObject()
        try {
            val map = quizWrite.asMap()
            map["createdAt"] = FieldValue.serverTimestamp()
            val fetch1 = db.collection("writing_test")
                .add(map).await()

            val fetch2 = fetch1.get().await()
            if (fetch2.exists()) {
                res.quizWrite = quizWriteMapper.convertToQuizWrite(fetch2)
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
    suspend fun getDocumentIdByField(field: String, value: Any): String {
        return db.collection("writing_test").whereEqualTo(field, value).get().await().documents[0].id
    }
    suspend fun getRankingWritingTest(topicId: String): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("writing_test")
                .whereEqualTo("topicId", topicId)
                .whereEqualTo("termType", ETermList.NORMAL_TERMS.name)
                .orderBy("overall", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get().await()

            if (data.documents.size == 0) {
                throw Exception("Not Found ChoiceTest")
            } else {
                res.status = true
                res.quizWrites = quizWriteMapper.convertToQuizWrites(data.documents)
            }
        } catch (e: Exception) {
            Log.i("TAG", "findWritingTestByTopicId: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }


    inner class WTForUserLogged{
        private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        suspend fun findWritingTestByTopicId(topicId: String, termType: ETermList): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val data = db.collection("writing_test")
                    .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                    .whereEqualTo("topicId", topicId)
                    .whereEqualTo("termType", termType.name)
                    .get().await()

                if (data.documents.size == 0) {
                    throw Exception("Not Found WritingTest")
                } else {
                    res.status = true
                    res.quizWrite = quizWriteMapper.convertToQuizWrite(data.documents[0])
                    Log.i("TAG", "findWritingTestByTopicId: ${ res.quizWrite  }")
                }
            } catch (e: Exception) {
                Log.i("TAG", "ERROR: ${e.message}")
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun deleteWritingTestById(uid: String): ResponseObject {
            val res = ResponseObject()
            try {
                val documentId = getDocumentIdByField("uid", uid)
                db.collection("writing_test").document(documentId).delete().await()
                res.status = true
            }catch (e: Exception){
                Log.i("TAG", "ERROR: ${e.message}")
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }
    }
}