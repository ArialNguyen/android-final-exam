package com.example.final_android_quizlet.models

import java.util.*

data class Topic (
    var uid: String,
    var title: String,
    var description: String,
    var terms: List<Term>,
    var userId: String
){

     constructor() : this(
         "","", "", listOf(), ""
     )
    override fun toString(): String {
        return "Topic(title='$title', description=$description, terms=$terms, userId=$userId)"
    }
}