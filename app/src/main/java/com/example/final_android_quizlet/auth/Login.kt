package com.example.final_android_quizlet.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.MainActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.AuthService
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private var manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)

    private var tvUsername: EditText? = null
    private var tvPwd: EditText? = null
    private var btnLogin: CircularProgressButton? = null
    private var btnRegister: TextView? = null
    private var tvForgotPwd: TextView? = null

    private val REGISTER_CODE = 1
    private val FORGORPWD_CODE = 2

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == REGISTER_CODE) {
            val data: Intent? = result.data
            tvUsername!!.setText(data!!.getStringExtra("email"))
        }else if (result.resultCode == FORGORPWD_CODE){
            // ???
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tvUsername = findViewById(R.id.et_email_login)
        tvPwd = findViewById(R.id.et_password_login)
        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_move_register)
        tvForgotPwd = findViewById(R.id.tvForgotPwd)
//        var classTarget = MainActivity::class.java.name

        if(authService.isLogin()){
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    Session.getInstance(this@Login).user = authService.getUserLogin().user
                    actionTransition.rollBackTransition()
                    finish()
                }
            }
        }


        btnLogin!!.setOnClickListener {
            val email = tvUsername!!.text.toString()
            val password = tvPwd!!.text.toString()
            manageScopeApi.getResponseWithCallback(
                lifecycleScope,
                { (authService::login)(email, password) },
                object : CallbackInterface {
                    override fun onBegin() {
                        btnLogin!!.startAnimation()
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
                            Session.getInstance(this@Login).user = res.user
                            actionTransition.rollBackTransition()
                            finish()
                        } else {
                            Toast.makeText(this@Login, res.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFinally() {
                        btnLogin!!.revertAnimation();
//                        btnLogin!!.doneLoadingAnimation(fillColor, imageBitMap)
                    }
                })
        }

        btnRegister!!.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            resultLauncher.launch(intent)
            actionTransition.moveNextTransition()
        }

        tvForgotPwd!!.setOnClickListener {
            val intent = Intent(this, ForgotPwd_sendMail::class.java)
            startActivity(intent)
            actionTransition.moveNextTransition()
        }

    }
}