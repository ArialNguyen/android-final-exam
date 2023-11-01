package com.example.final_android_quizlet.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.Term
import kotlin.math.log

class TermAdapter(private val terms: ArrayList<Term>) : RecyclerView.Adapter<TermAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val etTerm: EditText = itemView.findViewById(R.id.editTextAlgorithm)
        private val etDefinition: EditText = itemView.findViewById(R.id.editTextDefinition)

        fun bind(item: Term) {
            etTerm.setText(item.term)
            etDefinition.setText(item.definition)
            val position = adapterPosition
            etTerm.addTextChangedListener {
                terms[position].term = etTerm.text.toString()
            }
            etDefinition.addTextChangedListener {
                terms[position].definition = etDefinition.text.toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_algorithm, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = terms[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return terms.size
    }


}