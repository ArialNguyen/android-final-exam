package com.example.final_android_quizlet.dao

import com.example.final_android_quizlet.models.*

class ResponseObject(
    public var status: Boolean = false,
    public var data: Any? = null,
    public var user: User? = null,
    public var topic: Topic? = null,
    public var topics: List<Topic>? = null,
    public var folder: Folder? = null,
    public var folders: List<Folder>? = null,
    public var quizWrite: QuizWrite? = null,
    public var flashCard: FlashCard? = null,
    public var flashCards: List<FlashCard>? = null,
    public var testChoice: MultipleChoice? = null,
    public var testChoices: List<MultipleChoice>? = null,

){
    override fun toString(): String {
        return "ResponseObject(status=$status, data=$data, user=$user, topic=$topic, topics=$topics, folder=$folder, folders=$folders, quizWrite=$quizWrite, testChoice=$testChoice, testChoices=$testChoices,)"
    }
}

fun ResponseObject.clone(): ResponseObject {
    val res = ResponseObject()
    res.data = this.data
    res.status = this.status
    res.user = this.user
    res.topic = this.topic
    res.topics = this.topics
    res.folder = this.folder
    res.folders = this.folders
    res.quizWrite = this.quizWrite
    res.flashCard = this.flashCard
    res.flashCards = this.flashCards
    res.testChoice = this.testChoice
    res.testChoices = this.testChoices
    return res
}