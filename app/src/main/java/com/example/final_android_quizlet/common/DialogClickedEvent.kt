package com.example.final_android_quizlet.common

interface DialogClickedEvent {
    fun setSuccessButton(name: String, des: String) {}

    fun setCancelButton(){}

    interface FeedBackChoiceTest{
        fun setSuccessButton() {}

        fun setCancelButton(){}
    }
}