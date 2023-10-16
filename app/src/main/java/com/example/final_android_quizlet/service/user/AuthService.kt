package com.example.final_android_quizlet.service.user

import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.Login
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
    suspend fun register(email: String, password: String): Map<String, Any>{
        return withContext(Dispatchers.IO){
            val result: MutableMap<String, Any> = mutableMapOf()
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user!!

                firebaseAuth.currentUser!!.sendEmailVerification().await()
                result["status"] = true
                result["data"] = "User Registered successfully, please verify your email to login"

                userService.addUser(User(null, email, password, null))
                Log.i("AUTH", authResult.additionalUserInfo!!.username.toString())
            }catch (e: Exception){
                result["status"] = false
                result["data"] = e.message.toString()
            }
            result
        }
    }

    suspend fun login(email: String, password: String): Map<String, Any>{
        return withContext(Dispatchers.IO){
            val result: MutableMap<String, Any> = mutableMapOf()
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                if(firebaseAuth.currentUser!!.isEmailVerified){
                    result["status"] = true
                    result["data"] = authResult.user!!.email.toString()
                }else{
                    result["status"] = false
                    result["data"] = "Please verify your email to login !!!"
                }
            }catch (e: Exception){
                result["status"] = false
                result["data"] = e.message.toString()
            }
            result
        }
    }
}