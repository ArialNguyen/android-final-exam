package com.example.final_android_quizlet.models

class Term(
    var term: String,
    var definition: String,
){
    override fun toString(): String {
        return "Term(term='$term', definition='$definition')"
    }
}