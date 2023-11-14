package com.example.final_android_quizlet.models

import java.io.Serializable

data class QuizWrite(
    var uid: String,
    var topicId: String,
    var listAnswer: List<Answer>,
    var overall: Float,
): Serializable {
    constructor() : this("", "", listOf(), 0.0f)

    override fun toString(): String {
        return "QuizWrite(uid='$uid', topicId='$topicId', listAnswer=$listAnswer, overall='$overall')"
    }
}