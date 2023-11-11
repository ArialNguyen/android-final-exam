package com.example.final_android_quizlet.common

enum class MyFBQueryMethod {
    IN, EQUAL
}

class MyFBQuery(public val field: String, public var value: Any, public val mode: MyFBQueryMethod) {
    override fun toString(): String {
        return "MyFBQuery(field='$field', value=$value, mode=$mode)"
    }
}