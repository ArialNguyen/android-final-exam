package com.example.final_android_quizlet.models

import java.io.Serializable

class Folder(
    var uid: String,
    var name: String,
    var description: String,
    var topics: MutableList<String>,
    var userId: String
) : Serializable{
    constructor() : this(
        "", "", "", mutableListOf(), ""
    )
    constructor(folder: Folder) : this() {
        uid = folder.uid
        name = folder.name
        description = folder.description
        topics = folder.topics
        userId = folder.userId
    }

    override fun toString(): String {
        return "Folder(id='$uid', name='$name', description='$description', topics=$topics, userId=$userId)"
    }

}