package com.example.final_android_quizlet.common

import java.io.Serializable

interface DialogClickedEvent {
    fun setSuccessButton(name: String, des: String) {}

    fun setCancelButton(){}

    interface FeedBackChoiceTest : Serializable {
        fun setSuccessButton() {}

        fun setCancelButton(){}
    }
}