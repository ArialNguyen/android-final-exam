package com.example.final_android_quizlet.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class DetailFolderActivity : AppCompatActivity() {
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

        tvFolderName.text = folderName
        tvTotalTerm.text = totalTerm
        tvUserName.text = userName

        if (avatar != null) {
            if (avatar.isNotEmpty()) {
                Picasso.get().load(avatar).into(imgAvatar)
            }
        }
    }
}