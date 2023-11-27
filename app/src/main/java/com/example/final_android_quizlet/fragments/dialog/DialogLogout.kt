package com.example.final_android_quizlet.fragments.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.final_android_quizlet.MainActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.DialogClickedEvent
import com.example.final_android_quizlet.service.AuthService
import com.google.android.material.button.MaterialButton

class DialogLogout(private val activity: Activity, private val ctx: Context, private val dialogClickedEvent: DialogClickedEvent) : DialogFragment() {
    private val authService: AuthService = AuthService()
    private val actionTransition: ActionTransition = ActionTransition(activity)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.dialog_logout, container, false)
    

        rootView.findViewById<MaterialButton>(R.id.btnDialogLogout).setOnClickListener {
            authService.logout()
            startActivity(Intent(ctx, MainActivity::class.java))
            actionTransition.moveNextTransition()
        }
        rootView.findViewById<MaterialButton>(R.id.btnDialogCancel_logout).setOnClickListener {
            dismiss()
        }
        return rootView
    }
}