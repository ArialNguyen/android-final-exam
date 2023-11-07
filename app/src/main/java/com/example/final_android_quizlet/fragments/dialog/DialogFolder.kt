package com.example.final_android_quizlet.fragments.dialog


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.DialogClickedEvent

class DialogFolder(private val ctx: Context, private val dialogClickedEvent: DialogClickedEvent) : DialogFragment() {
    private var etFolderName: EditText? = null
    private var etDescription: EditText? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.activity_dialog_folder, container, false)

        etFolderName = rootView.findViewById(R.id.etFolderName)!!
        etDescription = rootView.findViewById(R.id.etDescription)!!

        rootView.findViewById<Button>(R.id.btnOkay).setOnClickListener {
            val folderName = etFolderName!!.text.toString()
            val des = etDescription!!.text.toString()
            if(folderName.isEmpty()){
                Toast.makeText(ctx, "Folder Name must be required!", Toast.LENGTH_LONG).show()
            }else{
                dialogClickedEvent.setSuccessButton(folderName, des)
                dismiss()
            }
        }

        rootView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }
        return rootView
    }
}