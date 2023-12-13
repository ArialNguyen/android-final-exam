package com.example.final_android_quizlet.fragments.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.example.final_android_quizlet.R

class DialogLoading(
    private val ctx: Context,
)  {
    private lateinit var dialog: Dialog
    fun showDialog(title: String){
        (ctx as Activity).runOnUiThread {
            dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_loading)

            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle_dialogLoading)
            tvTitle.text = title
            dialog.create()
            dialog.show()
        }
    }

    fun hideDialog(){
        (ctx as Activity).runOnUiThread {
            if (::dialog.isInitialized){
                dialog.dismiss()
            }
        }
    }

}