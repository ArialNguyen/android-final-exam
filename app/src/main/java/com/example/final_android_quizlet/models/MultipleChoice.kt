package com.example.final_android_quizlet.models

import java.io.Serializable
import java.util.*

data class MultipleChoice(
    var uid: String,
    var answers: MutableList<AnswerChoice>,
    var overall: Number,
    var topicId: String,
    var userId: String,
) : Serializable {
    var createdAt: Date?= null

    constructor() : this(
        "", mutableListOf(), 0, "", ""
    )
    override fun toString(): String {
        return "MultipleChoice(uid='$uid', answers=$answers, overall=$overall, topicId='$topicId', userId='$userId')"
    }
}

data class AnswerChoice(
    var term: Term,
    var answer: String,
    var result: Boolean
): Serializable {
    override fun toString(): String {
        return "AnswerChoice(term=$term, answer='$answer', result=$result)"
    }
}

