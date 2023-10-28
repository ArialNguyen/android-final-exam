package com.example.final_android_quizlet.common

import android.app.Activity
import android.content.Context
import com.example.final_android_quizlet.R

class ActionTransition (private val activity: Activity){
    fun rollBackTransition(){
        this.activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    fun moveNextTransition(){
        this.activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}