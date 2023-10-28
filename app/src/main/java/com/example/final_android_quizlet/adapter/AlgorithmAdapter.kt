package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.AlgorithmItem

class AlgorithmAdapter(private val algorithmList: ArrayList<AlgorithmItem>) : RecyclerView.Adapter<AlgorithmAdapter.ViewHolder>(){
    private var isItemExpanded = false
    private var expandedItemPosition = -1
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val algorithmEditText: EditText = itemView.findViewById(R.id.editTextAlgorithm)
        private val definitionEditText: EditText = itemView.findViewById(R.id.editTextDefinition)


        fun bind(item: AlgorithmItem) {
            algorithmEditText.setText(item.algorithm)
            definitionEditText.setText(item.definition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_algorithm, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = algorithmList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return algorithmList.size
    }
}