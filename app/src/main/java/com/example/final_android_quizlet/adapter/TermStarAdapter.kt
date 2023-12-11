package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.Term
import com.squareup.picasso.Picasso

data class TermStarAdapterItem(
    val term: Term,
    var selected: Boolean
)

class TermStarAdapter(
    private val items: MutableList<TermStarAdapterItem>
) : RecyclerView.Adapter<TermStarAdapter.ViewHolder>() {
    private var itemClickListener: ((TermStarAdapterItem, Int) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTerm: TextView = itemView.findViewById(R.id.tvTerm_itemTerm)
        private val tvDefinition: TextView = itemView.findViewById(R.id.tvDefinition_itemTerm)
        private val imgStar: ImageView = itemView.findViewById(R.id.imgStar_itemTerm)
        fun bind(item: TermStarAdapterItem) {
            tvTerm.text = item.term.term
            tvDefinition.text = item.term.definition
            imgStar.setImageResource(if (item.selected) R.drawable.star_fill else R.drawable.star_fall_minimalistic_2_svgrepo_com)
            imgStar.setOnClickListener {
                if (item.selected){
                    items[adapterPosition].selected = false
                    imgStar.setImageResource( R.drawable.star_fall_minimalistic_2_svgrepo_com)
                }else{
                    items[adapterPosition].selected = true
                    imgStar.setImageResource(R.drawable.star_fill)
                }
                itemClickListener?.invoke(items[adapterPosition], adapterPosition)

            }
            itemView.measuredWidth
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.term_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemStarClickListener(listener: (TermStarAdapterItem, Int) -> Unit) {
        itemClickListener = listener
    }

}