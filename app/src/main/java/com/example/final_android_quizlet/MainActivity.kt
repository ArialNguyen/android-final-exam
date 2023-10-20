package com.example.final_android_quizlet


import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.activity.CreateTermActivity
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.fragments.dialog_folder
import com.example.final_android_quizlet.service.user.AuthService
import com.example.final_android_quizlet.service.user.UserService


class MainActivity : AppCompatActivity() {
    private val userService: UserService = UserService()
    private val authService: AuthService = AuthService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvHello = findViewById<TextView>(R.id.textHello)

        val progrressBar: ProgressBar = findViewById(R.id.loadTextWelcome)
        val plusIcon: ImageView = findViewById(R.id.imageView5)

//        manageScopeApi.getResponseWithCallback(lifecycleScope, authService::getUserLogin, object : CallbackInterface{
//            override fun onBegin() {
//                //hien thi cai loader
//            }
//            override fun onCallback(res: ResponseObject) {
//                // lay data
//                // tat cai loader
//            }
//        })
        plusIcon.setOnClickListener {
            showBottomDialog()
        }
    }

    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_bottom_sheet)

        val createHocPhan: LinearLayout = dialog.findViewById(R.id.create_hoc_phan)
        val createFolder: LinearLayout = dialog.findViewById(R.id.create_folder)
        val createClass: LinearLayout = dialog.findViewById(R.id.create_class)
        val cancelButton: ImageView = dialog.findViewById(R.id.cancelButton)

        createHocPhan.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, CreateTermActivity::class.java)
            startActivity(intent)
        }

        createFolder.setOnClickListener {
            openDialog()
            dialog.dismiss()
        }

        createClass.setOnClickListener {
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    fun openDialog() {
        val exampleDialog = dialog_folder()
        exampleDialog.show(supportFragmentManager, "example dialog")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}