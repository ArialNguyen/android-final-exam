package com.example.final_android_quizlet.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.TermAdapter
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.FileAction
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.models.ELearnTopicStatus
import com.example.final_android_quizlet.models.EModeTopic
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CreateTermActivity : AppCompatActivity() {
    // Service
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val fileAction: FileAction = FileAction(this)


    // Adapter
    private val termList = ArrayList<Term>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TermAdapter


    // View
    private lateinit var llImportFromCSV: ConstraintLayout
    private lateinit var layoutDescription: ConstraintLayout
    private lateinit var etDescription: EditText
    private lateinit var etTitle: EditText
    private lateinit var btnBack: ImageView
    private lateinit var imgFinish: ImageView
    private lateinit var loaderFull: SpinKitView


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
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_hocphan)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
        }
        val currentUser = authService.getCurrentUser()

        // Get View
        llImportFromCSV = findViewById(R.id.llImportFromCSV_createTopic)
        layoutDescription = findViewById(R.id.expandDescriptionLayout)
        etDescription = findViewById(R.id.etDescription_createTopic)
        etTitle = findViewById(R.id.etTitle_createTopic)
        btnBack = findViewById(R.id.backButton)
        imgFinish = findViewById(R.id.btnFinish_createTopic)
        loaderFull = findViewById(R.id.spin_kit)

        // Handle Event
        btnBack.setOnClickListener {
            onBackPressed()
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


        layoutDescription.setOnClickListener {
            etDescription.visibility = if (etDescription.isGone) View.VISIBLE else View.GONE
        }

        imgFinish.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            manageScopeApi.getResponseWithCallback(lifecycleScope,
                {(topicService::createTopic)(Topic(
                    UUID.randomUUID().toString(),
                    title, description, getUsefulTerm(),
                    currentUser.uid,
//                    mutableListOf(),
                    EModeTopic.PRIVATE, ELearnTopicStatus.NOT_LEARN
                ))},
                object : CallbackInterface{
                    override fun onBegin() {
                        loaderFull.visibility = View.VISIBLE
                    }

                    override fun onValidate(): Boolean {
                        if(title.isEmpty()){
                            Toast.makeText(this@CreateTermActivity, "Title must be required", Toast.LENGTH_LONG).show()
                            return false
                        }else if (termList.size < 2){
                            Toast.makeText(this@CreateTermActivity, "Must provide at least 2 Term", Toast.LENGTH_LONG).show()
                            return false
                        }
                        return true
                    }
                    override fun onCallback(res: ResponseObject) {
                        if(res.status){
                            if(intent.getStringExtra("className") != null && intent.getStringExtra("className")!!.isNotEmpty()){
                                Log.i("TAG", "COME TO create Term check intent")
                                val resIntent = Intent()
                                resIntent.putExtra("extra_topic", res.topic!!)
                                setResult(Activity.RESULT_OK, resIntent)
                            }
                            finish()
                            actionTransition.rollBackTransition()
                        }else{
                            Toast.makeText(this@CreateTermActivity, res.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFinally() {
                        loaderFull.visibility = View.GONE
                    }
                })
        }

    }

    private fun getUsefulTerm(): ArrayList<Term>{
        return termList.filter {
            !(it.term.isEmpty() && it.definition.isEmpty())
        } as ArrayList<Term>
    }

}
