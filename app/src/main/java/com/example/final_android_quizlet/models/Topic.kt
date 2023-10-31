package com.example.final_android_quizlet.models

 class Topic (
     val title: String,
     val description: String,
     val terms: List<Term> ){

     constructor() : this(
         "", "", listOf()
     )
    override fun toString(): String {
        return "Topic(title='$title', description=$description, terms=$terms)"
    }
}