package com.example.final_android_quizlet.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.RankingItem
import de.hdodenhof.circleimageview.CircleImageView

class RankingAdapter( private val items: List<RankingItem>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>(){
    private var itemClickListener: ((RankingItem) -> Unit)? = null
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: RankingItem) {
            Log.i("TAG", "bind: $item")
//            if (item.user?.avatar!!.isNotEmpty()) {
//                Picasso.get().load(item.user.avatar).into(imgAvatar)
//            }
            itemView.setOnClickListener {
                itemClickListener?.invoke(item)
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