package com.example.final_android_quizlet.dao

import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User

class ResponseObject(
    public var status: Boolean = false,
    public var data: Any? = null,
    public var user: User? = null,
    public var topic: Topic? = null,
    public var topics: List<Topic>? = null,
    public var folder: Folder? = null
){}

fun ResponseObject.clone(): ResponseObject {
    val res = ResponseObject()
    res.data = this.data
    res.status = this.status
    res.user = this.user
    res.topic = this.topic
    res.topics = this.topics
    res.folder = this.folder
    return res
}