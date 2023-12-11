package com.example.final_android_quizlet.models

import com.example.final_android_quizlet.models.Enum.ETermList
import java.io.Serializable

class FlashCard(
    var uid: String,
    var termsLearning: MutableList<Term>,
    var termsKnew: MutableList<Term>,
    var currentTermId: String,
    var topicId: String,
    var userId: String,
    var eTermList: ETermList
) : Serializable {
    constructor() : this(
        "", mutableListOf(), mutableListOf(), "", "", "", ETermList.NORMAL_TERMS
    )
    constructor(flashCard: FlashCard) : this() {
        uid = flashCard.uid
        termsLearning = flashCard.termsLearning
        termsKnew = flashCard.termsKnew
        currentTermId = flashCard.currentTermId
        topicId = flashCard.topicId
        userId = flashCard.userId
        eTermList = flashCard.eTermList
    }

    override fun toString(): String {
        return "FlashCard(uid='$uid', termsLearning=$termsLearning, termsKnew=$termsKnew, currentTermId='$currentTermId', topicId='$topicId', userId='$userId')"
    }


}