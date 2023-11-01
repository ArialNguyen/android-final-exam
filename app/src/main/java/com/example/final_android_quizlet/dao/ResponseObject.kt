package com.example.final_android_quizlet.dao

import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User

class ResponseObject(
    public var status: Boolean = false,
    public var data: Any? = null,
    public var user: User? = null,
    public var topic: Topic? = null
){}

fun ResponseObject.clone(): ResponseObject {
    val res = ResponseObject()
    res.data = this.data
    res.status = this.status
    res.user = this.user
    res.topic = this.topic
    return res
}