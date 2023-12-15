package com.example.final_android_quizlet.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.AddToFolderAdapter
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.TopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.*
import com.example.final_android_quizlet.fragments.dialog.DialogFolder
import com.example.final_android_quizlet.fragments.dialog.DialogLoading
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.example.final_android_quizlet.service.TopicService
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class DetailFolderActivity : AppCompatActivity() {

    private lateinit var imgMenuFolder: ImageView
    private lateinit var imgBack: ImageView

    private lateinit var tvFolderName: TextView
    private lateinit var tvTotalTerm: TextView
    private lateinit var tvUserName: TextView
    private lateinit var imgAvatar: CircleImageView

    // Service
    private val dialogLoading: DialogLoading = DialogLoading(this)
    private val folderService: FolderService = FolderService()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()

    // Adapter
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()

    // Data for intent
    private lateinit var folder: Folder

    private lateinit var adapter: TopicAdapter

    private lateinit var session: Session

    private val DETAIL_FOLDER_ADD_TOPIC = 1

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == DETAIL_FOLDER_ADD_TOPIC) {
            // Handle add topic
            val topics = result.data!!.getSerializableExtra("topics") as MutableList<Topic>
            tvTotalTerm.text = "${folder.topics.size + topics.size} học phần"
            val positionStart = items.size
            items.addAll(topics.map { LibraryTopicAdapterItem(it, session.user!!) })
            adapter.notifyItemRangeInserted(positionStart, items.size)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_folder)
        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
        }

        // Get intent
        folder = intent.getSerializableExtra("folder") as Folder
        session = Session.getInstance(this)
        tvFolderName = findViewById(R.id.tvFolderName)
        tvTotalTerm = findViewById(R.id.tvTotalTerm)
        tvUserName = findViewById(R.id.tvUserName)
        imgAvatar = findViewById(R.id.imgAvatar)

        imgMenuFolder = findViewById(R.id.imgMenuFolder_DetailFolderActivity)
        imgBack = findViewById(R.id.imgBack_DetailFolderActivity)

        // Load draw data
        tvFolderName.text = folder.name
        tvTotalTerm.text = "${folder.topics.size} học phần"
//        tvUserName!!.text = getSharedPreferences()

        // Handle Event Listener
        imgMenuFolder.setOnClickListener {
            showMenuDetailFolder()
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }
        // Adapter
        adapter = TopicAdapter(EOrientationRecyclerView.VERTICAL, items)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_DetailFolder)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { item, position ->
            val intent = Intent(this, DetailTopic::class.java)
            intent.putExtra("topic", item.topic) // should be move to topic
            startActivity(intent)
        }

        adapter.setOnItemLongClickListener { item, position ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val deleteTopicInFolder = folderService.FolderForUserLogged().removeTopicFromFolder(folder.uid, item.topic.uid)
                    runOnUiThread {
                        if(deleteTopicInFolder.status){
                            val foldersSession = session.foldersOfUser!!
                            val folder = foldersSession.first { it.uid == folder.uid }
                            folder.topics.removeAt(folder.topics.indexOfFirst { it == item.topic.uid })
                            session.foldersOfUser = foldersSession

                            tvTotalTerm.text = "${items.size} học phần"
                            Toast.makeText(this@DetailFolderActivity, "Deleted", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@DetailFolderActivity, deleteTopicInFolder.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        val session = Session.getInstance(this)
        // Handle CallBack
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val user = session.user
                dialogLoading.showDialog("Loading...")
                user?.let {
                    runOnUiThread {
                        tvUserName.text = it.name
                        Picasso.get().load(it.avatar).into(imgAvatar)
                    }
                }
                val folderFetch = folderService.FolderForUserLogged().getFolderById(folder.uid)
//                if (folderFetch.status) {
//                    if (folderFetch.folder!!.topics.isNotEmpty()) {
//                        val topicsId = folderFetch.folder!!.topics
//                        val fetchTopics = topicService.TopicForUserLogged()
//                            .getTopicsByQuerys(mutableListOf(MyFBQuery("uid", topicsId, MyFBQueryMethod.IN)))
//                        items.addAll(fetchTopics.topics!!.map { LibraryTopicAdapterItem(it, user) })
//                        runOnUiThread { adapter.notifyDataSetChanged() }
//                    }
//                } else {
//                    runOnUiThread {
//                        Toast.makeText(this@DetailFolderActivity, folderFetch.data.toString(), Toast.LENGTH_LONG).show()
//                    }
//                }
                val topicsId = folderFetch.folder!!.topics
                val topics = session.topicsOfUser!!.filter { topicsId.contains(it.uid) }.toMutableList()
                if(session.topicsOfUserSaved != null){
                    topics.addAll(session.topicsOfUserSaved!!.filter { topicsId.contains(it.uid) })
                }
                items.addAll(topics.map { LibraryTopicAdapterItem(it, user!!) })
                runOnUiThread {
                    tvTotalTerm.text = "${topics.size} học phần"
                    dialogLoading.hideDialog()
                    adapter.notifyDataSetChanged()
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
            val folder = DialogFolder(this, object : DialogClickedEvent {
                override fun setSuccessButton(folderName: String, des: String) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val update = folderService.FolderForUserLogged().updateBaseInfo(folder.uid, folderName, des)
                            runOnUiThread {
                                if(update.status){
                                    tvFolderName.text = folderName
                                    // Handle Folder Session
                                    val foldersSession =session.foldersOfUser!!
                                    val idx = foldersSession.indexOfFirst { it.uid == folder.uid }
                                    foldersSession[idx].name = folderName
                                    foldersSession[idx].description = des
                                    session.foldersOfUser = foldersSession

                                    Toast.makeText(this@DetailFolderActivity, "Updated", Toast.LENGTH_LONG).show()
                                }else{
                                    Toast.makeText(this@DetailFolderActivity, update.data.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }, this.folder.name, this.folder.description)
            folder.show(supportFragmentManager, "Folder Dialog")
            dialog.dismiss()
        }

        addTopicDetailFolder.setOnClickListener {
            val intent = Intent(this, AddTopicFolderActivity::class.java)
            intent.putExtra("folder", folder)
            resultLauncher.launch(intent)
            dialog.dismiss()
        }

        removeDetailFolder.setOnClickListener {
            removeDetailFolder()
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

    private fun removeDetailFolder() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this folder?")

        builder.setPositiveButton("Yes") { dialog, which ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val deleteFolderResult = folderService.deleteFolder(folder.uid)
                    runOnUiThread {
                        if (deleteFolderResult.status) {
                            val foldersSession = session.foldersOfUser!!
                            foldersSession.removeAt(foldersSession.indexOfFirst { it.uid == folder.uid })
                            session.foldersOfUser = foldersSession

                            val intentBack = Intent()
                            Toast.makeText(this@DetailFolderActivity, "Folder deleted successfully", Toast.LENGTH_LONG)
                                .show()
                            intentBack.putExtra("folderId", folder.uid)
                            setResult(2, intentBack)
                            finish()
                            actionTransition.rollBackTransition()
                        } else {
                            Toast.makeText(this@DetailFolderActivity, "Failed to delete folder", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

//    override fun onResume() {
//
//    }
}
