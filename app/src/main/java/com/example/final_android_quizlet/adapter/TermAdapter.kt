package com.example.final_android_quizlet.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.CreateTermActivity
import com.example.final_android_quizlet.models.Term
import kotlin.math.log

class TermAdapter(private val terms: ArrayList<Term>) : RecyclerView.Adapter<TermAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
        private val etTerm: EditText = itemView.findViewById(R.id.editTextAlgorithm)
        private val etDefinition: EditText = itemView.findViewById(R.id.editTextDefinition)

        init {
            itemView.setOnLongClickListener(this)
        }

        fun bind(item: Term) {
            etTerm.setText(item.term)
            etDefinition.setText(item.definition)
            val position = adapterPosition
            etTerm.addTextChangedListener {
                terms[position].term = etTerm.text.toString()
                updateTopicWithTerms()
            }
            etDefinition.addTextChangedListener {
                terms[position].definition = etDefinition.text.toString()
                updateTopicWithTerms()
            }
        }
        private fun updateTopicWithTerms() {
            val activity = itemView.context as CreateTermActivity
            activity.updateTopicWithTerms()
        }

        override fun onLongClick(v: View?): Boolean {
            showPopupMenu(v)
            return true
        }

        private fun showPopupMenu(view: View?) {
            val popupMenu = PopupMenu(view?.context, view)
            popupMenu.inflate(R.menu.menu_delete)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.delete_item -> {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        terms.removeAt(position)
                        notifyItemRemoved(position)
                        updateTopicWithTerms()
                    }
                    true
                }
                else -> false
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