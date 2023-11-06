package com.example.final_android_quizlet.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.models.Term
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class DetailTopicHoriAdapter(private val items: MutableList<Term>) : RecyclerView.Adapter<DetailTopicHoriAdapter.ViewHolder>() {
    private var itemClickListener: ((Term) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTerm: TextView = itemView.findViewById(R.id.tv_Term_TopicDetail)
        fun bind(item: Term) {
            tvTerm.text = item.term
            tvTerm.setOnClickListener{
                val anime_1 = ObjectAnimator.ofFloat(tvTerm, "scaleX", 1f, 0f)
                val anime_2 = ObjectAnimator.ofFloat(tvTerm, "scaleX", 0f, 1f)
                anime_1.interpolator = DecelerateInterpolator()
                anime_2.interpolator = DecelerateInterpolator()

                anime_1.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        tvTerm.text = if (tvTerm.text == item.term) item.definition else item.term
                        anime_2.start()
                    }
                })
                anime_1.start()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_topic_item_horizontal, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(listener: (Term) -> Unit) {
        itemClickListener = listener
    }
}