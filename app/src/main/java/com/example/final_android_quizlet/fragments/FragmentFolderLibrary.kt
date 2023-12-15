package com.example.final_android_quizlet.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.DetailFolderActivity
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.fragments.dialog.DialogLoading
import com.example.final_android_quizlet.models.EModeTopic
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class FragmentFolderLibrary(private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager?) : Fragment() {
    constructor(): this(null)
    private var items: MutableList<LibraryFolderAdapterItem> = mutableListOf()
    private val authService: AuthService = AuthService()
    private val folderService: FolderService = FolderService()
    private lateinit var adapter: LibraryFolderAdapter

    private val DETAIL_FOLDER_DELETE = 2
    private val DETAIL_FOLDER_ADD_TOPIC = 1

    private lateinit var session: Session

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == DETAIL_FOLDER_ADD_TOPIC) {
            // Handle add topic
        }else if (DETAIL_FOLDER_DELETE == result.resultCode){
            val folderId = result.data!!.getStringExtra("folderId")!!
            val idx = items.indexOfFirst { it.folder.uid == folderId }
            items.removeAt(idx)
            adapter.notifyItemRemoved(idx)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__thu_muc, container, false)
        val dialogLoading = DialogLoading(requireContext())

        if (!authService.isLogin()) {
            startActivity(Intent(requireContext(), Login::class.java))
        } else {
            session = Session.getInstance(requireContext())
            adapter = LibraryFolderAdapter(items)
            val recyclerView = view.findViewById<RecyclerView>(R.id.folderRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val user = session.user
                    if(session.foldersOfUser == null){
                        dialogLoading.showDialog("Loading...")
                        val fetch1 = folderService.FolderForUserLogged().getFolders()
                        if(fetch1.status){
                            session.foldersOfUser = fetch1.folders!!.toMutableList()
                        }else{
                            Toast.makeText(activity, fetch1.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                    Log.i("TAG", "session.foldersOfUser: ${session.foldersOfUser}")
                    val folders = session.foldersOfUser
                    if(folders!!.isNotEmpty()){
                        val list =  folders.map {
                            LibraryFolderAdapterItem(it, it.topics.size, user!!)
                        }.toMutableList()
                        items.addAll(list)
                        (context as Activity).runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    }
                    dialogLoading.hideDialog()
                }
            }

            adapter.setOnItemClickListener { item ->
                val intent = Intent(requireContext(), DetailFolderActivity::class.java)
                intent.putExtra("folder", item.folder)
                resultLauncher.launch(intent)
            }
            getBackAdapterFromViewPager?.onResult(view, items, adapter)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if(session.foldersOfUser != null){
            items.clear()
            items.addAll(session.foldersOfUser!!.map { LibraryFolderAdapterItem(it, it.topics.size, session.user!!) })
            adapter.notifyDataSetChanged()
        }
    }
}