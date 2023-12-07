package com.example.final_android_quizlet.fragments.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.DialogClickedEvent
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment

class DialogFeedBackChoiceTest(
    private val result: Boolean,
    private val ques: String,
    private val ans: String,
    private val ownerAns: String,
    private val dialogClickedEvent: DialogClickedEvent.FeedBackChoiceTest
) : SupportBlurDialogFragment() {

    // View
    private lateinit var llTrue: LinearLayout
    private lateinit var llFalse: LinearLayout
    private lateinit var llResultAnswerTrue: LinearLayout
    private lateinit var llResultAnswerFalse: LinearLayout
    private lateinit var tvQues: TextView
    private lateinit var tvAns: TextView
    private lateinit var tvTermAnswerTrue: TextView
    private lateinit var tvTermAnswerFalse: TextView
    private lateinit var btnOK: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dialog_feed_back_choice_test, container, false)

        if(dialog != null && dialog!!.window != null){
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        llTrue = view.findViewById(R.id.llTrue_FGFeedBack)
        llFalse = view.findViewById(R.id.llFalse_FGFeedBack)
        llResultAnswerTrue = view.findViewById(R.id.llResultTrue_FGFeedBack)
        llResultAnswerFalse = view.findViewById(R.id.llResultFalse_FGFeedBack)
        tvQues = view.findViewById(R.id.tvTermQues_FGFeedBack)
        tvAns = view.findViewById(R.id.tvTermAnswer_FGFeedBack)
        tvTermAnswerTrue = view.findViewById(R.id.tvTermAnswerTrue_FGFeedBack)
        tvTermAnswerFalse= view.findViewById(R.id.tvTermAnswerFalse_FGFeedBack)

        btnOK = view.findViewById(R.id.btnOK_FGFeedBack)
        tvQues.text = ques
        if (result) {
            llTrue.visibility = View.VISIBLE
            llResultAnswerTrue.visibility = View.VISIBLE
            tvAns.text = ans
        } else {
            llFalse.visibility = View.VISIBLE
            llResultAnswerFalse.visibility = View.VISIBLE
            tvTermAnswerTrue.text = ans
            tvTermAnswerFalse.text = ownerAns
        }

        btnOK.setOnClickListener {
            dialogClickedEvent.setSuccessButton()
            dismiss()
        }

        return view
    }



}