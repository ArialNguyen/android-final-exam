package com.example.final_android_quizlet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.AddTopicToFolderAdapter
import com.example.final_android_quizlet.adapter.AddTopicToFolderApdater_CreatedAndLearned
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionDialog
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.fragments.FragmentAddTopic_CreateFolder
import com.example.final_android_quizlet.fragments.FragmentAddTopic_LearnFolder
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTopicFolderActivity : AppCompatActivity() {
    // Service
    private val authService: AuthService = AuthService()
    private val folderService: FolderService = FolderService()
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)
    private val actionTransition: ActionTransition = ActionTransition(this)

    // View
    private var imgYes: ImageView? = null
    private lateinit var tab: TabLayout
    private lateinit var viewPager: ViewPager2
    private var optionsCurrentTab: Int = 0

    // Adapter
    private lateinit var addTopicToFolderAdapter: AddTopicToFolderAdapter

    // data receive from intent
    public var folder: Folder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtopic_folder_infolder)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
        }
        if (intent.getSerializableExtra("folder") == null) {
            Toast.makeText(this, "Something wrong, please try again!!!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        // Catch date from intent
        folder = intent.getSerializableExtra("folder") as Folder

        // Load View
        imgYes = findViewById(R.id.imgYes_AddTopic_FolderActivity)
        tab = findViewById(R.id.tabAddTopic_AddTopic_FolderActivity)
        viewPager = findViewById(R.id.viewPager_AddTopic_FolderActivity)
        val imgBack = findViewById<ImageView>(R.id.imgBack_AddTopic_FolderActivity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_AddTopic_FolderActivity)
        setSupportActionBar(toolbar)

        // Handle onclick
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgYes!!.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (optionsCurrentTab == 0) { // Folder Created
                        val items =
                            addTopicToFolderAdapter.getAdapter(optionsCurrentTab).items as MutableList<LibraryTopicAdapterItem>
                        val itemsChosen =
                            addTopicToFolderAdapter.getAdapter(optionsCurrentTab).secondItems as MutableList<Int>
                        val adapter =
                            addTopicToFolderAdapter.getAdapter(optionsCurrentTab).adapter as AddTopicToFolderApdater_CreatedAndLearned
                        val chosenItems =
                            items.filterIndexed { index, libraryTopicAdapterItem -> itemsChosen.contains(index) }
                                .map { it.topic }
                        val addTopics = folderService.FolderForUserLogged()
                            .addTopicsToFolder(folder!!.uid, chosenItems.toMutableList())
                        if (addTopics.status) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@AddTopicFolderActivity,
                                    "Add topics successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            finish()
                            actionTransition.rollBackTransition()
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@AddTopicFolderActivity,
                                    addTopics.data.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
        val view = window.decorView.rootView
        // Adapter
        addTopicToFolderAdapter = AddTopicToFolderAdapter(this)

        addTopicToFolderAdapter.addFragment(FragmentAddTopic_CreateFolder(object :
            GetBackAdapterFromViewPager {
            override fun onResult(
                view: View,
                items: MutableList<LibraryTopicAdapterItem>,
                itemsChosen: MutableList<Int>,
                adapter: AddTopicToFolderApdater_CreatedAndLearned
            ) {
                addTopicToFolderAdapter.addAdapterForChild(items, itemsChosen, adapter)
            }
        }), "Đã tạo")

        addTopicToFolderAdapter.addFragment(FragmentAddTopic_LearnFolder(object :
            GetBackAdapterFromViewPager {
//            override fun onResult(items: MutableList<LibraryTopicAdapterItem>, adapter: LibraryTopicAdapter) {
//                addTopicToFolderAdapter.addAdapterForChild(items, adapter)
//            }
        }), "Đã học")

        viewPager.adapter = addTopicToFolderAdapter

        TabLayoutMediator(tab, viewPager) { tab, position ->
            tab.text = addTopicToFolderAdapter.getTabTitle(position)
            tab.view.setOnClickListener {
                optionsCurrentTab = position
            }
        }.attach()


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}