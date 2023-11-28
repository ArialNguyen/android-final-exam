package com.example.final_android_quizlet.common

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.google.firebase.auth.FirebaseAuth
import org.apache.commons.csv.CSVFormat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class FileAction(private val ctx: Context) {
    private val UTF8_BOM = "\uFEFF"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val fieldsTopicImport: List<String> = listOf(
        "title", "description", ""
    )
    val fieldsTopicExport: List<String> = listOf(
        "title", "description", "terms"
    )

    val fieldsFromFile: MutableList<Int> = mutableListOf()


    fun readFileCsvToTopics(uri: Uri): MutableList<Topic> {
        val inputStream = ctx.contentResolver.openInputStream(uri)!!
        val reader = BufferedReader(InputStreamReader(inputStream))
        val records = CSVFormat.DEFAULT.parse(reader).records
        val topicsSuccess = mutableListOf<Topic>()
        val listIndexRowFailure = mutableListOf<Int>()
        records.forEachIndexed { i, row ->
            if (i == 0) {
                row.forEachIndexed { i, it ->
                    val index = fieldsTopicImport.indexOf(removeUTF8BOM(it))
                    fieldsFromFile.add(index)
                }
                if (fieldsFromFile.size < fieldsTopicImport.size) {
                    (ctx as Activity).runOnUiThread {
                        Toast.makeText(
                            ctx,
                            "Wrong Format csv, make sure you enough column or correct name",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@forEachIndexed
                }
            } else {
                Log.i("TAG", "readFileCsvToTopics: $fieldsFromFile")
                val hashMapUser = hashMapOf<String, Any>()
                var isValidRow = true
                val terms = mutableListOf<Term>()
                row.forEachIndexed { index, it1 ->
                    val fieldValue = fieldsTopicImport[fieldsFromFile[index]]
                    if (fieldValue == "") {
                        if(it1 != ""){
                            val value = removeUTF8BOM(it1).split(",")
                            terms.add(Term(UUID.randomUUID().toString(), value[0].trim(), value[1].trim()))
                        }
                    } else {
                        if (removeUTF8BOM(it1).isEmpty() && fieldValue == fieldsTopicImport[0]) { // check if fieldInFile == title
                            isValidRow = false
                            return@forEachIndexed
                        }
                        hashMapUser[fieldValue] = removeUTF8BOM(it1)
                    }
                }
                Log.i("TAG", "isValidRow: $isValidRow")
                if (!isValidRow && (topicsSuccess.size > 1)) {
                    listIndexRowFailure.add(i)
                } else {
                    hashMapUser["term"] = terms
                    val user = mapToTopic(hashMapUser)
                    topicsSuccess.add(user)
                }
            }
        }
        Log.i("TAG", "topicsSuccess: $topicsSuccess")
        return topicsSuccess
    }

    fun mapToTopic(hashMap: HashMap<String, Any>): Topic {
        val topic = Topic()
        topic.userId = firebaseAuth.currentUser!!.uid
        for (entry in hashMap) {
            val key = entry.key
            val value = entry.value
            when (key) {
                "title" -> topic.title = value as String
                "description" -> topic.description = value as String
                "term" -> {
                    Log.i("TAG", "mapToTopic: $value")
                    val terms = value as List<Term>
                    topic.terms = terms
                }
            }
        }
        Log.i("TAG", "mapToTopic: $topic")
        return topic
    }


    private fun removeUTF8BOM(str: String): String {
        return if (str.startsWith(UTF8_BOM)) {
            str.substring(1)
        } else {
            str
        }
    }

}