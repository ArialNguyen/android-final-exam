package com.example.final_android_quizlet.models

import java.io.Serializable

data class Term(
    var term: String,
    var definition: String,
): Serializable{
    constructor() : this(
        "",""
    )
    override fun toString(): String {
        return "Term(term='$term', definition='$definition')"
    }
}