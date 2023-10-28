package com.example.final_android_quizlet.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.MainActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.user.AuthService
import com.example.final_android_quizlet.service.user.UserService
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ForgotPwd : AppCompatActivity() {
    var btnForgotPwd: Button? = null
    var etPwd: EditText? = null
    var etPwd2: EditText? = null
    private val authService: AuthService = AuthService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val userService: UserService = UserService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pwd)
        // Always check user logged in or not
        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java).putExtra("checkLogin", ForgotPwd::class.java.name))
        }

        lifecycleScope.launch{
            Log.i("DEEPLINk", "${intent.data}")
            val continueUrlShortLink = Uri.parse(intent.data!!.toString()).getQueryParameter("continueUrl")!!
            val continueUrlLongLink = FirebaseDynamicLinks.getInstance().getDynamicLink(Uri.parse(continueUrlShortLink)).await()
            Log.i("LONG LINK", "${continueUrlLongLink.link}")
            val finalLink = Uri.parse(continueUrlLongLink.link.toString())
            Log.i("DEEP LINK IN FORGOT PASS", "$finalLink")

            val email = finalLink.getQueryParameter("email")!!
            val passcode = finalLink.getQueryParameter("passcode")!!
            if(email.isEmpty() || passcode.isEmpty()){
                Toast.makeText(this@ForgotPwd, "Sorry but seem the param of link to UI forgot password wrong here", Toast.LENGTH_SHORT).show()
            }else{
                Log.i("PASSCODE_GET", "$email and $passcode")
                if(!userService.checkPasscodeFGP(email, passcode).status){
                    Toast.makeText(this@ForgotPwd, "Sorry but seem the passcode to UI forgot password wrong here", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnForgotPwd = findViewById(R.id.btn_forgot_password)
        etPwd = findViewById(R.id.et_newPWD)
        etPwd2 = findViewById(R.id.et_password2)
        btnForgotPwd!!.setOnClickListener {
            manageScopeApi.getResponseWithCallback(lifecycleScope, {(authService::changePassword)(etPwd!!.text.toString())}, object : CallbackInterface{
                override fun onCallback(res: ResponseObject) {
                    if(res.status){
                        Toast.makeText(this@ForgotPwd, "Update password successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ForgotPwd, Login::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@ForgotPwd, res.data.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
//

    }

}