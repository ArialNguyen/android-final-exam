package com.example.final_android_quizlet.models

import java.io.Serializable

data class Answer(
    var uid: String,
    var answer: String,
    var termId: String,
    var result: Boolean,
): Serializable {
    constructor() : this("", "", "", false)

    override fun toString(): String {
        return "Answer(uid='$uid', answer='$answer', termId='$termId', result='$result')"
    }
}