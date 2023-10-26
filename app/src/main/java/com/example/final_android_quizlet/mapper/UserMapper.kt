package com.example.final_android_quizlet.mapper

import android.util.Log
import org.modelmapper.ModelMapper

class UserMapper {
    private val mapper = ModelMapper()
    fun convertToUser(user: Any): com.example.final_android_quizlet.models.User{
        Log.i("MAPPER", user.toString())
        return mapper.map(user, com.example.final_android_quizlet.models.User::class.java)
    }

}