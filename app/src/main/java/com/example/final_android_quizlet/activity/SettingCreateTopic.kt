package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.models.EModeTopic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import java.util.Locale

class SettingCreateTopic : AppCompatActivity() {
    // Service
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()

    // View
    private lateinit var imgBackInSetting: ImageView
    private lateinit var tvSelectTermLang: TextView
    private lateinit var tvSelectDefinitionLang: TextView
    private lateinit var tvSelectMode: TextView

    // Intent Request
    private val INTENT_SELECT_LANGUAGE_TERM = 1
    private val INTENT_SELECT_LANGUAGE_DEFINITION = 2

    // Intent
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val language = result.data!!.getSerializableExtra("language") as Locale
        Log.i("TAG", "language: ${language}")
        if (INTENT_SELECT_LANGUAGE_TERM == result.resultCode) {
            termLang = language
            tvSelectTermLang.text = language.displayName
        } else if (INTENT_SELECT_LANGUAGE_DEFINITION == result.resultCode) {
            definitionLang = language
            tvSelectDefinitionLang.text = language.displayName
        }
    }

    // Hard data
    private var createFor: Int = 0
    private var termLang: Locale? = null
    private var definitionLang: Locale? = null
    private var accessMode: EModeTopic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_create_topic)

        // Get View
        imgBackInSetting = findViewById(R.id.imgBackInSetting_createTerm)
        tvSelectTermLang = findViewById(R.id.tvSelectTermLang_createTerm)
        tvSelectDefinitionLang = findViewById(R.id.tvSelectDefinitionLang_createTerm)
        tvSelectMode = findViewById(R.id.tvSelectMode_createTerm)

        // Load View
        if(intent.getSerializableExtra("langTerm") != null){
            termLang = intent.getSerializableExtra("langTerm") as Locale
            tvSelectTermLang.text = termLang!!.displayName
        }
        if(intent.getSerializableExtra("langDefinition") != null){
            definitionLang = intent.getSerializableExtra("langDefinition") as Locale
            tvSelectDefinitionLang.text = definitionLang!!.displayName
        }
        if( intent.getSerializableExtra("accessMode") != null ){
            accessMode = intent.getSerializableExtra("accessMode") as EModeTopic
            tvSelectMode.text = accessMode!!.name
        }

        imgBackInSetting.setOnClickListener {
            onBackPressed()
        }
        tvSelectTermLang.setOnClickListener {
            val intent = Intent(this, ChooseLanguageForTerm::class.java)
            intent.putExtra("createFor", INTENT_SELECT_LANGUAGE_TERM.toString())
            resultLauncher.launch(intent)
            actionTransition.moveNextTransition()
        }
        tvSelectDefinitionLang.setOnClickListener {
            val intent = Intent(this, ChooseLanguageForTerm::class.java)
            intent.putExtra("createFor", INTENT_SELECT_LANGUAGE_DEFINITION.toString())
            resultLauncher.launch(intent)
            actionTransition.moveNextTransition()
        }
        tvSelectTermLang.setOnClickListener {
            val intent = Intent(this, ChooseLanguageForTerm::class.java)
            intent.putExtra("createFor", INTENT_SELECT_LANGUAGE_TERM.toString())
            resultLauncher.launch(intent)
            actionTransition.moveNextTransition()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        if(termLang != null) intent.putExtra("termLang", termLang)
        if (definitionLang != null) intent.putExtra("definitionLang", definitionLang)
        if (accessMode != null) intent.putExtra("accessMode", accessMode)
        setResult(2, intent)
        finish()
        actionTransition.rollBackTransition()
    }
}