package com.example.final_android_quizlet.models

import java.io.Serializable
import java.util.UUID

data class Topic (
    var uid: String,
    var title: String,
    var description: String,
    var terms: List<Term>,
    var userId: String
): Serializable{

     constructor() : this(
         UUID.randomUUID().toString(),"", "", listOf(), ""
     )
    override fun toString(): String {
        return "Topic(uid='$uid', title='$title', description=$description, terms=$terms, userId=$userId)"
    }
}