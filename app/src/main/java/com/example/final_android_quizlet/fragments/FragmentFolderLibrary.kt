package com.example.final_android_quizlet.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.DetailFolderActivity
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import kotlinx.coroutines.launch

class FragmentFolderLibrary(private val ctx: Context,private val getBackAdapterFromViewPager: GetBackAdapterFromViewPager) : Fragment() {
    private var items: MutableList<LibraryFolderAdapterItem> = mutableListOf()
    private val authService: AuthService = AuthService()
    private val folderService: FolderService = FolderService()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = LibraryFolderAdapter(items)
        val view = inflater.inflate(R.layout.fragment__thu_muc, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.folderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            val user = authService.getUserLogin().user!!
            val fetch1 = folderService.FolderForUserLogged().getFolders()
            val folders = mutableListOf<Folder>()
            if(fetch1.status) {
                folders.addAll(fetch1.folders!!)
            }else{
                Toast.makeText(activity, fetch1.data.toString(), Toast.LENGTH_LONG).show()
            }
            Log.i("TAG", "folders: $folders")
            val list =  folders.map {
                LibraryFolderAdapterItem(it, it.topics.size, user)
            }.toMutableList()
            items.addAll(list)
//            getBackAdapterFromViewPager.onResult(items, adapter::class.java)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener { item ->
            val intent = Intent(requireContext(), DetailFolderActivity::class.java)
            intent.putExtra("folder", item.folder)
            startActivity(intent)
        }
        getBackAdapterFromViewPager.onResult(items, adapter)
        return view
    }
}