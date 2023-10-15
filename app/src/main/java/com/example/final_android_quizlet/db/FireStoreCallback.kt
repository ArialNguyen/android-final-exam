package com.example.final_android_quizlet.db

import com.example.final_android_quizlet.models.User

interface FireStoreCallback {
    fun onCallback(userList: List<User>)
    fun onCallback(status: String)
}