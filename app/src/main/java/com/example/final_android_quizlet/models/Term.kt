package com.example.final_android_quizlet.models

import java.io.Serializable
import java.util.Locale
import java.util.UUID

data class Term(
    var uid: String,
    var term: String,
    var definition: String,
): Serializable {
    constructor() : this(
        UUID.randomUUID().toString(),"", ""
    )
    override fun toString(): String {
        return "Term(uid='$uid', term='$term', definition='$definition')"
    }

    override fun equals(other: Any?): Boolean {
        return uid == (other as Term).uid
    }
}