package com.example.final_android_quizlet.mapper

import android.util.Log
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User
import com.google.firebase.firestore.DocumentSnapshot
import org.modelmapper.ModelMapper

class UserMapper {
    private val mapper = ModelMapper()
    fun convertToUser(user: MutableMap<String, Any>): User{
        return mapper.map(user, User::class.java)
    }
    fun convertToUsers(users: List<DocumentSnapshot>): MutableList<User>{
        return users.map {
            val user = convertToUser(it.data!!)
            user.createdAt = it.getTimestamp("createdAt")?.toDate() // !!
            return@map user
        }.toMutableList()
    }
}