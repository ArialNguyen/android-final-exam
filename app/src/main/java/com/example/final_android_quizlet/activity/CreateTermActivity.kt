package com.example.final_android_quizlet.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.TermAdapter
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.topic.TopicService
import com.github.ybq.android.spinkit.SpinKitView

class CreateTermActivity : AppCompatActivity() {
    private val termList = ArrayList<Term>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TermAdapter

    private lateinit var layoutDescription: ConstraintLayout
    private lateinit var etDescription: EditText
    private lateinit var etTitle: EditText
    private lateinit var btnBack: ImageView
    private lateinit var imgFinish: ImageView
    private lateinit var loaderFull: SpinKitView

    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_hocphan)

        layoutDescription = findViewById(R.id.expandDescriptionLayout)
        etDescription = findViewById(R.id.etDescription_createTopic)
        etTitle = findViewById(R.id.etTitle_createTopic)
        btnBack = findViewById(R.id.backButton)
        imgFinish = findViewById(R.id.btnFinish_createTopic)
        loaderFull = findViewById(R.id.spin_kit)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.algorithmRecyclerView)
        adapter = TermAdapter(termList)
        recyclerView.adapter = adapter

        val addAlgorithmButton = findViewById<ImageView>(R.id.addAlgorithmButton)
        termList.add(Term("", ""))
        termList.add(Term("", ""))
        adapter.notifyDataSetChanged()

        addAlgorithmButton.setOnClickListener {
            val newItem = Term("", "")
            termList.add(newItem)
            adapter.notifyItemInserted(termList.size - 1)
        }


        layoutDescription.setOnClickListener {
            etDescription.visibility = if (etDescription.isGone) View.VISIBLE else View.GONE
        }

        imgFinish.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            Log.i("TAG", "description: ${description == ""}")
            manageScopeApi.getResponseWithCallback(lifecycleScope,
                {(topicService::createTopic)(Topic(title, description, getUsefulTerm()))},
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
