package com.example.final_android_quizlet.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.DialogClickedEvent

class DialogCreateClass(private val ctx: Context, private val dialogClickedEvent: DialogClickedEvent) : DialogFragment() {
    private var etClassName: EditText? = null
    private var etDescription: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.activity_dialog_folder, container, false)

        etClassName = rootView.findViewById(R.id.etClassName_createClass)
        etDescription = rootView.findViewById(R.id.etDescription_createClass)

        rootView.findViewById<Button>(R.id.btnOkay_createClass).setOnClickListener {
            val className = etClassName!!.text.toString()
            val des = etDescription!!.text.toString()
            if(className.isEmpty()){
                Toast.makeText(ctx, "Class Name must be required!", Toast.LENGTH_LONG).show()
            }else{
                dialogClickedEvent.setSuccessButton(className, des)
                dismiss()
            }
        }

        rootView.findViewById<Button>(R.id.btnCancel_createClass).setOnClickListener {
            dismiss()
        }
        return rootView
    }
}