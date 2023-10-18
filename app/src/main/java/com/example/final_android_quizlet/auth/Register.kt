package com.example.final_android_quizlet.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.service.user.AuthService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private final var etUsername: TextInputEditText? = null
    private var etPwd: TextInputEditText? = null
    private var btnRegister: Button? = null
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById<TextInputEditText>(R.id.et_email_register)
        etPwd = findViewById<TextInputEditText>(R.id.et_password_register)
        btnRegister = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.spin_kit)

        btnRegister!!.setOnClickListener {
            lifecycleScope.launch {
                progressBar!!.visibility = View.VISIBLE
                val email = etUsername!!.text.toString()
                val password = etPwd!!.text.toString()
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(this@Register, "Username or password can not be none", Toast.LENGTH_SHORT).show()
                }else{
                    val result: ResponseObject = authService.register(email, password)
                    if(result.status){
                        Toast.makeText(this@Register, "MoshiUser Registered successfully, please verify your email to login", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@Register, Login::class.java)
                        intent.putExtra("email", result.data.toString())
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@Register, result.data.toString(), Toast.LENGTH_LONG).show()
                    }
                }
                progressBar!!.visibility = View.GONE
            }
        }
    }
}