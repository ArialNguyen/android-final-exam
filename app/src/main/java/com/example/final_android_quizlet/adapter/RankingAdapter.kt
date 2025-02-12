package com.example.final_android_quizlet.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.RankingItem
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class RankingAdapter( private val items: List<RankingItem>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>(){
    private var itemClickListener: ((RankingItem) -> Unit)? = null
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName_RankingActivity)
        val tvTotalTrue: TextView = itemView.findViewById(R.id.tvTotalTrue_RankingActivity)
        val imgAvatar: CircleImageView = itemView.findViewById(R.id.imgAvatar_RankingActivity)
        val tvRank: TextView = itemView.findViewById(R.id.tvRank_RankingActivity)

        fun bind(item: RankingItem) {
            tvUserName.text = item.user.name
            tvTotalTrue.text = if (item.quizWrite == null) item.multipleChoice!!.overall.toString() else item.quizWrite.overall.toString()
            if (item.user.avatar.isNotEmpty()) {
                Picasso.get().load(item.user.avatar).into(imgAvatar)
            }
            tvRank.text = (adapterPosition + 1).toString()

            val conRanking = itemView.findViewById<ConstraintLayout>(R.id.conRanking_ranking)

            when (adapterPosition) {
                0 -> {
                    conRanking.setPadding(18)
                    conRanking.setBackgroundResource(R.drawable.drawable_rank_1)
                }
                1 -> {
                    conRanking.setPadding(10)
                    conRanking.setBackgroundResource(R.drawable.drawable_rank_2)
                }
                2 -> {
                    conRanking.setPadding(9)
                    conRanking.setBackgroundResource(R.drawable.drawable_rank_3)
                }
                else -> {
                    conRanking.setBackgroundResource(R.drawable.drawable_rank_default)
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingAdapter.ViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_ranking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(listener: (RankingItem) -> Unit) {
        itemClickListener = listener
    }
}