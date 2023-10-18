package com.example.final_android_quizlet.dao

import com.example.final_android_quizlet.models.User

class ResponseObject(
    public var status: Boolean = false,
    public var data: Any? = null,
    public var user: User? = null
){}