package com.example.final_android_quizlet.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.MainActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.TermAdapter
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.FileAction
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.fragments.dialog.DialogLoading
import com.example.final_android_quizlet.models.ELearnTopicStatus
import com.example.final_android_quizlet.models.EModeTopic
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.github.ybq.android.spinkit.SpinKitView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

class CreateTermActivity : AppCompatActivity() {
    // Service
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val fileAction: FileAction = FileAction(this)
    private val dialogLoading: DialogLoading = DialogLoading(this)

    // Adapter
    private val termList = ArrayList<Term>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TermAdapter


    // View
    private lateinit var llImportFromCSV: ConstraintLayout
    private lateinit var layoutDescription: ConstraintLayout
    private lateinit var etDescription: EditText
    private lateinit var etTitle: EditText
    private lateinit var settingButton: ImageView
    private lateinit var imgFinish: ImageView
    private lateinit var loaderFull: SpinKitView

    // Hard data
    private var termLang: Locale? = null
    private var definitionLang: Locale? = null
    private var accessMode: EModeTopic = EModeTopic.PRIVATE
    private lateinit var session: Session
    // Intent Request
    private val INTENT_SETTING = 2
    // Intent
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Updating...")
            progressDialog.setMessage("Please wait, we're setting your data...")
            progressDialog.show()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val topics = fileAction.readFileCsvToTopics(result.data!!.data!!)
                    val res = topicService.TopicForUserLogged().createTopics(topics)

                    this@CreateTermActivity.runOnUiThread {
                        if (res.status) {
                            // Update Topics Session
                            Log.i("TAG", "data : ${res.topics}")
                            val topicsSession = session.topicsOfUser!!
                            topicsSession.addAll(res.topics!!)
                            session.topicsOfUser = topicsSession
                            Log.i("TAG", "session size: ${session.topicsOfUser!!.size}")
                            Toast.makeText(this@CreateTermActivity, "Import Successfully", Toast.LENGTH_LONG).show()
                            finish()
                            actionTransition.rollBackTransition()
                        } else {
                            // Handle notify to user
                            Toast.makeText(this@CreateTermActivity, res.data.toString(), Toast.LENGTH_LONG).show()
                        }
                        progressDialog.dismiss()
                    }
                }
            }
        }else if (INTENT_SETTING == result.resultCode){
            val intent = result.data!!
            termLang = if (intent.getSerializableExtra("termLang") != null) intent.getSerializableExtra("termLang") as Locale else termLang
            definitionLang = if (intent.getSerializableExtra("definitionLang") != null) intent.getSerializableExtra("definitionLang") as Locale else definitionLang
            accessMode = if (intent.getSerializableExtra("accessMode") != null) intent.getSerializableExtra("accessMode") as EModeTopic else accessMode
            Log.i("TAG", "CreateTerm: $termLang & $definitionLang & $accessMode")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_hocphan)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
        }
        session = Session.getInstance(this)
        Locale.getAvailableLocales()[0].displayName
        val currentUser = authService.getCurrentUser()
        // Get View
        llImportFromCSV = findViewById(R.id.llImportFromCSV_createTopic)
        layoutDescription = findViewById(R.id.expandDescriptionLayout)
        etDescription = findViewById(R.id.etDescription_createTopic)
        etTitle = findViewById(R.id.etTitle_createTopic)
        settingButton = findViewById(R.id.settingTopic_createTopic)
        imgFinish = findViewById(R.id.btnFinish_createTopic)
        loaderFull = findViewById(R.id.spin_kit)
        // Handle Event
        settingButton.setOnClickListener {
            val intent = Intent(this, SettingCreateTopic::class.java)
            if(termLang != null) intent.putExtra("langTerm", termLang)
            if (definitionLang != null) intent.putExtra("langDefinition", definitionLang)
            if (accessMode != null) intent.putExtra("accessMode", accessMode)
            resultLauncher.launch(intent)
            actionTransition.moveNextTransition()
        }

        if (intent.getBooleanExtra("isEditAction", false)) {
            settingButton.visibility = View.GONE
        }

        llImportFromCSV.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("*/*")
            resultLauncher.launch(Intent.createChooser(intent, "Open Csv"))
        }

        recyclerView = findViewById(R.id.algorithmRecyclerView)
        adapter = TermAdapter(termList)
        recyclerView.adapter = adapter

        val addAlgorithmButton = findViewById<ImageView>(R.id.addAlgorithmButton)
        termList.add(Term(UUID.randomUUID().toString(),"", ""))
        termList.add(Term(UUID.randomUUID().toString(), "", ""))
        adapter.notifyDataSetChanged()


        addAlgorithmButton.setOnClickListener {
            val newItem = Term(UUID.randomUUID().toString(), "", "")
            termList.add(newItem)
            adapter.notifyItemInserted(termList.size - 1)
        }


        val receivedTopicSerializable = intent.getSerializableExtra("topicData")
        if (receivedTopicSerializable != null && receivedTopicSerializable is Topic) {
            val receivedTopic = receivedTopicSerializable as Topic
            if (receivedTopic.terms.isEmpty()) {
                receivedTopic.terms = arrayListOf()
            }

            etTitle.setText(receivedTopic.title)
            etDescription.setText(receivedTopic.description)

            termList.clear()
            termList.addAll(receivedTopic.terms)

            adapter = TermAdapter(termList)
            recyclerView.adapter = adapter
        }



        layoutDescription.setOnClickListener {
            etDescription.visibility = if (etDescription.isGone) View.VISIBLE else View.GONE
        }
        if (intent.getBooleanExtra("isEditAction", false)){
            updateTopicWithTerms()
        }else{
            imgFinish.setOnClickListener {
                val title = etTitle.text.toString()
                val description = etDescription.text.toString()
                loaderFull.visibility = View.VISIBLE
                if(title.isEmpty()){
                    Toast.makeText(this@CreateTermActivity, "Title must be required", Toast.LENGTH_LONG).show()
                }else if (getUsefulTerm().size < 2){
                    Toast.makeText(this@CreateTermActivity, "Must provide at least 2 Term", Toast.LENGTH_LONG).show()
                }else if (termLang == null || definitionLang == null){
                    Toast.makeText(this@CreateTermActivity, "Must provide Language For Term", Toast.LENGTH_LONG).show()
                }else{
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){
                            val topic = Topic(
                                UUID.randomUUID().toString(),
                                title, description, getUsefulTerm(),
                                mutableListOf(), currentUser.uid,
                                accessMode, ELearnTopicStatus.NOT_LEARN,
                                termLang!!.toLanguageTag(), definitionLang!!.toLanguageTag()
                            )
                            val res = topicService.createTopic(
                                topic
                            )
                            if(res.status){
                                // Update Session
                                val listTmp =  session.topicsOfUser!!
                                listTmp.add(topic)
                                session.topicsOfUser = listTmp

                                if(intent.getStringExtra("className") != null && intent.getStringExtra("className")!!.isNotEmpty()){
                                    Log.i("TAG", "COME TO create Term check intent")
                                    val resIntent = Intent()
                                    resIntent.putExtra("extra_topic", res.topic!!)
                                    setResult(1, resIntent)
                                }
                                runOnUiThread {
                                    Toast.makeText(this@CreateTermActivity, "Success!!", Toast.LENGTH_LONG).show()
                                }

                                finish()
                                actionTransition.rollBackTransition()
                            }else{
                                runOnUiThread {
                                    Toast.makeText(this@CreateTermActivity, res.data.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
                loaderFull.visibility = View.GONE
            }
        }

    }

    private fun updateTopicWithTerms() {
        imgFinish.setOnClickListener {

            val receivedTopic = intent.getSerializableExtra("topicData") as Topic
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            val usefulTerms = getUsefulTerm()

            val usefulTermId = usefulTerms.map { it.uid }

            val starList = receivedTopic.starList.filter { usefulTermId.contains(it.uid) }.toMutableList()

            val updatedTopic =
                receivedTopic.copy(title = title, description = description, terms = usefulTerms, starList = starList)

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dialogLoading.showDialog("Updating...")
                    val res = topicService.updateTopicWithTerms(updatedTopic)
                    if (res.status) {

                        // Update Session
                        val topicsSession =  session.topicsOfUser!!
                        topicsSession[topicsSession.indexOfFirst { it.uid == updatedTopic.uid }] = updatedTopic
                        session.topicsOfUser = topicsSession


                        runOnUiThread {
                            Toast.makeText(
                                this@CreateTermActivity,
                                "Topic updated successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                            setResult(1, Intent().putExtra("topic", updatedTopic))
                            finish()
                            actionTransition.rollBackTransition()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@CreateTermActivity,
                                res.data.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    dialogLoading.hideDialog()
                }
            }
        }
    }

    private fun getUsefulTerm(): ArrayList<Term>{
        return termList.filter {
            !(it.term.isEmpty() && it.definition.isEmpty())
        } as ArrayList<Term>
    }

}
