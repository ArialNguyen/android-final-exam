package com.example.final_android_quizlet.adapter

import android.animation.*
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.models.Term
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class DetailTopicHoriAdapter(private val items: MutableList<Term>) : RecyclerView.Adapter<DetailTopicHoriAdapter.ViewHolder>() {
    private var itemClickListener: ((Term) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cvFront: CardView = itemView.findViewById(R.id.cvTermFront_TopicDetail)
        private val cvBack: CardView = itemView.findViewById(R.id.cvTermBack_TopicDetail)
        private val tvTermFront: TextView = itemView.findViewById(R.id.tv_TermFront_TopicDetail)
        private val tvTermBack: TextView = itemView.findViewById(R.id.tv_TermBack_TopicDetail)
        private var isFront: Boolean = true

        fun bind(item: Term) {
            var scale = itemView.resources.displayMetrics.density

            cvFront.cameraDistance = 8000 * scale
            cvBack.cameraDistance = 8000 * scale

            val frontAnimation = AnimatorInflater.loadAnimator(itemView.context, R.anim.front_animator_vertical) as AnimatorSet
            val backAnimation = AnimatorInflater.loadAnimator(itemView.context, R.anim.back_animator_vertical) as AnimatorSet

            tvTermFront.text = item.term
            tvTermBack.text = item.definition

            cvFront.setOnClickListener {
                flipCard(frontAnimation, backAnimation)
            }
            cvBack.setOnClickListener {
                flipCard(frontAnimation, backAnimation)
            }
        }

        private fun flipCard(frontAnimation: AnimatorSet, backAnimation: AnimatorSet) {
            if (isFront) {
                frontAnimation.setTarget(cvFront);
                backAnimation.setTarget(cvBack);
                frontAnimation.start()
                backAnimation.start()
                isFront = false

            } else {
                frontAnimation.setTarget(cvBack)
                backAnimation.setTarget(cvFront)
                backAnimation.start()
                frontAnimation.start()
                isFront = true
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

