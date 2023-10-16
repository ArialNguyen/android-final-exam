package com.example.final_android_quizlet.service.user

import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.models.User
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Objects
import kotlin.math.log
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class AuthService(){
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val userService: UserService = UserService()
    suspend fun register(email: String, password: String): ResponseObject{
        return withContext(Dispatchers.IO){
            val res = ResponseObject()
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user!!

                firebaseAuth.currentUser!!.sendEmailVerification().await()
                res.status = true
                res.data = email

                userService.addUser(User(null, email, password, null))
                Log.i("AUTH", authResult.additionalUserInfo!!.username.toString())
            }catch (e: Exception){
                res.status = false
                res.data = e.message.toString()
            }
            res
        }
    }

    suspend fun login(email: String, password: String): ResponseObject{
        return withContext(Dispatchers.IO){
            val res = ResponseObject()
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                if(firebaseAuth.currentUser!!.isEmailVerified){
                    res.status = true
                    res.data = authResult.user!!.email.toString()
                }else{
                    throw Exception("Please verify your email to login !!!")
                }
            }catch (e: Exception){
                res.status = false
                res.data = e.message.toString()
            }
            res
        }
    }
}