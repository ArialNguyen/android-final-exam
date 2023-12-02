package com.example.final_android_quizlet.models

import java.io.Serializable
import java.util.Date
import java.util.UUID

enum class ELearnTopicStatus{
    NOT_LEARN, LEARNED
}

enum class EModeTopic{
    PUBLIC, PRIVATE
}

data class Topic(
    var uid: String,
    var title: String,
    var description: String,
    var terms: List<Term>,
    var userId: String,
    var mode: EModeTopic,
    var learnStatus: ELearnTopicStatus
): Serializable{
    var createdAt: Date? = null
    constructor() : this(
         UUID.randomUUID().toString(),"", "", listOf(), "",
        EModeTopic.PRIVATE, ELearnTopicStatus.NOT_LEARN
     )

    override fun toString(): String {
        return "Topic(uid='$uid', title='$title', description='$description', terms=$terms, userId='$userId', createdAt='$createdAt', mode=$mode, learnStatus=$learnStatus)"
    }

}