package com.example.final_android_quizlet.auth

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
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
import kotlinx.coroutines.launch

class ChangePassword : AppCompatActivity() {

    private val authService = AuthService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSave: CircularProgressButton
    private lateinit var imgBack: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etOldPassword = findViewById(R.id.et_odd_password)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnSave = findViewById(R.id.btn_register)
        imgBack = findViewById(R.id.img_back_changePwd)

        btnSave.setOnClickListener {
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "New password and confirm password do not match.", Toast.LENGTH_SHORT).show()
            } else {
                changePassword(oldPassword, newPassword)
            }
        }
        imgBack.setOnClickListener {
            actionTransition.rollBackTransition()
            finish()

        }
    }

    private fun changePassword(oldPass: String, newPassword: String) {
        lifecycleScope.launch {
            btnSave.startAnimation()
            val user = authService.getUserLogin().user!!
            val fetch1 = authService.changePasswordForUserLogged(oldPass, newPassword)
            if(fetch1.status){
                Toast.makeText(this@ChangePassword, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                finish()
                actionTransition.rollBackTransition()
            }else{
                Toast.makeText(this@ChangePassword, "${fetch1.data}", Toast.LENGTH_SHORT).show()
            }
            btnSave.revertAnimation()
        }
    }
}