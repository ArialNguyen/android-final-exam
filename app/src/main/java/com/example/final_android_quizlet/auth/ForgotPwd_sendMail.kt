package com.example.final_android_quizlet.auth

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.user.AuthService
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton

class ForgotPwd_sendMail : AppCompatActivity() {

    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val authService: AuthService = AuthService()
    private val actionTransition: ActionTransition = ActionTransition(this)

    private var tvEmail: TextView? = null
    private var btnPwdSendMail: CircularProgressButton? = null
    private var imgBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pwd_send_mail)

        tvEmail = findViewById(R.id.et_email_forgot)
        btnPwdSendMail = findViewById(R.id.btn_forgotPWD_sendMail)
        imgBack = findViewById(R.id.img_back_forgot_sendMail)

        btnPwdSendMail!!.setOnClickListener {
            val email = tvEmail!!.text.toString()
            manageScopeApi.getResponseWithCallback(
                lifecycleScope,
                { (authService::sendMailForgotPassword)(email) },
                object :
                    CallbackInterface {
                    override fun onBegin() {
                        btnPwdSendMail!!.startAnimation()
                    }
                    override fun onValidate(): Boolean {
                        return email.isNotEmpty()
                    }
                    override fun onCallback(res: ResponseObject) {
                        if (res.status) {
                            Toast.makeText(
                                this@ForgotPwd_sendMail,
                                "We had sent the link to reset password to $email",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(this@ForgotPwd_sendMail, res.data.toString(), Toast.LENGTH_LONG).show()
                        }
                        btnPwdSendMail!!.revertAnimation()
                    }
                })
        }
        imgBack!!.setOnClickListener {
            finish()
            actionTransition.rollBackTransition()
        }
    }

}