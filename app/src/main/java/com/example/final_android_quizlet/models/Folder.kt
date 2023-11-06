package com.example.final_android_quizlet.models

class Folder(
    var name: String,
    var description: String,
    var topics: List<Topic>,
    var userId: String
) {
    constructor() : this(
        "", "", listOf(), ""
    )

    override fun toString(): String {
        return "Folder(name='$name', description='$description', topics=$topics, userId=$userId)"
    }

}