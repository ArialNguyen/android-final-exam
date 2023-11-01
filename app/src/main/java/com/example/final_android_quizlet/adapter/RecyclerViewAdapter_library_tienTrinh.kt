package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.Library_tientrinh

class RecyclerViewAdapter_library_tienTrinh(private val items: List<Library_tientrinh>) : RecyclerView.Adapter<RecyclerViewAdapter_library_tienTrinh.ViewHolder>() {
    private var itemClickListener: ((Library_tientrinh) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTitle: TextView = itemView.findViewById(R.id.name_title)
        val numberAlgorithms: TextView = itemView.findViewById(R.id.number_algorithms)
        val codeQuiz: TextView = itemView.findViewById(R.id.code_quiz)

        init {
            itemView.setOnClickListener {
                itemClickListener?.invoke(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tien_trinh, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTitle.text = item.name
        holder.numberAlgorithms.text = item.numberAlgorithms
        holder.codeQuiz.text = item.codeQuiz
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(listener: (Library_tientrinh) -> Unit) {
        itemClickListener = listener
    }
}