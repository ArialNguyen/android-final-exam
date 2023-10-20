package com.example.final_android_quizlet.fragments


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment

import com.example.final_android_quizlet.R

class dialog_folder : AppCompatDialogFragment() {
    private var editTextFolder: EditText? = null
    private var editText: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireActivity()) // Sử dụng requireActivity() thay vì activity
        val inflater = requireActivity().layoutInflater // Sử dụng requireActivity() thay vì activity
        val view: View = inflater.inflate(R.layout.activity_dialog_folder, null)
        builder.setView(view)
            .setTitle("Tạo thư mục")
            .setNegativeButton("HỦY") { dialogInterface, i -> }
            .setPositiveButton("OK") { dialogInterface, i ->
                val foldername = editTextFolder?.text.toString()
                val description = editText?.text.toString()
            }
        editTextFolder = view.findViewById(R.id.edit_foldername)
        editText = view.findViewById(R.id.edit_decription)
        return builder.create()
    }



}