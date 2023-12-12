package com.example.final_android_quizlet.common

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.google.firebase.auth.FirebaseAuth
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.util.*

class FileAction(private val ctx: Context) {
    private val UTF8_BOM = "\uFEFF"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val fieldsTopicImport: List<String> = listOf(
        "title", "description", "termLanguage", "definitionLanguage", ""
    )
    val fieldsTopicExport: List<String> = listOf(
        "title", "description", "termLanguage", "definitionLanguage", "terms"
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
                Locale.getAvailableLocales()[0].toLanguageTag()
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
                        try {
                            if (removeUTF8BOM(it1).isEmpty() && fieldValue == fieldsTopicImport[0]) { // check if fieldInFile == title
                                isValidRow = false
                                return@forEachIndexed
                            }
                            if (removeUTF8BOM(it1).isEmpty() && fieldValue == fieldsTopicImport[2]) { // check if fieldInFile == title
                                isValidRow = false
                                return@forEachIndexed
                            }
                            if (removeUTF8BOM(it1).isEmpty() && fieldValue == fieldsTopicImport[3]) { // check if fieldInFile == title
                                isValidRow = false
                                return@forEachIndexed
                            }
                            if(fieldValue == fieldsTopicImport[3] || fieldValue == fieldsTopicImport[2]){
                                val locale = Locale.getAvailableLocales().firstOrNull { it.toLanguageTag() == removeUTF8BOM(it1) }
                                if(locale == null){
                                    isValidRow = false
                                    return@forEachIndexed
                                }
                            }
                        }catch (e: IllegalArgumentException){
                            isValidRow = false
                            return@forEachIndexed
                        }
                        hashMapUser[fieldValue] = removeUTF8BOM(it1)
                    }
                }
                if (!isValidRow) {
                    listIndexRowFailure.add(i)
                } else {
                    hashMapUser["term"] = terms
                    val user = mapToTopic(hashMapUser)
                    topicsSuccess.add(user)
                }
            }
        }
        return topicsSuccess
    }

    fun writeTopicToCsv(fileName: String, topic: Topic): ResponseObject {
        val res = ResponseObject()
        val name = if (fileName.contains(".")) fileName.substring(0, fileName.indexOf(".")) + ".csv"
        else "$fileName.csv"
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name
            )
            if(file.exists()){
                throw Exception("File with name '${fileName}' already exist. Please type another name")
            }
            val printer = CSVPrinter(
                FileWriter(
                    file
                ), CSVFormat.DEFAULT
            )
            printer.printRecord(fieldsTopicExport[0], topic.title)
            printer.printRecord(fieldsTopicExport[1], topic.description)
            printer.printRecord(fieldsTopicExport[2], topic.termLanguage)
            printer.printRecord(fieldsTopicExport[3], topic.definitionLanguage)
            printer.printRecord(fieldsTopicExport[4], *topic.terms.map { "${it.term}, ${it.definition}" }.toTypedArray())
            printer.close()
            res.status = true
        } catch (e: Exception) {
            Log.i("TAG ERROR", "writeUsersToCsv: ${e.message}")
            res.status = false
            res.data = e.message.toString()
        }
        return res
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
                "termLanguage" -> topic.termLanguage = value as String
                "definitionLanguage" -> topic.definitionLanguage = value as String
                "term" -> {
                    Log.i("TAG", "mapToTopic: $value")
                    val terms = value as List<Term>
                    topic.terms = terms
                }
            }
        }
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