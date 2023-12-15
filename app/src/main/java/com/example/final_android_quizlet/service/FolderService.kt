package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.common.MyFBQuery
import com.example.final_android_quizlet.common.MyFBQueryMethod
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.FolderMapper
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FolderService {
    private val db = Firebase.firestore
    private val folderMapper: FolderMapper = FolderMapper()

    suspend fun createFolder(folder: Folder): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val fetch1 = db.collection("folders")
                .add(folder).await()
            val fetch2 = fetch1.get().await()
            if (!fetch2.exists()) {
                throw Exception("Some thing wrong when create Folder --Unknown_Reason")
            }
            res.folder = folderMapper.convertToFolder(fetch2.data!!)
            res.status = true
        } catch (e: Exception) {
            Log.i(e.message.toString(), "Folder: ")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun deleteFolder(folderId: String): ResponseObject {
        val res = ResponseObject()
        try {
            val documentId = getDocumentIdByFields(listOf(MyFBQuery("uid", folderId, MyFBQueryMethod.EQUAL)))
            db.collection("folders").document(documentId).delete().await()
            res.status = true
        } catch (e: Exception) {
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun getDocumentIdByFields(params: List<MyFBQuery>): String {
        var collectionRef = db.collection("folders")
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
        return query!!.get().await().documents[0].id
    }

    suspend fun getDocumentIdsByFields(params: List<MyFBQuery>): List<String> {
        var collectionRef = db.collection("folders")
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
        return query!!.get().await().documents.map { it.id }.toList()
    }

    inner class FolderForUserLogged {
        private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        suspend fun getFolders(): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val data = db.collection("folders")
                    .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                    .get().await()
                res.status = true
                if (data.documents.size == 0) {
                    res.folders = listOf()
                } else {
                    res.folders = folderMapper.convertToFolders(data.documents)
                }
            } catch (e: Exception) {
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun getFolderById(uid: String): ResponseObject {
            Log.i("TAG", "getFolderById: $uid")
            val res: ResponseObject = ResponseObject()
            try {
                val data = db.collection("folders")
                    .whereEqualTo("userId", firebaseAuth.currentUser!!.uid)
                    .whereEqualTo("uid", uid)
                    .get().await()

                if (data.documents.size == 0) {
                    throw Exception("Not Found Folder with id = $uid")
                } else {
                    res.status = true
                    res.folder = folderMapper.convertToFolder(data.documents[0].data!!)
                }
            } catch (e: Exception) {
                Log.i("TAG", "getFolderById: ${e.message}")
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun addTopicToFolders(topicId: String, folders: List<String>): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val query = mutableListOf<MyFBQuery>()
                query.add(MyFBQuery("userId", firebaseAuth.currentUser!!.uid, MyFBQueryMethod.EQUAL))
                query.add(MyFBQuery("uid", folders, MyFBQueryMethod.IN))
                val documentIds = getDocumentIdsByFields(
                    query
                )
                val batch = db.batch()
                documentIds.forEach {
                    batch.update(db.collection("folders").document(it), "topics", FieldValue.arrayUnion(topicId))
                }
                batch.commit().await()
                res.status = true
            } catch (e: Exception) {
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }
        suspend fun addTopicsToFolder(folderId: String, topics: MutableList<Topic>): ResponseObject{
            val res: ResponseObject = ResponseObject()
            try {
                val query = mutableListOf<MyFBQuery>()
                query.add(MyFBQuery("userId", firebaseAuth.currentUser!!.uid, MyFBQueryMethod.EQUAL))
                query.add(MyFBQuery("uid", folderId, MyFBQueryMethod.EQUAL))
                val documentId = getDocumentIdByFields(
                    query
                )
                val topicIds = topics.map { it.uid }

                db.collection("folders").document(documentId)
                    .update("topics", FieldValue.arrayUnion(*topicIds.toTypedArray())).await()
                res.status = true
            } catch (e: Exception) {
                Log.i("TAG", "ERROR: ${e.message}")
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun removeTopicFromFolder(folderId: String, topicId: String): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val query = mutableListOf<MyFBQuery>()
                query.add(MyFBQuery("userId", firebaseAuth.currentUser!!.uid, MyFBQueryMethod.EQUAL))
                query.add(MyFBQuery("uid", folderId, MyFBQueryMethod.EQUAL))
                val documentId = getDocumentIdByFields(
                    query
                )

                db.collection("folders").document(documentId)
                    .update("topics", FieldValue.arrayRemove(topicId)).await()
                res.status = true
            } catch (e: Exception) {
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun updateBaseInfo(folderId: String, folderName: String, des: String): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val query = mutableListOf<MyFBQuery>()
                query.add(MyFBQuery("userId", firebaseAuth.currentUser!!.uid, MyFBQueryMethod.EQUAL))
                query.add(MyFBQuery("uid", folderId, MyFBQueryMethod.EQUAL))
                val documentId = getDocumentIdByFields(
                    query
                )

                db.collection("folders").document(documentId)
                    .update("name", folderName, "description", des)
                    .await()
                res.status = true
            } catch (e: Exception) {
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

        suspend fun removeTopic(topicId: String): ResponseObject {
            val res: ResponseObject = ResponseObject()
            try {
                val documentIds = db.collection("folders")
                    .whereArrayContains("topics", topicId)
                    .get().await()
                val batch = db.batch()

                documentIds.forEach {
                    batch.update(it.reference, "topics", FieldValue.arrayRemove(topicId))
                }
                batch.commit()
                res.status = true
            } catch (e: Exception) {
                res.data = e.message.toString()
                res.status = false
            }
            return res
        }

    }

}


