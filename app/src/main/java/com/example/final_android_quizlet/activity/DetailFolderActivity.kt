package com.example.final_android_quizlet.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.common.ActionDialog
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class DetailFolderActivity : AppCompatActivity() {
    private val actionDialog: ActionDialog = ActionDialog(this, lifecycleScope)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_folder)

        val folderName = intent.getStringExtra("folderName")
        val totalTerm = intent.getStringExtra("totalTerm")
        val userName = intent.getStringExtra("userName")
        val avatar = intent.getStringExtra("avatar")

        val tvFolderName = findViewById<TextView>(R.id.tvFolderName)
        val tvTotalTerm = findViewById<TextView>(R.id.tvTotalTerm)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val imgAvatar = findViewById<CircleImageView>(R.id.imgAvatar)

        val imgMenuFolder = findViewById<ImageView>(R.id.imgMenuFolder_DetailFolderActivity)
        val imgBack = findViewById<ImageView>(R.id.imgBack_DetailFolderActivity)

        imgMenuFolder.setOnClickListener {
            showMenuDetailFolder()
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }

        tvFolderName.text = folderName
        tvTotalTerm.text = totalTerm
        tvUserName.text = userName

        if (avatar != null) {
            if (avatar.isNotEmpty()) {
                Picasso.get().load(avatar).into(imgAvatar)
            }
        }
    }

    private fun showMenuDetailFolder() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_menu_detail_folder)

        val editDetailFolder: LinearLayout = dialog.findViewById(R.id.liEdit_DetailFolderActivity)
        val addTopicDetailFolder: LinearLayout = dialog.findViewById(R.id.liAddTopic_DetailFolderActivity)
        val removeDetailFolder: LinearLayout = dialog.findViewById(R.id.liRemove_DetailFolderActivity)
        val cancelDetailFolder: ImageView = dialog.findViewById(R.id.imgCancel_DetailFolderActivity)

        editDetailFolder.setOnClickListener {
            actionDialog.openCreateFolderDialog(null)
            dialog.dismiss()
        }

        addTopicDetailFolder.setOnClickListener {
            val intent = Intent(this, AddTopic_FolderActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        removeDetailFolder.setOnClickListener {
            dialog.dismiss()
        }

        cancelDetailFolder.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}