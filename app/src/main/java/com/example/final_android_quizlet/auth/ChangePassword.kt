package com.example.final_android_quizlet.auth

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.service.user.AuthService
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton

class ChangePassword : AppCompatActivity() {
    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSave: CircularProgressButton
    private val authService = AuthService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etOldPassword = findViewById(R.id.et_odd_password)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnSave = findViewById(R.id.btn_register)

        btnSave.setOnClickListener {
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()


            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "New password and confirm password do not match.", Toast.LENGTH_SHORT).show()
            } else {
                val user = authService.getUserLogin()
            }
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        btnSave.startAnimation()

        manageScopeApi.getResponseWithCallback(
            lifecycleScope,
            { authService.changePassword(newPassword) },
            object : CallbackInterface {
                override fun onBegin() {
                    btnSave!!.startAnimation()
                }

                override fun onValidate(): Boolean {
                    if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                        Toast.makeText(this@ChangePassword, "Please fill in old and new password.", Toast.LENGTH_SHORT).show()
                        return false
                    }
                    if(newPassword != confirmPassword){
                        Toast.makeText(this@ChangePassword, "Two password must be same", Toast.LENGTH_SHORT)
                            .show()
                        return false
                    }
                    return true
                }

                override fun onCallback(res: ResponseObject) {
                    if (res.status) {
                        Toast.makeText(this@ChangePassword, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ChangePassword, "Failed to change password: ${res.data}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFinally() {
                    btnSave.revertAnimation()
                }
            }
        )
    }
}