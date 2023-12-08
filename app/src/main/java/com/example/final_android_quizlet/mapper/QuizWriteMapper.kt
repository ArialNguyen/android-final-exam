package com.example.final_android_quizlet.mapper

import com.example.final_android_quizlet.models.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import org.modelmapper.ModelMapper

class QuizWriteMapper {
    private val mapper = ModelMapper()

    fun convertToQuizWrite(quizWrite: DocumentSnapshot): QuizWrite {
        val uid = quizWrite["uid"] as String
        val answers = mutableListOf<Answer>()
        val list = quizWrite["answers"] as? List<Map<String, Any>>
        val createdAt = (quizWrite["createdAt"] as Timestamp).toDate()
        list?.forEach { map ->
            val term = map["term"] as Map<String, Any>
            val answer = map["answer"] as String
            val result = map["result"] as Boolean
            answers.add(
                Answer(
                Term(
                    term["uid"] as String,
                    term["term"] as String,
                    term["definition"] as String,
                ),
                answer,
                result
            )
            )
        }
        val optionMap = quizWrite["optionExam"] as Map<String, Any>
        val optionData = OptionExamData(
            optionMap["numberQues"].toString().toInt(), optionMap["showAns"].toString().toBoolean(),
            optionMap["shuffle"].toString().toBoolean(), EAnswer.valueOf(optionMap["answer"].toString()), optionMap["autoSpeak"].toString().toBoolean()
        )
        val overall = quizWrite["overall"] as Number
        val totalQuestion = quizWrite["totalQuestion"] as Number
        val topicId = quizWrite["topicId"] as String
        val userId = quizWrite["userId"] as String
        val multipleChoice = QuizWrite(uid, topicId, answers, overall, optionData, totalQuestion.toInt(), userId)
        multipleChoice.createdAt = createdAt
        return multipleChoice
    }

    fun convertToQuizWrites(quizWrites: List<DocumentSnapshot>): List<QuizWrite> {
        return quizWrites.map {
            return@map convertToQuizWrite(it)
        }
    }
}