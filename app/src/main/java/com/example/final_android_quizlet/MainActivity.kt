package com.example.final_android_quizlet


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.activity.CreateTermActivity
import com.example.final_android_quizlet.activity.LibraryActivity
import com.example.final_android_quizlet.activity.ProfileActivity
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.DialogClickedEvent
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.example.final_android_quizlet.fragments.DialogFolder
import com.example.final_android_quizlet.models.Folder
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.FolderService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val authService: AuthService = AuthService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val folderService: FolderService = FolderService()
    private var txName_main: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val plusIcon: ImageView = findViewById(R.id.imageView5)
//        val btn_logout = findViewById<Button>(R.id.btn_logout)
        txName_main = findViewById(R.id.txName_main)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java).putExtra("checkLogin", this@MainActivity::class.java.name))
        }

//        btn_logout!!.setOnClickListener {
//            lifecycleScope.launch {
//                authService.logout()
//            }
//        }

        lifecycleScope.launch {
            val user = authService.getUserLogin().user!!
            txName_main!!.text = user.name
        }


        plusIcon.setOnClickListener {
            showBottomDialog()
        }

        val profileButton: ImageView = findViewById(R.id.imageView7)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val libraryButton: ImageView = findViewById(R.id.imageView6)
        libraryButton.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
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
            openDialog()
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

    private fun openDialog() {
        val folder = DialogFolder(this, object : DialogClickedEvent{
            override fun setSuccessButton(folderName: String, des: String) {
                Log.i("TAG", "$folderName, $des")
                manageScopeApi.getResponseWithCallback(
                    lifecycleScope,
                    {(folderService::createFolder)(Folder(folderName, des, listOf(), authService.getCurrentUser().uid))},
                    object : CallbackInterface{
                        override fun onCallback(res: ResponseObject) {
                            if(res.status){
                                Log.i("TAG", "Folder Created: ${res.folder}")
                            }else{
                                Toast.makeText(this@MainActivity, res.data.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
        })
        folder.show(supportFragmentManager, "Folder Dialog")
    }




    override fun onResume() {
        super.onResume()
        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java).putExtra("checkLogin", this@MainActivity::class.java.name))
        }
    }
}