package com.example.final_android_quizlet.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.LanguageAdapter
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import org.w3c.dom.Text
import java.util.Locale

class ChooseLanguageForTerm : AppCompatActivity() {

    // Service
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()

    // View
    private lateinit var tvTitle: TextView
    private lateinit var imgBack: ImageView
    private lateinit var etSearch: SearchView
    private lateinit var recyclerView: RecyclerView

    // Intent Request
    private val INTENT_SELECT_LANGUAGE_TERM = 1
    private val INTENT_SELECT_LANGUAGE_DEFINITION = 2

    // Hard data
    private var createFor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_language_for_term)

        // Get intent
        if (intent.getStringExtra("createFor") == null) {
            Toast.makeText(this, "Oops something wrong, try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        createFor = intent.getStringExtra("createFor")!!.toInt()

        // Get View
        tvTitle = findViewById(R.id.imgTitle_ChooseLangTerm)
        imgBack = findViewById(R.id.imgBack_ChooseLangTerm)
        etSearch = findViewById(R.id.searchView_ChooseLangTerm)
        recyclerView = findViewById(R.id.listView_ChooseLangTerm)

        if(createFor == INTENT_SELECT_LANGUAGE_TERM){
            tvTitle.text = "Ngôn ngữ của thuật ngữ"
        }
        // Locale
        val areas = Locale.getAvailableLocales()
        val items = mutableListOf<Locale>()
        items.addAll(areas)
        // Adapter
        val adapter = LanguageAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener {
            val intent = Intent()
            intent.putExtra("language", it)
            setResult(createFor, intent)
            finish()
            actionTransition.rollBackTransition()
        }

        etSearch.setOnCloseListener {
            items.clear()
            items.addAll(areas)
            adapter.notifyDataSetChanged()
            false
        }

        etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    items.clear()
                    items.addAll(areas.filter { it.displayName.contains(newText, ignoreCase = true) })
                    adapter.notifyDataSetChanged()
                    return true
                }
                return false
            }
        })
    }
}