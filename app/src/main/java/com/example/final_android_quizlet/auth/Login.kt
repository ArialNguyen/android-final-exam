package com.example.final_android_quizlet.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.MainActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.user.AuthService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.Serializable

class Login : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private var tvUsername: EditText? = null
    private var tvPwd: EditText? = null
    private var btnLogin: Button? = null
    private var btnRegister: Button? = null
    private var progressBar: ProgressBar? = null
    private var manageScopeApi: ManageScopeApi = ManageScopeApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tvUsername = findViewById<EditText>(R.id.et_email_login)
        tvPwd = findViewById<EditText>(R.id.et_password_login)
        btnLogin = findViewById<Button>(R.id.btn_login)
        btnRegister = findViewById<Button>(R.id.btn_move_register)
        progressBar = findViewById(R.id.spin_kit)
        var classTarget = MainActivity::class.java.name
        if (intent.getStringExtra("email") != null) {
            tvUsername!!.setText(intent.getStringExtra("email"))
        }

        if(intent.getStringExtra("checkLogin") != null){
            classTarget = intent.getStringExtra("checkLogin").toString()
        }

        btnLogin!!.setOnClickListener {
            val email = tvUsername!!.text.toString()
            val password = tvPwd!!.text.toString()
            manageScopeApi.getResponseWithCallback(
                lifecycleScope,
                { (authService::login)(email, password) },
                object : CallbackInterface {
                    override fun onBegin() {
                        progressBar!!.visibility = View.VISIBLE
                    }

                    override fun onValidate(): Boolean {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(this@Login, "Username or password can not be none", Toast.LENGTH_SHORT)
                                .show()
                            return false
                        }
                        return true
                    }

                    override fun onCallback(res: ResponseObject) {
                        if (res.status) {
                            Log.i("FINISH", "onCallback: ")
                            val intent = Intent(this@Login, Class.forName(classTarget))
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Login, res.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFinally() {
                        progressBar!!.visibility = View.GONE
                    }
                })
        }

        btnRegister!!.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

    }
}