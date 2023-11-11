package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.TopicMapper
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User
import com.google.firebase.auth.FirebaseAuth
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
            if (data.documents.size == 0) {
                throw Exception("User doesn't have any topic yet")
            } else {
                res.topics = topicMapper.convertToTopics(data.documents)
                res.status = true
            }
        } catch (e: Exception) {
            res.data = e.message.toString()
            res.status = false
        }
        return res
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
        suspend fun getTopics(): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val data = db.collection("topics")
                    .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
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
    }
}