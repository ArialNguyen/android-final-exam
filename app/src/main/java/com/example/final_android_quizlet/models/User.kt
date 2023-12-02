package com.example.final_android_quizlet.models

import java.io.Serializable
import java.util.*

data class User(
    var uid: String,
    var name: String,
    var email: String,
    var avatar: String,
    var passwordAuth: String,
    var password: String,
    var passcodeFGP: Int,
    var topicSaved: MutableList<String>
) : Serializable {
    var createdAt: Date? = null

    constructor() : this(
        UUID.randomUUID().toString(), "", "", "", "", "", 0, mutableListOf()
    )

    override fun toString(): String {
        return "User(uid='$uid', name=$name, email='$email', avatar=$avatar, passwordAuth='$passwordAuth', password='$password', passcodeFGP=$passcodeFGP, topicSaved=$topicSaved)"
    }


}