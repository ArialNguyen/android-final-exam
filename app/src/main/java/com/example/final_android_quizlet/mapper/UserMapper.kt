package com.example.final_android_quizlet.mapper

import org.modelmapper.ModelMapper

class UserMapper {
    private val mapper = ModelMapper()
    fun convertToUser(user: Any): com.example.final_android_quizlet.models.User{
        return mapper.map(user, com.example.final_android_quizlet.models.User::class.java)
    }

}