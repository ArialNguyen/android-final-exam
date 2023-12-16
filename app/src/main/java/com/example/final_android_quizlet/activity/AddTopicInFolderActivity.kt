package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.AddToFolderAdapter
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionDialog
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.AdapterAndItems
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.fragments.dialog.DialogLoading
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTopicInFolderActivity : AppCompatActivity() {
    // Service
    private val folderService: FolderService = FolderService()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)
    private val dialogLoading: DialogLoading = DialogLoading(this)

    // View
    private var toAddInFolder: Toolbar? = null
    private var imgBack: ImageView? = null
    private var imgYes: ImageView? = null
    private var liCreateFolder: LinearLayout? = null
    private var txCreateFolder: TextView? = null
    private var folderRecyclerView: RecyclerView? = null

    // Adapter
    private val items: MutableList<Folder> = mutableListOf()
    private val itemsChosen: MutableList<Int> = mutableListOf()

    // hard data
    private lateinit var topicId: String
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topicinfolder_intopic)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
            actionTransition.moveNextTransition()
        }
        session = Session.getInstance(this)
        if (intent.getStringExtra("topicId") == null) {
            Toast.makeText(this@AddTopicInFolderActivity, "Some thing error! Please try again", Toast.LENGTH_LONG)
                .show()
            finish()
            actionTransition.rollBackTransition()
        }
        topicId = intent.getStringExtra("topicId")!!

        toAddInFolder = findViewById(R.id.toAddInFolder_AddTopicInFolderActivity)
        imgBack = findViewById(R.id.imgBack_AddTopicInFolderActivity)
        imgYes = findViewById(R.id.imgYes_AddTopicInFolderActivity)
        liCreateFolder = findViewById(R.id.liCreateFolder_AddTopicInFolderActivity)
        txCreateFolder = findViewById(R.id.txCreateFolder_AddTopicInFolderActivity)
        folderRecyclerView = findViewById(R.id.folderRecyclerView)

        itemsChosen.addAll(MutableList(items.size) { index -> index })
        // Adapter
        val adapter = AddToFolderAdapter(items, itemsChosen, this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_AddTopicFolder)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Handle Event Click
        imgBack!!.setOnClickListener {
            onBackPressed()
        }

        imgYes!!.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dialogLoading.showDialog("Loading...")
                    val listChosenItem =
                        items.filterIndexed { index, folder -> itemsChosen.contains(index) }.map { it.uid }
                    val fetchFolder = folderService.FolderForUserLogged().addTopicToFolders(topicId, listChosenItem)
                    if(fetchFolder.status){
                        // Handle Session Folder
                        if(session.foldersOfUser != null){
                            val folders = session.foldersOfUser!!
                            itemsChosen.forEach { i ->
                                if (!folders[i].topics.contains(topicId)){
                                    folders[i].topics.add(topicId)
                                }
                            }
                            session.foldersOfUser = folders
                        }
                        runOnUiThread { Toast.makeText(this@AddTopicInFolderActivity, "Add to Folder successfully", Toast.LENGTH_LONG).show() }
                    }else{
                        runOnUiThread { Toast.makeText(this@AddTopicInFolderActivity, fetchFolder.data.toString(), Toast.LENGTH_LONG).show() }
                    }
                    dialogLoading.hideDialog()
                    finish()
                    actionTransition.rollBackTransition()
                }
            }
        }
        txCreateFolder!!.setOnClickListener {
            dialogLoading.showDialog("Creating...")
            actionDialog.openCreateFolderDialog(AdapterAndItems(listOf(items, itemsChosen), null, adapter))
            dialogLoading.hideDialog()
        }

        // Handle CallBack
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (session.foldersOfUser == null) {
                    dialogLoading.showDialog("Loading...")
                    val fetchFolders = folderService.FolderForUserLogged().getFolders()
                    if (fetchFolders.status) {
                        session.foldersOfUser = fetchFolders.folders!!.toMutableList()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@AddTopicInFolderActivity,
                                fetchFolders.data.toString(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    dialogLoading.hideDialog()
                }
                val folders = if (session.foldersOfUser == null) mutableListOf() else session.foldersOfUser!!
                if(folders.isNotEmpty()){
                    itemsChosen.addAll(folders.mapIndexed { index, folder ->
                        if (folder.topics.contains(topicId)) index else -1
                    }.filter { it != -1 })
                    items.addAll(folders)
                    runOnUiThread { adapter.notifyDataSetChanged() }
                }
            }
        }

    }
}