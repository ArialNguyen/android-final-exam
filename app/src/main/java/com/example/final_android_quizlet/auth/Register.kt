package com.example.final_android_quizlet.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.user.AuthService
import com.google.android.material.textfield.TextInputEditText

class Register : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private final var etUsername: TextInputEditText? = null
    private var etPwd: TextInputEditText? = null
    private var btnRegister: Button? = null
    private var progressBar: ProgressBar? = null
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById<TextInputEditText>(R.id.et_email_register)
        etPwd = findViewById<TextInputEditText>(R.id.et_password_register)
        btnRegister = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.loader_register)

        btnRegister!!.setOnClickListener {
            val email = etUsername!!.text.toString()
            val password = etPwd!!.text.toString()
            manageScopeApi.getResponseWithCallback(
                lifecycleScope,
                { (authService::register)(email, password) },
                object : CallbackInterface {
                    override fun onBegin() {
                        progressBar!!.visibility = View.VISIBLE
                    }

                    override fun onValidate(): Boolean {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(this@Register, "Username or password can not be none", Toast.LENGTH_SHORT)
                                .show()
                            return false
                        }
                        return true
                    }

                    override fun onCallback(res: ResponseObject) {
                        if (res.status) {
                            Toast.makeText(
                                this@Register,
                                "Registered successfully, please verify your email to login",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@Register, Login::class.java)
                            intent.putExtra("email", res.data.toString())
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@Register, res.data.toString(), Toast.LENGTH_LONG).show()
                        }
                        progressBar!!.visibility = View.GONE
                    }
                })
        }
    }
}
