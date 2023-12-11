package com.example.final_android_quizlet.models

import com.example.final_android_quizlet.models.Enum.ETermList
import java.io.Serializable
import java.util.*

data class MultipleChoice(
    var uid: String,
    var answers: MutableList<AnswerChoice>,
    var overall: Number,
    var optionExam: OptionExamData,
    var totalQuestion: Int,
    var topicId: String,
    var userId: String,
    var termType: ETermList
) : Serializable {
    var createdAt: Date?= null

    constructor() : this(
        "", mutableListOf(), 0, OptionExamData(), 0,"", "", ETermList.NORMAL_TERMS
    )
    override fun toString(): String {
        return "MultipleChoice(uid='$uid', answers=$answers, overall=$overall, optionExam=$optionExam, totalQuestion=$totalQuestion, topicId='$topicId', userId='$userId', termType='$termType')"
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

