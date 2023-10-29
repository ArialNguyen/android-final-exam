package com.example.final_android_quizlet.auth

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import com.example.final_android_quizlet.R
import com.google.firebase.auth.FirebaseAuth

class Logout(private val activity: Activity) : Dialog(activity), View.OnClickListener {

    private lateinit var logoutButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_logout)

        logoutButton = findViewById(R.id.btnDialogLogout)
        cancelButton = findViewById(R.id.btnDialogCancel)

        logoutButton.setOnClickListener(this)
        cancelButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnDialogLogout -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(activity, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
                activity.finish()
                dismiss()
            }
            R.id.btnDialogCancel -> dismiss()
        }
    }
}