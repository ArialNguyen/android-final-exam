package com.example.final_android_quizlet.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.UserMapper
import com.example.final_android_quizlet.models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.rpc.context.AttributeContext.Auth
import kotlinx.coroutines.tasks.await
import kotlin.reflect.full.memberProperties


class UserService {
    private val db = Firebase.firestore
    private val userAvatarRef: StorageReference = FirebaseStorage.getInstance().reference.child("user")
    private val userMapper: UserMapper = UserMapper()
    inline fun <reified T : Any> T.asMap() : MutableMap<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) } as MutableMap<String, Any?>
    }
    suspend fun getUsers(): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("users")
                .get().await()
            res.status = true
            if (data.documents.size == 0) {
                res.users = mutableListOf()
            } else {
                res.users =  userMapper.convertToUsers(data.documents)
                res.status = true
            }
        } catch (e: Exception) {
            Log.i("TAG", "ERROR: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }


    suspend fun addUser(user: User): ResponseObject {
        val res = ResponseObject()
        try {
            val map = user.asMap()
            map["createdAt"] = FieldValue.serverTimestamp()

            val fetch1 =  db.collection("users")
                .add(map).await()

            val fetch2 = fetch1.get().await()

            if (!fetch2.exists()){
                throw Exception("Some thing wrong when add User --Unknown_Reason")
            }
            res.user = userMapper.convertToUser(fetch2.data!!)
            res.status = true
        }catch (e: Exception){
            Log.i(e.message.toString(), "addUser: ")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun getUserByEmail(email: String): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("users")
                .whereEqualTo("email", email)
                .get().await()
            if (data.documents.size == 0) {
                throw Exception("Email not registered yet")
            } else {
                res.user = data.documents[0].toObject(User::class.java)!!
                Log.i("TAG", "User: ${res.user}")
                res.status = true
            }
        } catch (e: Exception) {
            Log.i("TAG", "ERROR: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun getUserByField(field: String, value: Any): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("users")
                .whereEqualTo(field, value)
                .get().await()
            if (data.documents.size == 0) {
                throw Exception("Not Found!!!")
            } else {
                res.user = data.documents[0].toObject(User::class.java)!!
                res.status = true
            }
        } catch (e: Exception) {
            Log.i("TAG", "ERROR: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun getUsersInListUserId(userIds: List<String>): ResponseObject {
        val res: ResponseObject = ResponseObject()
        try {
            val data = db.collection("users")
                .whereIn("uid", userIds)
                .get().await()
            if (data.documents.size == 0) {
                res.users = mutableListOf()
            } else {
                res.users = userMapper.convertToUsers(data.documents)
                res.status = true
            }
        } catch (e: Exception) {
            Log.i("TAG", "ERROR: ${e.message}")
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun getDocumentIdByField(field: String, value: Any): String {
        return db.collection("users").whereEqualTo(field, value).get().await().documents[0].id
    }

    suspend fun updateProfile(uid: String, user: HashMap<String, Any>): ResponseObject {
        val res = ResponseObject()
        try {
            val documentId = getDocumentIdByField("uid", uid)
            db.collection("users").document(documentId).update(user).await()
            res.status = true
        }catch (e: Exception){
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }
    suspend fun removeTopicSaved(topicId: String): ResponseObject {
        val res = ResponseObject()
        try {
            val documentIds = db.collection("users")
                .whereArrayContains("topicSaved", topicId)
                .get().await()

            val batch = db.batch()
            documentIds.forEach {
                batch.update(it.reference, "topicSaved", FieldValue.arrayRemove(topicId))
            }
            batch.commit()
            res.status = true
        }catch (e: Exception){
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun checkPasscodeFGP(email: String, passcode: String): ResponseObject {
        val res: ResponseObject = ResponseObject()
        Log.i("EMAIL", email)
        try {
            val data = db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("passcodeFGP", passcode.toInt())
                .get().await()
            if (data.documents.size == 0) {
                throw Exception("Not Found element with email = $email")
            } else {
                res.user = data.documents[0].toObject(User::class.java)!!
                res.status = true
            }
        } catch (e: Exception) {
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun uploadAvatar(uid: String, uri: Uri): ResponseObject {
        val res = ResponseObject()
        Log.i("TAG", "URI: $uri")
        try {
            val fetch1 = userAvatarRef.child("avatar/$uid.jpg").putFile(uri).await()
            val path = fetch1.storage.downloadUrl.await()

            val fetch2 = updateProfile(uid, hashMapOf(
                "avatar" to path
            ))
            if(!fetch2.status){
                throw Exception(fetch2.data.toString())
            }
            res.status = true
        }catch (e: Exception){
            res.status = false
            res.data = e.message.toString()
        }
        return res
    }

    suspend fun updateUserName(uid: String, newName: String): ResponseObject {
        val res = ResponseObject()
        try {
            val documentId = getDocumentIdByField("uid", uid)
            val updateMap = hashMapOf<String, Any>("name" to newName)

            db.collection("users").document(documentId)
                .update(updateMap)
                .await()

            res.status = true
        } catch (e: Exception) {
            res.status = false
            res.data = e.message.toString()
        }
        return res
    }

    companion object {
        private var instance: UserService? = null
        @Synchronized
        fun getInstance(): UserService {
            if (instance == null) {
                instance = UserService()
            }
            return instance!!
        }
    }

}