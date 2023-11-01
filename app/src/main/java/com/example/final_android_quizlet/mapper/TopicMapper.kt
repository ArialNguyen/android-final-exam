package com.example.final_android_quizlet.mapper

import android.util.Log
import com.example.final_android_quizlet.models.Topic
import org.modelmapper.ModelMapper

class TopicMapper {
    private val mapper = ModelMapper()

    fun convertToTopic(topic: Any): Topic{
        return mapper.map(topic, Topic::class.java)
    }
}