package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.common.MyFBQuery
import com.example.final_android_quizlet.common.MyFBQueryMethod
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.TopicMapper
import com.example.final_android_quizlet.models.EModeTopic
import com.example.final_android_quizlet.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.reflect.full.memberProperties

class TopicService {
    private val db = Firebase.firestore
    private val topicMapper: TopicMapper = TopicMapper()
    inline fun <reified T : Any> T.asMap() : MutableMap<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) } as MutableMap<String, Any?>
    }
    suspend fun createTopic(topic: Topic): ResponseObject {
        val res = ResponseObject()
        try {
            val map = topic.asMap()
            map["createdAt"] = FieldValue.serverTimestamp()
            val fetch1 =  db.collection("topics")
                .add(map).await()

            val fetch2 = fetch1.get().await()
            if (!fetch2.exists()){
                throw Exception("Some thing wrong when create Topic --Unknown_Reason")
            }
            res.topic = topicMapper.convertToTopic(fetch2.data!!)
            res.status = true
        }catch (e: Exception){
            Log.i("TAG", "ERROR: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun getTopicById(id: String): ResponseObject {
        val res: ResponseObject = ResponseObject()
        Log.i("id", id)
        try {
            val data = db.collection("topics")
                .whereEqualTo("uid", id)
                .get().await()
            if (data.documents.size == 0) {
                throw Exception("Error... Not Found Topic with id = $id!")
            } else {
                res.topic = topicMapper.convertToTopic(data.documents[0].data!!)
                res.status = true
            }
        } catch (e: Exception) {
            Log.i("TAG", "ERROR: ${e.message}")
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
        return query!!.get().await().documents.toList()
    }

    suspend fun getPublicTopic(): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("topics")
                .where(Filter.and(
                    Filter.equalTo("mode", EModeTopic.PUBLIC.name)
                )).get().await()
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

    suspend fun getDocumentIdByField(field: String, value: Any): String {
        return db.collection("topics").whereEqualTo(field, value).get().await().documents[0].id
    }

    suspend fun getTopicsByQuerys(myFBQuery: MutableList<MyFBQuery>): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
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


    inner class TopicForUserLogged{
        private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        fun createTopics(topics: MutableList<Topic>): ResponseObject {
            val res = ResponseObject()
            try {
                val batch = db.batch()
                val docRef = db.collection("topics")
                topics.forEach {
                    val map = it.asMap()
                    map["createdAt"] = FieldValue.serverTimestamp()
                    batch.set(docRef.document(), map)
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
        suspend fun updateInfo(topicId: String, topic: HashMap<String, Any>): ResponseObject {
            val res = ResponseObject()
            try {
                val documentId = getDocumentIdByField("uid", topicId)
                db.collection("topics").document(documentId).update(topic).await()
                res.status = true
            } catch (e: Exception) {
                Log.i("TAG", "ERROR: ${e.message}")
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }
    }

}