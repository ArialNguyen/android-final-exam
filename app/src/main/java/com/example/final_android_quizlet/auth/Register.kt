package com.example.final_android_quizlet.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
import com.example.final_android_quizlet.service.AuthService
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton

class Register : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private var etName: EditText? = null
    private var etEmail: EditText? = null
    private var etPwd: EditText? = null
    private var etPwd2: EditText? = null
    private var btnRegister: CircularProgressButton? = null
    private var textMoveToLogin: TextView? = null
    private var imgBack: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // form
        etName = findViewById(R.id.et_name_register)
        etEmail = findViewById(R.id.et_email_register)
        etPwd = findViewById(R.id.et_password_register)
        etPwd2 = findViewById(R.id.et_password2)
        btnRegister = findViewById(R.id.btn_register)
        //other
        textMoveToLogin = findViewById(R.id.move_to_login)
        imgBack = findViewById(R.id.img_back)

        textMoveToLogin!!.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            actionTransition.rollBackTransition()
        }

        imgBack!!.setOnClickListener {
            finish()
            actionTransition.rollBackTransition()
        }


        btnRegister!!.setOnClickListener {
            val name = etName!!.text.toString()
            val email = etEmail!!.text.toString()
            val password = etPwd!!.text.toString()
            val password2 = etPwd2!!.text.toString()
            manageScopeApi.getResponseWithCallback(
                lifecycleScope,
                { (authService::register)(name, email, password) },
                object : CallbackInterface {
                    override fun onBegin() {
                        btnRegister!!.startAnimation()
                    }

                    override fun onValidate(): Boolean {
                        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                            Toast.makeText(this@Register, "Information can not be none", Toast.LENGTH_SHORT)
                                .show()
                            return false
                        }
                        if(password != password2){
                            Toast.makeText(this@Register, "Two password must be same", Toast.LENGTH_SHORT)
                                .show()
                            return false
                        }
                        return true
                    }

                    override fun onCallback(res: ResponseObject) {
                        Log.i(res.status.toString(), "onCallback: ")
                        if (res.status) {
                            Toast.makeText(
                                this@Register,
                                "Registered successfully, please verify your email to login",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@Register, Login::class.java)
                            intent.putExtra("email", res.data.toString())
                            setResult(1, intent)
                            finish()
                            actionTransition.rollBackTransition()
                        } else {
                            Toast.makeText(this@Register, res.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFinally() {
                        btnRegister!!.revertAnimation()
                    }
                })
        }
    }

}
