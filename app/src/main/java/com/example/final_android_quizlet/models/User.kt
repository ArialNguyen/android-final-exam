package com.example.final_android_quizlet.models

class User(
    val uid: String,
    val name: String?,
    val email: String,
    val password: String,
    val className: String?,
    val passcodeFGP: Int? = null
) {
    constructor() : this(
        "", "", "", "", ""
    )

    override fun toString(): String {
        return "MoshiUser(uid = '$uid', name='$name', email='$email', password='$password', className='$className')"
    }
}