package com.example.final_android_quizlet.service.user

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.modelmapper.ModelMapper


class UserService {
    val db = Firebase.firestore
    val modelMapper = ModelMapper()
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
                                userReturn = modelMapper.map(documentSnapshot.data, User::class.java)
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

    suspend fun getUserByEmail(email: String): ResponseObject{
        return withContext(Dispatchers.IO){
            var user: User
            val res = ResponseObject()
            try {
                val query = db.collection("users").whereEqualTo("email", email).limit(1).get().await()
                Log.i("getUserByEmail", "${query.documents.size}")
                if(query.documents.size != 0){
                    user = query.documents[0].toObject(User::class.java)!!
                    res.data = user
                    res.status = true
                }else{
                    throw Exception("Not Found user with email = $email")
                }
            }catch (e: Exception){
                res.data = e.message
                res.status = false
            }
            res
        }
    }
}