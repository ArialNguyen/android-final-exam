package com.example.final_android_quizlet.models

import java.io.Serializable

class FlashCard(
    var uid: String,
    var termsLearning: MutableList<Term>,
    var termsKnew: MutableList<Term>,
    var currentTermId: String,
    var topicId: String,
    var userId: String
) : Serializable {
    constructor() : this(
        "", mutableListOf(), mutableListOf(), "", "", ""
    )
    constructor(flashCard: FlashCard) : this() {
        uid = flashCard.uid
        termsLearning = flashCard.termsLearning
        termsKnew = flashCard.termsKnew
        currentTermId = flashCard.currentTermId
        topicId = flashCard.topicId
        userId = flashCard.userId
    }

    override fun toString(): String {
        return "FlashCard(uid='$uid', TermsLearning=$termsLearning, TermsKnew=$termsKnew, currentTermId=$currentTermId, topicId=$topicId, userId='$userId')"
    }


}