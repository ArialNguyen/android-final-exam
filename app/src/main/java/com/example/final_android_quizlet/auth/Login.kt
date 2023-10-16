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
import com.example.final_android_quizlet.MainActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.service.user.AuthService
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private var tvUsername: EditText? = null
    private var tvPwd: EditText? = null
    private var btnLogin: Button? = null
    private var btnRegister: Button? = null
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tvUsername = findViewById<EditText>(R.id.et_email_login)
        tvPwd = findViewById<EditText>(R.id.et_password_login)
        btnLogin = findViewById<Button>(R.id.btn_login)
        btnRegister = findViewById<Button>(R.id.btn_move_register)
        progressBar = findViewById(R.id.spin_kit)

        btnLogin!!.setOnClickListener {
            lifecycleScope.launch {
                progressBar!!.visibility = View.VISIBLE
                val email = tvUsername!!.text.toString()
                val password = tvPwd!!.text.toString()
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(this@Login, "Username or password can not be none", Toast.LENGTH_SHORT).show()
                }else{
                    val res = authService.login(email, password)
                    if(res["status"] == true){
                        val emailResponse = res["data"]
                        val intent = Intent(this@Login, MainActivity::class.java)
                        Log.i("EMAIL", "${emailResponse}")
                        intent.putExtra("email", emailResponse.toString())
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@Login, res["data"].toString(), Toast.LENGTH_LONG).show()
                    }
                }
                progressBar!!.visibility = View.GONE
            }
        }

        btnRegister!!.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

    }
}