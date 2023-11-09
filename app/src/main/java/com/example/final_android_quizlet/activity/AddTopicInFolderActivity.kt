package com.example.final_android_quizlet.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.example.final_android_quizlet.R

class AddTopicInFolderActivity : AppCompatActivity() {

    private var toAddInFolder: Toolbar? = null
    private var imgBack: ImageView? = null
    private var imgYes: ImageView? = null
    private var liCreateFolder: LinearLayout? = null
    private var txCreateFolder: TextView? = null
    private var folderRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topicinfolder_intopic)

        toAddInFolder = findViewById(R.id.toAddInFolder_AddTopicInFolderActivity)
        imgBack = findViewById(R.id.imgBack_AddTopicInFolderActivity)
        imgYes = findViewById(R.id.imgYes_AddTopicInFolderActivity)
        liCreateFolder = findViewById(R.id.liCreateFolder_AddTopicInFolderActivity)
        txCreateFolder = findViewById(R.id.txCreateFolder_AddTopicInFolderActivity)
        folderRecyclerView = findViewById(R.id.folderRecyclerView)

        imgBack!!.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}