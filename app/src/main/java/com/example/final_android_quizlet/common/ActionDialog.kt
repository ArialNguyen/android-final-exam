package com.example.final_android_quizlet.common

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.final_android_quizlet.adapter.LibraryFolderAdapter
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.fragments.dialog.DialogFolder
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.example.final_android_quizlet.service.UserService
import kotlinx.coroutines.launch


class ActionDialog(private val ctx: Context, private val lifecycleScope: LifecycleCoroutineScope) {
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val folderService: FolderService = FolderService()
    private val authService: AuthService = AuthService()

    public fun openCreateFolderDialog( adapterAndItems: AdapterAndItems? ) {
        Log.i("TAG", "openCreateFolderDialog: ")
        val folder = DialogFolder(ctx, object : DialogClickedEvent{
            override fun setSuccessButton(folderName: String, des: String) {
                Log.i("TAG", "$folderName, $des")
                lifecycleScope.launch{
                    val fetch1 = folderService.createFolder(Folder(folderName, des, listOf(), authService.getCurrentUser().uid))
                    if(fetch1.status){
                        Log.i("TAG", "Folder Created: ${fetch1.folder}")
                        Log.i("TAG", "adapterAndItems: $adapterAndItems")
                        (adapterAndItems!!.items as MutableList<LibraryFolderAdapterItem>)
                            .add(LibraryFolderAdapterItem(fetch1.folder!!, fetch1.folder!!.topics.size,
                                authService.getUserLogin().user!!
                            ))
                        (adapterAndItems.adapter as LibraryFolderAdapter).notifyDataSetChanged()
                    }else{
                        Toast.makeText(ctx, fetch1.data.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
        Log.i("TAG", "openCreateFolderDialog: ")
        folder.show((ctx as FragmentActivity?)!!.supportFragmentManager, "Folder Dialog")
    }
}
