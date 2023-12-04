package com.example.final_android_quizlet.adapter.data

import com.example.final_android_quizlet.models.MultipleChoice
import com.example.final_android_quizlet.models.QuizWrite
import com.google.firebase.firestore.auth.User

data class RankingItem(
    val user: User,
    val multipleChoice: MultipleChoice?,
    val quizWrite: QuizWrite?,
    val position: Int
) {}