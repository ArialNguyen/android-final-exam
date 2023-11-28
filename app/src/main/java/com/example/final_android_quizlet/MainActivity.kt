package com.example.final_android_quizlet


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.activity.*
import com.example.final_android_quizlet.adapter.TopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionDialog
import com.example.final_android_quizlet.common.EOrientationRecyclerView
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.example.final_android_quizlet.service.TopicService
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    // Service
    private val authService: AuthService = AuthService()
    private val topicService: TopicService = TopicService()
    private val folderService: FolderService = FolderService()
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)

    // View
    private lateinit var etSearchTopic: EditText
    private lateinit var tvName: TextView
    private lateinit var tvViewAllTopic: TextView
    private lateinit var plusIcon: ImageView
    private lateinit var profileButton: BottomNavigationItemView
    private lateinit var libraryButton: BottomNavigationItemView
    private lateinit var rankingButton: BottomNavigationItemView
    private lateinit var topicRV: RecyclerView

    // Adapter
    // -> Topic
    private val topicsItem: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private lateinit var topicAdapter: TopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java).putExtra("checkLogin", this@MainActivity::class.java.name))
        }

        // Get View
        etSearchTopic = findViewById(R.id.etSearchTopic_main)
        topicRV = findViewById(R.id.topicRV_Main)
        tvViewAllTopic = findViewById(R.id.tvViewAllTopic_main)
        plusIcon = findViewById(R.id.imageView5)
        tvName = findViewById(R.id.txName_main)
        profileButton = findViewById(R.id.imageView7)
        libraryButton = findViewById(R.id.imageView6)
        rankingButton = findViewById(R.id.imgRanking_Main)

        // Adapter
        topicAdapter = TopicAdapter(EOrientationRecyclerView.HORIZONTAL, topicsItem)
        topicRV.adapter = topicAdapter

        // Handle event
        etSearchTopic.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                val text = etSearchTopic.text.toString()
                if(text.isNotEmpty()){
                    val intent = Intent(this, SearchCommunity::class.java)
                    intent.putExtra("keyword", text)
                    startActivity(intent)
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
        tvViewAllTopic.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))

        }
        plusIcon.setOnClickListener {
            showBottomDialog()
        }
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        libraryButton.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }
        rankingButton.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }
        // Load data

        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val user = authService.getUserLogin().user!!
                runOnUiThread {
                    tvName.text = user.name
                }
                // Fetch Topics
                val fetchTopics = topicService.getTopicsByUserId(user.uid)
                if(fetchTopics.status){
                    topicsItem.addAll(fetchTopics.topics!!.map { LibraryTopicAdapterItem(it, user) })
                    runOnUiThread {
                        topicAdapter.notifyDataSetChanged()
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, fetchTopics.data.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_bottom_sheet)

        val createHocPhan: LinearLayout = dialog.findViewById(R.id.create_hoc_phan)
        val createFolder: LinearLayout = dialog.findViewById(R.id.create_folder)
        val createClass: LinearLayout = dialog.findViewById(R.id.create_class)
        val cancelButton: ImageView = dialog.findViewById(R.id.cancelButton)

        createHocPhan.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, CreateTermActivity::class.java)
            startActivity(intent)
        }

        createFolder.setOnClickListener {
            actionDialog.openCreateFolderDialog(null)
            dialog.dismiss()
        }

        createClass.setOnClickListener {
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onResume() {
        super.onResume()
        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java).putExtra("checkLogin", this@MainActivity::class.java.name))
        }
    }
}