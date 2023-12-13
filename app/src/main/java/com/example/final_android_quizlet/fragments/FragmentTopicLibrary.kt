package com.example.final_android_quizlet.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.DetailTopic
import com.example.final_android_quizlet.adapter.TopicAdapter
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.*
import com.example.final_android_quizlet.fragments.dialog.DialogLoading
import com.example.final_android_quizlet.mapper.TopicMapper
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.example.final_android_quizlet.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentTopicLibrary(private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager?) : Fragment() {
    constructor(): this(null)
    // Service
    private val userService: UserService = UserService()
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private val itemsTemp: MutableList<LibraryTopicAdapterItem> = mutableListOf()

    private var itemsSaved: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private val itemsSavedTemp: MutableList<LibraryTopicAdapterItem> = mutableListOf()

    private val authService: AuthService = AuthService()
    private val topicService: TopicService = TopicService()
    private lateinit var adapter: TopicAdapter
    private lateinit var session: Session

    private var etSearchTopic: EditText? = null

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 2) {
            val topicId = result.data!!.getStringExtra("topicId")
            val idx = items.indexOfFirst { it.topic.uid == topicId }

            items.removeAt(idx)
            adapter.notifyItemRemoved(idx)
            // Handle Session
            val listTmp =  session.topicsOfUser!!
            val idx2 = listTmp.indexOfFirst { it.uid == topicId }

            listTmp.removeAt(idx2)
            session.topicsOfUser = listTmp
            Log.i("TAG", "session: ${session.topicsOfUser!!.size}")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__hoc_phan, container, false)
        val dialogLoading = DialogLoading(requireContext())

        if(!authService.isLogin()){
            startActivity(Intent(context, Login::class.java))
        }else{
            etSearchTopic = view.findViewById(R.id.etFilterTopic_library)
            // Topic Owner
            adapter = TopicAdapter(EOrientationRecyclerView.VERTICAL, items)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_library)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener { item ->
                val intent = Intent(context, DetailTopic::class.java)
                intent.putExtra("topicId", item.topic.uid)
                resultLauncher.launch(intent)
            }

            // Saved Topic
            val topicSavedAdapter = TopicAdapter(EOrientationRecyclerView.VERTICAL, itemsSaved)
            val recyclerViewSaved = view.findViewById<RecyclerView>(R.id.recyclerViewSaved_library)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerViewSaved.adapter = topicSavedAdapter
            topicSavedAdapter.setOnItemClickListener { item ->
                val intent = Intent(context, DetailTopic::class.java)
                intent.putExtra("topicId", item.topic.uid)
                startActivity(intent)
            }
            session = Session.getInstance(requireContext())
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    // Fetch topic owner
                    val user = session.user
                    val topics = session.topicsOfUser
                    if(topics!!.isNotEmpty()){
                        val list =  topics.map {
                            LibraryTopicAdapterItem(it, user)
                        }.toMutableList()
                        itemsTemp.addAll(list)
                        items.addAll(list)
                        (context as Activity).runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    }
                    // Fetch Topic Saved
                    if(user!!.topicSaved.isNotEmpty()){
                        dialogLoading.showDialog("Loading...")
                        if (session.topicsOfUserSaved == null){
                            val topicsSaved = topicService.getTopicsByQuerys(mutableListOf(
                                MyFBQuery("uid", user.topicSaved, MyFBQueryMethod.IN)
                            ))
                            session.topicsOfUserSaved = topicsSaved.topics!!.toMutableList()
                        }
                        val fetchUserInTopic = userService.getUsersInListUserId(session.topicsOfUserSaved!!.map { it.userId }.distinct())
                        if(fetchUserInTopic.status){
                            val list =  session.topicsOfUserSaved!!.map {
                                LibraryTopicAdapterItem(it, fetchUserInTopic.users!!.first { us -> us.uid == it.userId })
                            }.toMutableList()
                            itemsSavedTemp.addAll(list)
                            itemsSaved.addAll(list)
                            (context as Activity).runOnUiThread {
                                topicSavedAdapter.notifyDataSetChanged()
                            }
                        }
                        dialogLoading.hideDialog()
                    }

                }
            }

            etSearchTopic?.doOnTextChanged { text, start, before, count ->
                // Topic Owner
                items.clear()
                items.addAll(itemsTemp.filter { it.topic.title.contains(text!!, ignoreCase = true) }.toMutableList())
                adapter.notifyDataSetChanged()

                // Topic Saved
                itemsSaved.clear()
                itemsSaved.addAll(itemsSavedTemp.filter { it.topic.title.contains(text!!, ignoreCase = true) }.toMutableList())
                topicSavedAdapter.notifyDataSetChanged()
            }
            getBackAdapterFromViewPager?.onResult(view, items, adapter)
        }
        return view
    }
}