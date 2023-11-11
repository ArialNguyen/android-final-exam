package com.example.final_android_quizlet.common

class AdapterAndItems(public var items: Any, public var secondItems: Any?, public var adapter: Any){
    override fun toString(): String {
        return "AdapterAndItems(items=$items, secondItems=$secondItems, adapter=$adapter)"
    }
}