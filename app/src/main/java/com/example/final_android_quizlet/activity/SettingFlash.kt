package com.example.final_android_quizlet.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.models.EAnswer
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.models.OptionExamData
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.webianks.library.scroll_choice.ScrollChoice
class SettingFlash: AppCompatActivity() {
    // Service
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()

    // View
    private lateinit var swAutoSpeak: SwitchCompat
    private lateinit var imgCancel: ImageView
    private lateinit var tvTopicName: TextView
    private lateinit var llNumberQues: LinearLayout
    private lateinit var swShowAns: SwitchCompat
    private lateinit var llLanguageAns: ConstraintLayout
    private lateinit var tvTotalQues: TextView
    private lateinit var btnDone: Button
    private lateinit var tvLangAnswer: TextView
    private lateinit var swShuffle: SwitchCompat
    // -> Dialog Scroll View
    private lateinit var tvDone: TextView
    private lateinit var scrollChoiceNbQues: ScrollChoice

    // Intent Request
    private val INTENT_SELECT_LANGUAGE_TERM = 1

    // Hard data
    private val listQues: MutableList<String> = mutableListOf()
    private lateinit var topicIntent: Topic
    private lateinit var examDes: String

    private val optionExamData: OptionExamData = OptionExamData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_flash)

        // Check Intent
        if (intent.getSerializableExtra("topic") == null || intent.getStringExtra("exam") == null) {
            finish()
            Toast.makeText(this, "Oops, something wrong. Try again!!!", Toast.LENGTH_LONG).show()
            actionTransition.rollBackTransition()
        }
        topicIntent = intent.getSerializableExtra("topic") as Topic
        examDes = intent.getStringExtra("exam") as String
        Log.i("TAG", "examDes: $examDes")
        // Get view
        imgCancel = findViewById(R.id.imgCancel_SettingFlash)
        tvTopicName = findViewById(R.id.tvTopicName_SettingFlash)
        llLanguageAns = findViewById(R.id.llLanguageAns_SettingFlash)
        swShuffle = findViewById(R.id.swShuffle_SettingFlash)
        btnDone = findViewById(R.id.btn_Done)
        tvLangAnswer = findViewById(R.id.tvLangAnswer_SettingFlash)

        tvTopicName.text = topicIntent.title

        btnDone.setOnClickListener {
            // Thêm logic set giá trị cho optionExamData trước khi chuyển activity
            optionExamData.shuffle = swShuffle.isChecked // Ví dụ, set giá trị từ SwitchCompat swShuffle
            // Chuyển qua MainQuizActivity
            val intent = Intent(this, MainQuizActivity::class.java)
            intent.putExtra("data", optionExamData)
            intent.putExtra("classDestination", examDes)
            intent.putExtra("topicId", topicIntent.uid)
            startActivity(intent)
            finish()
            actionTransition.moveNextTransition()
        }

        swShuffle.setOnCheckedChangeListener { compoundButton, b ->
            Log.i("TAG", "IsChecked: $b")
            optionExamData.shuffle = b
        }

        llLanguageAns.setOnClickListener {
            actionOnLanguageAns()
        }

        imgCancel.setOnClickListener {
            finish()
            actionTransition.rollBackTransition()
        }

    }

    fun actionOnLanguageAns(){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Status")
        val statusArray = arrayOf(
            EAnswer.TERM.name, EAnswer.DEFINITION.name
        )
        var selectedStatus = 0
        val checkedItem = statusArray.indexOfFirst {
            optionExamData.answer.name == it
        }
        builder.setSingleChoiceItems(statusArray, checkedItem) { dialog, i ->
            selectedStatus = i
        }

        builder.setPositiveButton("OK") { dialog, i ->
            optionExamData.answer = EAnswer.valueOf(statusArray[selectedStatus])
            tvLangAnswer.text = if (optionExamData.answer.name == EAnswer.TERM.name) "Thuật ngữ" else "Định nghĩa"
        }

        builder.setNegativeButton("Cancel") { dialog, i ->
            dialog.dismiss()
        }

        builder.show()
    }
}