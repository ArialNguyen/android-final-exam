package com.example.final_android_quizlet.models

import java.io.Serializable
import java.util.*

enum class ELearnTopicStatus {
    NOT_LEARN, LEARNED
}

enum class EModeTopic {
    PUBLIC, PRIVATE
}

data class Topic(
    var uid: String,
    var title: String,
    var description: String,
    var terms: List<Term>,
    var starList: MutableList<Term>,
    var userId: String,
    var mode: EModeTopic,
    var learnStatus: ELearnTopicStatus,
    var termLanguage: String?,
    var definitionLanguage: String?,

) : Serializable {
    var createdAt: Date? = null

    constructor() : this(
        UUID.randomUUID().toString(), "", "", listOf(), mutableListOf(), "",
        EModeTopic.PRIVATE, ELearnTopicStatus.NOT_LEARN, null, null
    )

    override fun toString(): String {
        return "Topic(uid='$uid', title='$title', description='$description', terms=$terms, starList=$starList, userId='$userId', mode=$mode, learnStatus=$learnStatus, termLanguage=$termLanguage, definitionLanguage=$definitionLanguage, createdAt=$createdAt)"
    }
}