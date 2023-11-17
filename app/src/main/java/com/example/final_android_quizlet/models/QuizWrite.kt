package com.example.final_android_quizlet.models

import java.io.Serializable

data class QuizWrite(
    var uid: String,
    var topicId: String,
    var listAnswer: MutableList<Answer>,
    var overall: Int,
    var userId: String
): Serializable {
    constructor() : this("", "", mutableListOf(), 0, "")

    override fun toString(): String {
        return "QuizWrite(uid='$uid', topicId='$topicId', listAnswer=$listAnswer, overall='$overall', userId='$userId')"
    }
}

data class Answer(
    var answer: String,
    var term: Term,
    var result: Boolean,
): Serializable {
    constructor() : this( "", Term(), false)

    override fun toString(): String {
        return "Answer(answer='$answer', term='$term', result='$result')"
    }
}