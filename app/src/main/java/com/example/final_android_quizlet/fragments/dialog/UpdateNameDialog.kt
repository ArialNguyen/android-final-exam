package com.example.final_android_quizlet.fragments.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.final_android_quizlet.R

class UpdateNameDialog(
    context: Context,
    private val oldName: String,
    private val onUpdateName: (String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_update_nameuser)

        val edtNewName = findViewById<EditText>(R.id.etChangeName_ProfileActivity)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnOkay = findViewById<Button>(R.id.btnOkay)

        edtNewName.setText(oldName)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnOkay.setOnClickListener {
            val newName = edtNewName.text.toString()
            onUpdateName(newName)
            dismiss()
        }
    }
}