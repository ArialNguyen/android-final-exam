package com.example.final_android_quizlet


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.activity.*
import com.example.final_android_quizlet.adapter.TopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionDialog
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.EOrientationRecyclerView
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.fragments.dialog.DialogLoading
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    // Service
    private val authService: AuthService = AuthService()
    private val topicService: TopicService = TopicService()
    private val actionTransition: ActionTransition = ActionTransition(this)

    // Dialog
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)
    private val dialogLoading: DialogLoading = DialogLoading(this)

    // View
    private lateinit var etSearchTopic: EditText
    private lateinit var tvName: TextView
    private lateinit var tvViewAllTopic: TextView
    private lateinit var plusIcon: ImageView
    private lateinit var profileButton: ImageView
    private lateinit var libraryButton: BottomNavigationItemView
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
        } else {

            // Get View
            etSearchTopic = findViewById(R.id.etSearchTopic_main)
            topicRV = findViewById(R.id.topicRV_Main)
            tvViewAllTopic = findViewById(R.id.tvViewAllTopic_main)
            plusIcon = findViewById(R.id.imageView5)
            tvName = findViewById(R.id.txName_main)
            profileButton = findViewById(R.id.imageView7)
            libraryButton = findViewById(R.id.imageView6)

            // Adapter
            topicAdapter = TopicAdapter(EOrientationRecyclerView.HORIZONTAL, topicsItem)
            topicAdapter.setOnItemClickListener {
                val intent = Intent(this, DetailTopic::class.java)
                intent.putExtra("topicId", it.topic.uid)
                startActivity(intent)
                actionTransition.moveNextTransition()
            }
            topicRV.adapter = topicAdapter

            // Handle event
            etSearchTopic.setOnKeyListener { view, i, keyEvent ->
                if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    val text = etSearchTopic.text.toString()
                    if (text.isNotEmpty()) {
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

            // Load data
            val session = Session.getInstance(this@MainActivity)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if(session.user == null){
                        startActivity(Intent(this@MainActivity, Login::class.java))
                    }else{
                        val user = session.user
                        runOnUiThread {
                            tvName.text = user!!.name
                        }
                        // Fetch Topics

                        if (session.topicsOfUser == null) {
                            dialogLoading.showDialog("Loading...")

                            val fetchTopics = topicService.getTopicsByUserId(user!!.uid)
                            if (fetchTopics.status) {
                                session.topicsOfUser = fetchTopics.topics!!.toMutableList()
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this@MainActivity, fetchTopics.data.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                            runOnUiThread {
                                dialogLoading.hideDialog()
                            }
                        }
                        if (session.topicsOfUser!!.isNotEmpty()) {
                            topicsItem.addAll(session.topicsOfUser!!.map { LibraryTopicAdapterItem(it, user) })
                            runOnUiThread {
                                topicAdapter.notifyDataSetChanged()
                            }
                        }
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
        Log.i("TAG", "onResume: ${Session.getInstance(this).topicsOfUserSaved}")
        Log.i("TAG", "onResume: ${Session.getInstance(this).foldersOfUser}")

    }
}