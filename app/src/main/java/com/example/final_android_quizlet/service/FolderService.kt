package com.example.final_android_quizlet.service

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.FolderMapper
import com.example.final_android_quizlet.models.Folder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FolderService {
    private val db = Firebase.firestore
    private val folderMapper: FolderMapper = FolderMapper()

    suspend fun createFolder(folder: Folder): ResponseObject{
        val res: ResponseObject = ResponseObject()
        try {
            val fetch1 =  db.collection("folders")
                .add(folder).await()
            val fetch2 = fetch1.get().await()
            if (!fetch2.exists()){
                throw Exception("Some thing wrong when create Folder --Unknown_Reason")
            }
            res.folder = folderMapper.convertToFolder(fetch2.data!!)
            res.status = true
        }catch (e: Exception){
            Log.i(e.message.toString(), "Folder: ")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    inner class FolderForUserLogged{
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
    }

}