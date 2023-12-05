package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import java.util.Locale

class LanguageAdapter(private val items: List<Locale>) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
    private var itemClickListener: ((Locale) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName_LanguageItem)

        fun bind(item: Locale) {
            tvName.text = item.displayName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun setOnItemClickListener(listener: (Locale) -> Unit) {
        itemClickListener = listener
    }
}