package com.example.final_android_quizlet.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.UserService
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton

class ForgotPwd : AppCompatActivity() {

    private val authService: AuthService = AuthService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val userService: UserService = UserService()
    private val actionTransition: ActionTransition = ActionTransition(this)


    private var btnForgotPwd: CircularProgressButton? = null
    private var etPwd: EditText? = null
    private var etPwd2: EditText? = null
    private var imgBack: ImageView? = null
    private var tvHelloEmail: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pwd)


        btnForgotPwd = findViewById(R.id.btn_forgot_password)
        etPwd = findViewById(R.id.et_newPWD)
        etPwd2 = findViewById(R.id.et_password2)
        imgBack = findViewById(R.id.img_back_forgotPWD)
        tvHelloEmail = findViewById(R.id.tvHelloEmail)

        val email = if (intent.getStringExtra("email") != null) intent.getStringExtra("email")!! else ""
        val emailText = "Hi $email"
        tvHelloEmail!!.text = emailText

        btnForgotPwd!!.setOnClickListener {
            val pass1 = etPwd!!.text.toString()
            val pass2 = etPwd2!!.text.toString()
            manageScopeApi.getResponseWithCallback(lifecycleScope, {(authService::changePassword)(email, pass1)}, object : CallbackInterface{
                override fun onBegin() {
                    btnForgotPwd!!.startAnimation()
                }

                override fun onValidate(): Boolean {
                    return pass1 == pass2
                }

                override fun onCallback(res: ResponseObject) {
                    if(res.status){
                        Toast.makeText(this@ForgotPwd, "Update password successfully", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@ForgotPwd, Login::class.java))
                        finish()
                        actionTransition.rollBackTransition()
                    }else{
                        Toast.makeText(this@ForgotPwd, res.data.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        imgBack!!.setOnClickListener {
            finish()
            actionTransition.rollBackTransition()
        }

    }

}