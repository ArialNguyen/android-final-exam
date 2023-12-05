package com.example.final_android_quizlet.mapper

import com.example.final_android_quizlet.models.AnswerChoice
import com.example.final_android_quizlet.models.MultipleChoice
import com.example.final_android_quizlet.models.Term
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import org.modelmapper.ModelMapper

class ChoiceTestMapper {
    private val mapper = ModelMapper()

    fun convertToChoiceTest(choiceTest: DocumentSnapshot): MultipleChoice {
        val uid = choiceTest["uid"] as String
        val answers = mutableListOf<AnswerChoice>()
        val list = choiceTest["answers"] as? List<Map<String, Any>>
        val createdAt = (choiceTest["createdAt"] as Timestamp).toDate()
        list?.forEach { map ->
            val term = map["term"] as Map<String, Any>
            val answer = map["answer"] as String
            val result = map["result"] as Boolean
            answers.add(AnswerChoice(
                Term(
                    term["uid"] as String,
                    term["term"] as String,
                    term["definition"] as String,
                ),
                answer,
                result
            ))
        }
        val overall = choiceTest["overall"] as Number
        val topicId = choiceTest["topicId"] as String
        val userId = choiceTest["userId"] as String
        val multipleChoice = MultipleChoice(uid, answers, overall, topicId, userId)
        multipleChoice.createdAt = createdAt
        return multipleChoice
    }
    fun convertToChoicesTest(choicesTest: List<DocumentSnapshot>): List<MultipleChoice> {
        return choicesTest.map {
            return@map convertToChoiceTest(it)
        }
    }
}