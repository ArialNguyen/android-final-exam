package com.example.final_android_quizlet.models

import java.io.Serializable


enum class EAnswer{
    TERM, DEFINITION
}
class OptionExamData(
    var numberQues: Int,
    var showAns: Boolean,
    var shuffle: Boolean,
    var answer: EAnswer,
) : Serializable{
    constructor(): this(
        0, false, false, EAnswer.DEFINITION
    )

    override fun toString(): String {
        return "OptionExamData(numberQues=$numberQues, showAns=$showAns, shuffle=$shuffle, answerLanguage=$answer)"
    }

}