package com.example.final_android_quizlet.models

data class Term(
    var term: String,
    var definition: String,
){
    constructor() : this(
        "",""
    )
    override fun toString(): String {
        return "Term(term='$term', definition='$definition')"
    }
}