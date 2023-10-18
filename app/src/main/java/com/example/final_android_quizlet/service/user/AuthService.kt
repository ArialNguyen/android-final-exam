package com.example.final_android_quizlet.service.user

import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.UserMapper
import com.example.final_android_quizlet.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthService() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userService: UserService = UserService()
    private val userMapper: UserMapper = UserMapper()
    suspend fun register(email: String, password: String): ResponseObject {
        return withContext(Dispatchers.IO) {
            val res = ResponseObject()
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user!!

                firebaseAuth.currentUser!!.sendEmailVerification().await()
                res.status = true
                res.data = email

                userService.addUser(User(null, email, password, null))
                Log.i("AUTH", authResult.additionalUserInfo!!.username.toString())
            } catch (e: Exception) {
                res.status = false
                res.data = e.message.toString()
            }
            res
        }
    }

    suspend fun login(email: String, password: String): ResponseObject {
        val res = ResponseObject()
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                res.status = true
                res.data = authResult.user!!.email.toString()
            } else {
                throw Exception("Please verify your email to login !!!")
            }
        } catch (e: Exception) {
            res.status = false
            res.data = e.message.toString()
        }
        Log.i("login", "${res.data}")
        return res
    }

    fun isLogin(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun getUserLogin(): ResponseObject {
        val email = firebaseAuth.currentUser!!.email!!
        return userService.getUserByEmail(email)
    }

    fun logout() {
        firebaseAuth.signOut()
        Log.i("LOGOUT", "${firebaseAuth.currentUser}")
    }

}