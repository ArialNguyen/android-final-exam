package com.example.final_android_quizlet.common

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.final_android_quizlet.adapter.AddToFolderAdapter
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.fragments.dialog.DialogFolder
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ActionDialog(private val ctx: Context, private val lifecycleScope: LifecycleCoroutineScope) {
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val folderService: FolderService = FolderService()
    private val authService: AuthService = AuthService()

    public fun openCreateFolderDialog(adapterAndItems: AdapterAndItems?) {
        val session = Session.getInstance(ctx)
        Log.i("TAG", "openCreateFolderDialog: ")
        val folder = DialogFolder(ctx, object : DialogClickedEvent {
            override fun setSuccessButton(folderName: String, des: String) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val fetch1 = folderService.createFolder(
                            Folder(
                                UUID.randomUUID().toString(),
                                folderName,
                                des,
                                mutableListOf(),
                                authService.getCurrentUser().uid
                            )
                        )
                        if (fetch1.status) {
                            val foldersSession = session.foldersOfUser
                            foldersSession!!.add(fetch1.folder!!)
                            session.foldersOfUser = foldersSession
                            if (adapterAndItems == null) {
                                (ctx as Activity).runOnUiThread {
                                    Toast.makeText(ctx, "Create Folder Successfully", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                when (adapterAndItems.adapter) {
                                    is LibraryFolderAdapter -> {
                                        val adapter = adapterAndItems.adapter as LibraryFolderAdapter
                                        val items = adapterAndItems.items as MutableList<LibraryFolderAdapterItem>
                                        val position: Int = items.size

                                        items.add(
                                            LibraryFolderAdapterItem(
                                                fetch1.folder!!, fetch1.folder!!.topics.size,
                                                authService.getUserLogin().user!!
                                            )
                                        )
                                        (ctx as Activity).runOnUiThread {
                                            adapter.notifyItemInserted(position)
                                        }
                                    }

                                    is AddToFolderAdapter -> {
                                        val adapter = adapterAndItems.adapter as AddToFolderAdapter
                                        val items = ((adapterAndItems.items as List<Any>)[0] as MutableList<Folder>)
                                        val itemsChosen = ((adapterAndItems.items as List<Any>)[1] as MutableList<Int>)
                                        val positionStart = items.size
                                        items.add(Folder(fetch1.folder!!))
                                        itemsChosen.add(items.size - 1)
                                        (ctx as Activity).runOnUiThread { adapter.notifyItemInserted(positionStart)}
                                    }

                                    else -> null
                                }
                            }
                        } else {
                            Toast.makeText(ctx, fetch1.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }, "", "")
        folder.show((ctx as FragmentActivity?)!!.supportFragmentManager, "Folder Dialog")
    }
}
