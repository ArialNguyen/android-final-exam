package com.example.final_android_quizlet.models

import com.example.final_android_quizlet.models.Enum.ETermList
import java.io.Serializable
import java.util.*

data class QuizWrite(
    var uid: String,
    var topicId: String,
    var answers: MutableList<Answer>,
    var overall: Number,
    var optionExam: OptionExamData,
    var totalQuestion: Int,
    var userId: String,
    var termType: ETermList
): Serializable {
    var createdAt: Date?= null
    constructor() : this("", "", mutableListOf(), 0, OptionExamData(), 0, "", ETermList.NORMAL_TERMS)

    override fun toString(): String {
        return "QuizWrite(uid='$uid', topicId='$topicId', answers=$answers, overall=$overall, optionExam=$optionExam, totalQuestion=$totalQuestion, userId='$userId', termType='$termType', createdAt=$createdAt)"
    }
}

data class Answer(
    var term: Term,
    var answer: String,
    var result: Boolean,
): Serializable {
    constructor() : this( Term(),"",  false)

    override fun toString(): String {
        return "Answer(term=$term, answer='$answer', result=$result)"
    }
}