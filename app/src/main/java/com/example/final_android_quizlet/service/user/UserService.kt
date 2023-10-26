package com.example.final_android_quizlet.service.user

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.UserMapper
import com.example.final_android_quizlet.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class UserService {
    private val db = Firebase.firestore
    private val userMapper: UserMapper = UserMapper()
    suspend fun getUsers(): MutableList<User> {
        val users = mutableListOf<User>()

        db.collection("users")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result.documents) {
                        val user = document.toObject<User>()
                        if (user != null) {
                            users.add(user)
                        }
                    }
                } else if (it.isCanceled) {
                    Log.i("Cancel GET Users: ", "")
                }
//                callback.onCallback(users)
            }
            .addOnFailureListener { exception ->
                Log.w("GET Users: ", "Error getting documents $exception")
            }
            .await()
        return users
    }

    suspend fun addUser(user: User): ResponseObject {
        val res = ResponseObject()
        try {
            Log.i("111", "addUser: ")
            val fetch1 =  db.collection("users")
                .add(user).await()
            Log.i("222", "addUser: ")
            val fetch2 = fetch1.get().await()
            if (!fetch2.exists()){
                throw Exception("Some thing wrong when add User --Unknown_Reason")
            }
            Log.i("USER RESPONSE IN ADD USER", fetch2.data.toString())
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
        Log.i("EMAIL", email)
        try {
            val data = db.collection("users")
                .whereEqualTo("email", email)
                .get().await()
            Log.i("DOCUEMNTS", "${data.documents.size}")
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

    suspend fun getDocumentIdByField(field: String, value: Any): String {
        return db.collection("users").whereEqualTo(field, value).get().await().documents[0].id
    }

    suspend fun updateProfile(uid: String, user: HashMap<String, Any>): ResponseObject {
        val res = ResponseObject()
        try {
            val documentId = getDocumentIdByField("uid", uid)
            db.collection("users").document(documentId).update(user).await()
            res.status = true
            res.data
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
            Log.i("DOCUEMNTS", "${data.documents.size}")
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

}