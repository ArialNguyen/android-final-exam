package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.common.MyFBQuery
import com.example.final_android_quizlet.common.MyFBQueryMethod
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.TopicMapper
import com.example.final_android_quizlet.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TopicService {
    private val db = Firebase.firestore
    private val topicMapper: TopicMapper = TopicMapper()
    suspend fun createTopic(topic: Topic): ResponseObject {
        val res = ResponseObject()
        try {
            val fetch1 =  db.collection("topics")
                .add(topic).await()

            val fetch2 = fetch1.get().await()
            if (!fetch2.exists()){
                throw Exception("Some thing wrong when create Topic --Unknown_Reason")
            }
            res.topic = topicMapper.convertToTopic(fetch2.data!!)
            res.status = true
        }catch (e: Exception){
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }


    suspend fun getTopicsByUserId(userId: String): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("topics")
                .whereEqualTo("userId", userId)
                .get().await()
            res.status = true
            if (data.documents.size == 0) {
                res.topics = listOf()
            } else {
                res.topics = topicMapper.convertToTopics(data.documents)
            }
        } catch (e: Exception) {
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun getDocumentsByFields(params: List<MyFBQuery>): List<DocumentSnapshot> {
        var collectionRef = db.collection("topics")
        var query: Query? = null
        params.forEach { it ->
            when (it.mode) {
                MyFBQueryMethod.EQUAL -> {
                    Log.i("TAG", "String: ${it.field}, value = ${it.value}")
                    query = if (query == null) collectionRef.whereEqualTo(it.field, it.value.toString())
                    else query!!.whereEqualTo(it.field, it.value.toString())
                }
                MyFBQueryMethod.IN -> {
                    Log.i("TAG", "List: ${it.field}, value = ${it.value as List<Any>}")
                    query = if (query == null) collectionRef.whereIn(it.field, it.value as List<Any>)
                    else query!!.whereIn(it.field, it.value as List<Any>)
                }
            }
        }
        Log.i("TAG", "getDocumentIdsByFields: ")
        return query!!.get().await().documents.toList()
    }

    inner class TopicForUserLogged{
        private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        suspend fun getTopicById(id: String): ResponseObject {
            val res: ResponseObject = ResponseObject()
            Log.i("id", id)
            try {
                val data = db.collection("topics")
                    .whereEqualTo("uid", id)
                    .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                    .get().await()
                if (data.documents.size == 0) {
                    throw Exception("Error... Make sure you already logged and had this topic!")
                } else {
                    res.topic = topicMapper.convertToTopic(data.documents[0].data!!)
                    res.status = true
                }
            } catch (e: Exception) {
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        fun createTopics(topics: MutableList<Topic>): ResponseObject {
            val res = ResponseObject()
            try {
                val batch = db.batch()
                val docRef = db.collection("topics")
                topics.forEach {
                    batch.set(docRef.document(), it)
                }
                batch.commit()

                res.topics = topics
                res.status = true
            }catch (e: Exception){
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun getTopicsByQuerys(myFBQuery: MutableList<MyFBQuery>): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                myFBQuery.add(MyFBQuery("userId", firebaseAuth.currentUser!!.uid, MyFBQueryMethod.EQUAL))
                val data = getDocumentsByFields(myFBQuery)
                res.status = true
                if (data.isEmpty()) {
                    res.topics = listOf()
                } else {
                    res.topics = topicMapper.convertToTopics(data)
                }
            } catch (e: Exception) {
                Log.i("ERROR", "getTopicsByQuerys: ${e.message}")
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }
    }
}