package com.example.final_android_quizlet.common

import android.content.Context
import android.content.SharedPreferences
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Session(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)

    private val gson = Gson()
    var user: User?
        set(userP){
            with(sharedPreferences.edit()) {
                putString("user", gson.toJson(userP))
                apply()
            }
        }
        get() {
            val user = sharedPreferences.getString("user", null)
            return gson.fromJson(user, User::class.java)
        }
    var users: MutableList<User>?
        set(userP){
            with(sharedPreferences.edit()) {
                putString("users", gson.toJson(userP))
                apply()
            }
        }
        get() {
            val users = sharedPreferences.getString("users", null)
            val type = object : TypeToken<List<User>>() {}.type
            return gson.fromJson(users, type)
        }
    var topicsOfUser: MutableList<Topic>?
        set(topics){
            with(sharedPreferences.edit()) {
                putString("topicsOfUser", gson.toJson(topics))
                apply()
            }
        }
        get() {
            val topics = sharedPreferences.getString("topicsOfUser", null)
            val type = object : TypeToken<List<Topic>>() {}.type
            return gson.fromJson(topics, type)
        }

    var topicsOfUserSaved: MutableList<Topic>?
        set(topics){
            with(sharedPreferences.edit()) {
                putString("topicsOfUserSaved", gson.toJson(topics))
                apply()
            }
        }
        get() {
            val topics = sharedPreferences.getString("topicsOfUserSaved", null)
            val type = object : TypeToken<List<Topic>>() {}.type
            return gson.fromJson(topics, type)
        }

    var topicsPublic: MutableList<Topic>?
        set(topics){
            with(sharedPreferences.edit()) {
                putString("topicsPublic", gson.toJson(topics))
                apply()
            }
        }
        get() {
            val topics = sharedPreferences.getString("topicsPublic", null)
            val type = object : TypeToken<List<Topic>>() {}.type
            return gson.fromJson(topics, type)
        }

    var foldersOfUser: MutableList<Folder>?
        set(topics){
            with(sharedPreferences.edit()) {
                putString("foldersOfUser", gson.toJson(topics))
                apply()
            }
        }
        get() {
            val topics = sharedPreferences.getString("foldersOfUser", null)
            val type = object : TypeToken<List<Folder>>() {}.type
            return gson.fromJson(topics, type)
        }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
    fun clearData(type: Type) {
        if (type == User::class.java) sharedPreferences.edit().remove("user").apply()
        else if (type == object : TypeToken<List<Topic>>() {}.type) sharedPreferences.edit().remove("topics").apply()
        else if (type == object : TypeToken<List<Folder>>() {}.type) sharedPreferences.edit().remove("foldersOfUser").apply()

    }


    companion object {
        private var instance: Session? = null

        @Synchronized
        fun getInstance(context: Context): Session {
            if (instance == null) {
                instance = Session(context)
            }
            return instance!!
        }
    }
}