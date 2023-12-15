package com.example.final_android_quizlet.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.LibraryAdapter
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.TopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionDialog
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.fragments.FragmentFolderLibrary
import com.example.final_android_quizlet.fragments.FragmentTopicLibrary
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User
import com.example.final_android_quizlet.service.AuthService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch


class LibraryActivity : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)
    private val actionTransition: ActionTransition = ActionTransition(this)


    private lateinit var tabLibrary: TabLayout
    private lateinit var viewPager: ViewPager2
    private var optionsAddInMenu: Number = 0

    private lateinit var libraryAdapter: LibraryAdapter
    private lateinit var session: Session

    private val INTENT_ADD_TOPIC = 1
    private val INTENT_REMOVE_TOPIC = 2



    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == INTENT_ADD_TOPIC) {
            val topic = result.data!!.getSerializableExtra("extra_topic") as Topic
            Log.i("TAG", "BACk: $topic")
            (libraryAdapter.getAdapter(0).items as MutableList<LibraryTopicAdapterItem>)
                .add(LibraryTopicAdapterItem(topic, session.user!!))
            (libraryAdapter.getAdapter(0).adapter as TopicAdapter).notifyDataSetChanged()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
        }

        session = Session.getInstance(this)
        Log.i("TAG", "session: ${session.topicsOfUser!!.size}")

        tabLibrary = findViewById(R.id.tabLibrary)
        viewPager = findViewById(R.id.viewPager_library)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_library)
        setSupportActionBar(toolbar)

        libraryAdapter = LibraryAdapter(this)
        libraryAdapter.addFragment(FragmentTopicLibrary( object : GetBackAdapterFromViewPager{
            override fun onResult(view: View, items: MutableList<LibraryTopicAdapterItem>, adapter: TopicAdapter) {
                view.findViewById<TextInputLayout>(R.id.llFilterTopic_TopicFG).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.text_tien_trinh).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tvSaveTopic_library).visibility = View.VISIBLE
                view.findViewById<RecyclerView>(R.id.recyclerViewSaved_library).visibility = View.VISIBLE
                libraryAdapter.addAdapterForChild(items, adapter)
            }
        }), "Học Phần")
        libraryAdapter.addFragment(FragmentFolderLibrary( object : GetBackAdapterFromViewPager{
            override fun onResult(view: View, items: MutableList<LibraryFolderAdapterItem>, adapter: LibraryFolderAdapter) {
                libraryAdapter.addAdapterForChild(items, adapter)
            }
        }), "Thư Mục")
        viewPager.adapter = libraryAdapter

        TabLayoutMediator(tabLibrary, viewPager){ tab, position ->
            tab.text = libraryAdapter.getTabTitle(position)
            tab.view.setOnClickListener {
                optionsAddInMenu = position
            }
        }.attach() // Connect viewPager and Tab
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_library, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_Algorithm -> {
               if (optionsAddInMenu == 0){
                   val intent = Intent(this, CreateTermActivity::class.java)
                   intent.putExtra("className", this::class.java.name.toString())
                   resultLauncher.launch(intent)
               }else if(optionsAddInMenu == 1){
                   val adapterAndItems = libraryAdapter.getAdapter(1)
                   actionDialog.openCreateFolderDialog(adapterAndItems)
               }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finish()
        actionTransition.rollBackTransition()
    }

    override fun onResume() {
        super.onResume()

    }

}
