package com.example.final_android_quizlet.moshi

import com.example.final_android_quizlet.models.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class MoshiUser {
    private val moshi: JsonAdapter<User> = Moshi.Builder().build().adapter<User>(User::class.java)
    fun convertToJson(user: User): String {
        return moshi.toJson(user)
    }
    fun convertToObject(user: String?): User? {
        return moshi.fromJson(user)
    }
}