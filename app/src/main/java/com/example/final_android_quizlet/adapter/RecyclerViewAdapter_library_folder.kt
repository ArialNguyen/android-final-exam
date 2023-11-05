package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.Library_folder

class RecyclerViewAdapter_library_folder(private val items: List<Library_folder>) : RecyclerView.Adapter<RecyclerViewAdapter_library_folder.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameFolder: TextView = itemView.findViewById(R.id.name_folder)
        val codeQuiz: TextView = itemView.findViewById(R.id.code_quiz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thu_muc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameFolder.text = item.nameFolder
        holder.codeQuiz.text = item.codeQuiz
    }

    override fun getItemCount(): Int {
        return items.size
    }
}