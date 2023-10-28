package com.example.final_android_quizlet.service.user

import android.net.Uri
import android.util.Log
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.mapper.UserMapper
import com.example.final_android_quizlet.models.User
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.random.Random

class AuthService() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userService: UserService = UserService()
    private val userMapper: UserMapper = UserMapper()
    suspend fun register(name: String, email: String, password: String): ResponseObject {
        return withContext(Dispatchers.IO) {
            val res = ResponseObject()
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user!!
//                firebaseAuth.currentUser.isEmailVerified
                firebaseAuth.currentUser!!.sendEmailVerification().await()
                res.status = true
                res.data = email
                Log.i("CURRENT USER ID IN REGISTER ", firebaseAuth.currentUser!!.uid)
                val data = userService.addUser(User(firebaseAuth.currentUser!!.uid, name, email, password, null))
                Log.i(data.user.toString(), "register: ")
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
                Log.i(res.data.toString(), "login: ")
                throw Exception("Please verify your email to login !!!")
            }
        } catch (e: Exception) {
            res.status = false
            res.data = if (e.message!!.contains("INVALID_LOGIN_CREDENTIALS"))
                    "INCORRECT INFORMATION"
                else
                    e.message.toString()
        }
        return res
    }

    suspend fun changePassword(password: String): ResponseObject{
        val res = ResponseObject()
        try {
            firebaseAuth.currentUser!!.updatePassword(password)
            userService.updateProfile(firebaseAuth.currentUser!!.uid, hashMapOf<String, Any>(
                "password" to password
            ))
            res.status = true
        }catch (e: Exception){
            res.data = e.message.toString()
            res.status = false
        }
        return res
    }

    suspend fun sendMailForgotPassword(email: String): ResponseObject {
        val res = ResponseObject()
        try {
            val passcode = Random.nextInt(100000, 999999)
            val savePasscodeToDB = userService.updateProfile(firebaseAuth.currentUser!!.uid, hashMapOf(
                "passcodeFGP" to passcode
            ))
            if(!savePasscodeToDB.status){
                throw Exception(savePasscodeToDB.data.toString())
            }
            Log.i("PASSCODE_BEGIN", "${savePasscodeToDB.status} and $passcode")
            val continueUrl = "https://www.example.com/forgotPassword?email=$email&passcode=$passcode"
            val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(continueUrl))
                .setDomainUriPrefix("https://androidfinalpj.page.link")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink()
            // Tạo liên kết cắt gọn
            val shortenedLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.uri)
                .buildShortDynamicLink().await()
            firebaseAuth.currentUser!!.email
            val deepLinkUrl = shortenedLink.shortLink.toString()
            Log.i("deepLinkUrl", deepLinkUrl)
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(deepLinkUrl)
                .setHandleCodeInApp(true)
                .build()
            if(email != firebaseAuth.currentUser!!.email){
                throw Exception("Email doesn't match registered email!")
            }
            firebaseAuth.sendPasswordResetEmail(email,actionCodeSettings).await()
            res.status = true
        }catch (e: Exception){
            res.data = e.message.toString()
            Log.i("ERROR", "${res.data}")
            res.status = false
        }
        return res
    }

    fun isLogin(): Boolean {
        return firebaseAuth.currentUser != null && firebaseAuth.currentUser!!.isEmailVerified
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