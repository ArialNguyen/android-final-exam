package com.example.final_android_quizlet.models

class User(
    val uid: String,
    val name: String?,
    val email: String,
    val avatar: String?,
    val passwordAuth: String,
    val password: String,
    val passcodeFGP: Int? = null
    // topicSaved: TopicId???
) {
    constructor() : this(
        "", "", "", "", "", "", null
    )

    override fun toString(): String {
        return "MoshiUser(uid = '$uid', name='$name', email='$email', avatar='$avatar', passwordAuth='$passwordAuth', password='$password', passcodeFGP='$password')"
    }
}