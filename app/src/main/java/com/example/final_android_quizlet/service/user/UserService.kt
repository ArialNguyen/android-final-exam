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
    val db = Firebase.firestore
    val userMapper: UserMapper = UserMapper()
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

    suspend fun addUser(user: User): User {
        var userReturn: User? = null
        db.collection("users")
            .add(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val documentReference = it.result
                    documentReference.get().addOnCompleteListener { nestedTask ->
                        if (nestedTask.isSuccessful) {
                            val documentSnapshot = nestedTask.result // Get the DocumentSnapshot
                            if (documentSnapshot.exists()) {
                                // Access the data
                                // Process the data as needed
                                userReturn = userMapper.convertToUser(documentSnapshot.data!!)
                            } else {
                                // Document does not exist
                            }
                        } else {
                            // Handle nestedTask exception
                        }
                    }
                } else if (it.isCanceled) {
                    Log.i("Cancel GET Users: ", "")
                }
//                callback.onCallback(users)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error adding document $exception")
            }
            .await()
        return user
    }

    suspend fun getUserByEmail(email: String): ResponseObject {
        val res: ResponseObject = ResponseObject()
        Log.i("EMAIL", "$email")
        try {
            val data = db.collection("users")
                .whereEqualTo("email", email)
                .get().await()
            Log.i("DOCUEMNTS", "${data.documents.size}")
            if (data.documents.size == 0) {
                throw Exception("Not Found element with email = ${email}")
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