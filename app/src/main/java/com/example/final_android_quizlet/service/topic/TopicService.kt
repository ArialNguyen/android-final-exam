package com.example.final_android_quizlet.service.topic

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.TopicMapper
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.ArrayList

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

}