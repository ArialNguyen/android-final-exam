package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.ChoiceTestMapper
import com.example.final_android_quizlet.models.Enum.ETermList
import com.example.final_android_quizlet.models.MultipleChoice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.reflect.full.memberProperties

class MultipleChoiceService {
    private val db = Firebase.firestore
    private val choiceMapper: ChoiceTestMapper = ChoiceTestMapper()
    inline fun <reified T : Any> T.asMap() : MutableMap<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) } as MutableMap<String, Any?>
    }
    suspend fun getDocumentIdByField(field: String, value: Any): String {
        return db.collection("choice_test").whereEqualTo(field, value).get().await().documents[0].id
    }
    suspend fun createChoiceTest(multipleChoice: MultipleChoice): ResponseObject {
        val res = ResponseObject()
        try {
            val map = multipleChoice.asMap()
            map["createdAt"] = FieldValue.serverTimestamp()
            val fetch1 =  db.collection("choice_test")
                .add(map).await()

            val fetch2 = fetch1.get().await()
            if (!fetch2.exists()){
                throw Exception("Some thing wrong when create choice_test --Unknown_Reason")
            }
            Log.i("TAG", "createChoiceTest: ${fetch2.data!!}")
            res.testChoice = choiceMapper.convertToChoiceTest(fetch2)
            res.status = true
        }catch (e: Exception){
            Log.i("TAG", "ERROR: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun getRankingChoiceTest(topicId: String): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("choice_test")
                .whereEqualTo("topicId", topicId)
                .orderBy("overall", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .whereEqualTo("termType", ETermList.NORMAL_TERMS.name)
                .get().await()

            if (data.documents.size == 0) {
                throw Exception("Not Found ChoiceTest")
            } else {
                res.status = true
                res.testChoices = choiceMapper.convertToChoicesTest(data.documents)
            }
        } catch (e: Exception) {
            Log.i("TAG", "ERROr: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }



    inner class MPForUserLogged{

        private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        suspend fun findChoiceTestByTopicIdAndTermType(topicId: String, termType: ETermList): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val data = db.collection("choice_test")
                    .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                    .whereEqualTo("topicId", topicId)
                    .whereEqualTo("termType", termType.name)
                    .get().await()

                if (data.documents.size == 0) {
                    throw Exception("Not Found ChoiceTest")
                } else {
                    res.status = true
                    res.testChoice = choiceMapper.convertToChoiceTest(data.documents[0])
                    Log.i("TAG", "findChoiceTestByTopicId: ${ res.testChoice  }")
                }
            } catch (e: Exception) {
                Log.i("TAG", "findChoiceTestByTopicId: ${e.message}")
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun deleteChoiceTest(uid: String): ResponseObject {
            val res = ResponseObject()
            try {
                val documentId = getDocumentIdByField("uid", uid)
                db.collection("choice_test").document(documentId).delete().await()
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