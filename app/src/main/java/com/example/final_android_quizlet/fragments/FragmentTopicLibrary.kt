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
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.example.final_android_quizlet.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentTopicLibrary(private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager?) : Fragment() {
    constructor() : this(null)

    // Service
    private val userService: UserService = UserService()
    private var items: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private val itemsTemp: MutableList<LibraryTopicAdapterItem> = mutableListOf()

    private var itemsSaved: MutableList<LibraryTopicAdapterItem> = mutableListOf()
    private val itemsSavedTemp: MutableList<LibraryTopicAdapterItem> = mutableListOf()

    private val authService: AuthService = AuthService()
    private val topicService: TopicService = TopicService()
    private lateinit var adapter: TopicAdapter
    private lateinit var topicSavedAdapter: TopicAdapter
    private lateinit var session: Session
    private var currentClickTopicIdx: Int = -1
    private var currentClickTopicSavedIdx: Int = -1


    private var etSearchTopic: EditText? = null

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 2) {
            val topicId = result.data!!.getStringExtra("topicId")

            val idx = items.indexOfFirst { it.topic.uid == topicId }
            itemsTemp.removeAt(itemsTemp.indexOfFirst { it.topic.uid == items[idx].topic.uid  })
            items.removeAt(idx)
            adapter.notifyItemRemoved(idx)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__hoc_phan, container, false)
        val dialogLoading = DialogLoading(requireContext())

        if (!authService.isLogin()) {
            startActivity(Intent(context, Login::class.java))
        } else {
            etSearchTopic = view.findViewById(R.id.etFilterTopic_library)
            // Topic Owner
            adapter = TopicAdapter(EOrientationRecyclerView.VERTICAL, items)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_library)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener { item, position ->
                val intent = Intent(context, DetailTopic::class.java)
                intent.putExtra("topic", item.topic)
                currentClickTopicIdx = position
                resultLauncher.launch(intent)
            }

            // Saved Topic
            topicSavedAdapter = TopicAdapter(EOrientationRecyclerView.VERTICAL, itemsSaved)
            val recyclerViewSaved = view.findViewById<RecyclerView>(R.id.recyclerViewSaved_library)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerViewSaved.adapter = topicSavedAdapter
            topicSavedAdapter.setOnItemClickListener { item, position ->
                val intent = Intent(context, DetailTopic::class.java)
                intent.putExtra("topic", item.topic)
                intent.putExtra("ownUser", false)
                currentClickTopicSavedIdx = position
                startActivity(intent)
            }
            session = Session.getInstance(requireContext())
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dialogLoading.showDialog("Loading...")
                    // Fetch TopicSaved in User
                    val fetchUser = authService.getUserLogin()
                    session.user = fetchUser.user

                    // Fetch topic owner
                    val user = session.user!!
                    if (session.topicsOfUser != null) {
                        val topics = session.topicsOfUser
                        if (topics!!.isNotEmpty()) {
                            val list = topics.map {
                                LibraryTopicAdapterItem(it, user)
                            }.toMutableList()
                            items.clear()
                            itemsTemp.clear()
                            itemsTemp.addAll(list)
                            items.addAll(list)
                            (context as Activity).runOnUiThread {
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                    // Fetch Topic Saved
                    if (user.topicSaved.isNotEmpty()) {

                        // Fetch Topics saved in User
                        val topicsSaved = topicService.getTopicsByQuerys(
                            mutableListOf(
                                MyFBQuery("uid", user.topicSaved, MyFBQueryMethod.IN)
                            )
                        )

                        session.topicsOfUserSaved = topicsSaved.topics!!.toMutableList()

                        val fetchUserInTopic =
                            userService.getUsersInListUserId(session.topicsOfUserSaved!!.map { it.userId }.distinct())
                        Log.i("TAG", "fetchUserInTopic: ${fetchUserInTopic.users}")
                        if (fetchUserInTopic.status) {
                            val list = session.topicsOfUserSaved!!.map {it2 ->
                                LibraryTopicAdapterItem(
                                    it2,
                                    fetchUserInTopic.users!!.find { us ->
                                        us.uid == it2.userId
                                    }!!)
                            }.toMutableList()
                            itemsSaved.clear()
                            itemsSavedTemp.clear()
                            itemsSavedTemp.addAll(list)
                            itemsSaved.addAll(list)
                            (context as Activity).runOnUiThread {
                                topicSavedAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    dialogLoading.hideDialog()
                }
            }

            etSearchTopic?.doOnTextChanged { text, start, before, count ->
                // Topic Owner
                items.clear()
                items.addAll(itemsTemp.filter { it.topic.title.contains(text!!, ignoreCase = true) }.toMutableList())
                adapter.notifyDataSetChanged()

                // Topic Saved
                itemsSaved.clear()
                itemsSaved.addAll(itemsSavedTemp.filter { it.topic.title.contains(text!!, ignoreCase = true) }
                    .toMutableList())
                topicSavedAdapter.notifyDataSetChanged()
            }
            getBackAdapterFromViewPager?.onResult(view, items, adapter)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        // Load Session Current Topic Chosen
        if (currentClickTopicIdx != -1 && session.topicsOfUser != null && currentClickTopicIdx < items.size) { // Handle ownTopic
            val topicSessionIdx =
                session.topicsOfUser!!.indexOfFirst { it.uid == items[currentClickTopicIdx].topic.uid }
            if(topicSessionIdx != -1){
                itemsTemp.first { it.topic.uid == items[currentClickTopicIdx].topic.uid }.topic =
                    session.topicsOfUser!![topicSessionIdx]

                items[currentClickTopicIdx].topic = session.topicsOfUser!![topicSessionIdx]

                adapter.notifyItemChanged(currentClickTopicIdx)
            }
            currentClickTopicIdx = -1
        }
        if (currentClickTopicSavedIdx != -1 && session.topicsOfUserSaved != null) {// Handle savedTopic -- this action for upgrade because user not allow to modify topic saved now
            // Update Session
            val topicSavedSessionIdx =
                session.topicsOfUserSaved!!.indexOfFirst { it.uid == itemsSaved[currentClickTopicSavedIdx].topic.uid }

            if (topicSavedSessionIdx == -1) { // Has been removed
                itemsSavedTemp.removeAt(itemsSavedTemp.indexOfFirst { it.topic.uid == itemsSaved[currentClickTopicSavedIdx].topic.uid })
                itemsSaved.removeAt(currentClickTopicSavedIdx)
                topicSavedAdapter.notifyItemRemoved(currentClickTopicSavedIdx)
            }else{
                itemsSavedTemp.first { it.topic.uid == itemsSaved[currentClickTopicSavedIdx].topic.uid }.topic =
                    session.topicsOfUserSaved!![topicSavedSessionIdx]

                itemsSaved[currentClickTopicSavedIdx].topic = session.topicsOfUserSaved!![topicSavedSessionIdx]

                topicSavedAdapter.notifyItemChanged(currentClickTopicSavedIdx)
            }

            currentClickTopicSavedIdx = -1
        }


        if (session.topicsOfUserSaved != null) {
            if (session.topicsOfUserSaved!!.size != itemsSaved.size) {
                Log.i("TAG", "onResume: LOAD FROM session.topicsOfUserSaved")
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val fetchUserInTopic =
                            userService.getUsersInListUserId(session.topicsOfUserSaved!!.map { it.userId }.distinct())
                        if (fetchUserInTopic.status) {
                            val list = session.topicsOfUserSaved!!.map {
                                LibraryTopicAdapterItem(
                                    it,
                                    fetchUserInTopic.users!!.first { us -> us.uid == it.userId })
                            }.toMutableList()
                            itemsSaved.clear()
                            itemsSavedTemp.clear()
                            itemsSavedTemp.addAll(list)
                            itemsSaved.addAll(list)
                            requireActivity().runOnUiThread {
                                topicSavedAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        } else if (session.topicsOfUser != null) {
            if (session.topicsOfUser!!.size != itemsTemp.size) {
                Log.i("TAG", "onResume: LOAD FROM session.topicsOfUser")

                val list = session.topicsOfUser!!.map {
                    LibraryTopicAdapterItem(it, session.user!!)
                }
                items.clear()
                itemsTemp.clear()
                itemsTemp.addAll(list)
                items.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }
    }

}