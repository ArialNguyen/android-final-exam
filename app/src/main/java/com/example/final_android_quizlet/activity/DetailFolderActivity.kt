package com.example.final_android_quizlet.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.LibraryTopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.*
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.example.final_android_quizlet.service.TopicService
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailFolderActivity : AppCompatActivity() {

    private var imgMenuFolder: ImageView? = null
    private var imgBack: ImageView? = null

    private var tvFolderName: TextView? = null
    private var tvTotalTerm: TextView? = null
    private var tvUserName: TextView? = null
    private var imgAvatar: CircleImageView? = null

    // Service
    private val folderService: FolderService = FolderService()
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)

    // Adapter
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()

    // Data for intent
    private var folder: Folder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_folder)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
        }

        // Get intent
        folder = intent.getSerializableExtra("folder") as Folder
        Log.i("TAG", "Receivie Folder: $folder")

        tvFolderName = findViewById(R.id.tvFolderName)
        tvTotalTerm = findViewById(R.id.tvTotalTerm)
        tvUserName = findViewById(R.id.tvUserName)
        imgAvatar = findViewById(R.id.imgAvatar)

        imgMenuFolder = findViewById(R.id.imgMenuFolder_DetailFolderActivity)
        imgBack = findViewById(R.id.imgBack_DetailFolderActivity)

        // Load draw data
        tvFolderName!!.text = folder!!.name
        tvTotalTerm!!.text = "${folder!!.topics.size} học phần"
//        tvUserName!!.text = getSharedPreferences()

        // Handle Event Listener
        imgMenuFolder!!.setOnClickListener {
            showMenuDetailFolder()
        }

        imgBack!!.setOnClickListener {
            onBackPressed()
        }
        // Adapter
        val adapter = LibraryTopicAdapter(items)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_DetailFolder)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { item ->
            val intent = Intent(this, DetailTopic::class.java)
            intent.putExtra("topicId", item.topic.uid) // should be move to topic
            startActivity(intent)
        }

        // Handle CallBack
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val user = authService.getUserLogin().user!!
                runOnUiThread {
                    tvUserName!!.text = user.name
                    Picasso.get().load(user.avatar).into(imgAvatar)
                }
                val folderFetch = folderService.FolderForUserLogged().getFolderById(folder!!.uid)
                if(folderFetch.status){
                    if(folderFetch.folder!!.topics.isNotEmpty()){
                        val topicsId = folderFetch.folder!!.topics
                        val fetchTopics = topicService.TopicForUserLogged().getTopicsByQuerys(mutableListOf(MyFBQuery("uid", topicsId, MyFBQueryMethod.IN)))
                        items.addAll(fetchTopics.topics!!.map { LibraryTopicAdapterItem(it, user) })
                        runOnUiThread { adapter.notifyDataSetChanged() }
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@DetailFolderActivity, folderFetch.data.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showMenuDetailFolder() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_menu_detail_folder)

        val editDetailFolder: LinearLayout = dialog.findViewById(R.id.liEdit_DetailFolderActivity)
        val addTopicDetailFolder: LinearLayout = dialog.findViewById(R.id.liAddTopic_DetailFolderActivity)
        val removeDetailFolder: LinearLayout = dialog.findViewById(R.id.liRemove_DetailFolderActivity)
        val cancelDetailFolder: ImageView = dialog.findViewById(R.id.imgCancel_DetailFolderActivity)

        editDetailFolder.setOnClickListener {
            actionDialog.openCreateFolderDialog(null)
            dialog.dismiss()
        }

        addTopicDetailFolder.setOnClickListener {
            val intent = Intent(this, AddTopicFolderActivity::class.java)
            Log.i("TAG", "INTENT: $folder")
            intent.putExtra("folder", folder!!)
            startActivity(intent)
            dialog.dismiss()
        }

        removeDetailFolder.setOnClickListener {
            dialog.dismiss()
        }

        cancelDetailFolder.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
