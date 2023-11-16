package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.ResultChoiceAdapterItem
import com.squareup.picasso.Picasso

class ChoiceTestResultAdapter(
    private val items: List<ResultChoiceAdapterItem>
) : RecyclerView.Adapter<ChoiceTestResultAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTermTitle: TextView = itemView.findViewById(R.id.tv_TermTitle_choiceTest)
        private val tvTrueAnswer: TextView = itemView.findViewById(R.id.tvTrueAnswer_choiceTest)
        private val boxFalse: LinearLayout = itemView.findViewById(R.id.layoutBoxFalseAnswer_choiceTest)
        private val tvFalseAnswer: TextView = itemView.findViewById(R.id.tvFalseAnswer_choiceTest)
        private val boxBottomFalse: LinearLayout = itemView.findViewById(R.id.layoutBoxFalse_choiceTest)
        private val boxBottomTrue: LinearLayout = itemView.findViewById(R.id.layoutBoxTrue_choiceTest)
        fun bind(item: ResultChoiceAdapterItem) {
            tvTermTitle.text = item.term
            if(item.status){
                boxBottomFalse.visibility = View.GONE
                boxFalse.visibility = View.GONE
                tvTrueAnswer.text = item.answer
            }else{
                boxBottomTrue.visibility = View.GONE
                tvTrueAnswer.text = item.definition
                tvFalseAnswer.text = item.answer
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_answer_choice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}