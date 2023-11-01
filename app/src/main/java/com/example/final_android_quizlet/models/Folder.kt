package com.example.final_android_quizlet.models

class Folder(
    val name: String,
    val description: String,
    val topics: List<String>
) {
    constructor() : this(
        "", "", listOf()
    )

    override fun toString(): String {
        return "Folder(name='$name', description='$description', topics=$topics)"
    }

}