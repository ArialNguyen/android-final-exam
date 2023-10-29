package com.example.final_android_quizlet.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.service.user.UserService
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ManageDeeplink : AppCompatActivity() {
    private val userService: UserService = UserService()
    private val actionTransition: ActionTransition = ActionTransition(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_deeplink)

        lifecycleScope.launch{
            val checkMode = Uri.parse(intent.data!!.toString())
            Log.i(TAG, "${checkMode.toString()}")
            if(checkMode.getQueryParameter("mode") == "verifyEmail"){
              Log.i(TAG, "onCreate: ")
                val intentBrowser = Intent(Intent.ACTION_VIEW)
                val oobCode = checkMode.getQueryParameter("oobCode")
                val apiKey = checkMode.getQueryParameter("apiKey")
                intentBrowser.setData(Uri.parse("https://android-finalpj.firebaseapp.com/__/auth/action?mode=verifyEmail&oobCode=$oobCode&apiKey=$apiKey&lang=en"))
                startActivity(intentBrowser)

//                finish()
            }else{
                val continueUrlShortLink = checkMode.getQueryParameter("continueUrl")!!
                val continueUrlLongLink = FirebaseDynamicLinks.getInstance().getDynamicLink(Uri.parse(continueUrlShortLink)).await()
                val finalLink = Uri.parse(continueUrlLongLink.link.toString())

                val email = finalLink.getQueryParameter("email")!!
                val passcode = finalLink.getQueryParameter("passcode")!!
                if(email.isEmpty() || passcode.isEmpty()){
                    Toast.makeText(this@ManageDeeplink, "Sorry but seem the param of link to UI forgot password wrong here", Toast.LENGTH_SHORT).show()
                }else{
                    val verifyCode = userService.checkPasscodeFGP(email, passcode)
                    if(!verifyCode.status){
                        Log.i("ERROR for check passcodeFGP", "${verifyCode.data}")
                        Toast.makeText(this@ManageDeeplink, "Sorry but seem the passcode to UI forgot password wrong here", Toast.LENGTH_SHORT).show()
                    }else{
                        val intentForgot = Intent(this@ManageDeeplink, ForgotPwd::class.java)
                        intentForgot.putExtra("email", email)
                        startActivity(intentForgot)
                        actionTransition.moveNextTransition()
                        finish()
                    }
                }
            }
        }
    }
}