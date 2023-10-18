package com.example.final_android_quizlet.models

class User(
    val name: String?,
    val email: String,
    val password: String,
    val className: String?
){
    constructor() : this(
        "", "", "", ""
    )
    override fun toString(): String {
        return "MoshiUser(name='$name', email='$email', password='$password', className='$className')"
    }
}