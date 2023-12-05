package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.UserItem
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val users: MutableList<UserItem>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var itemClickListener: ((UserItem) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvUsername_userItem)
        private val avatar: CircleImageView = itemView.findViewById(R.id.imgAvatarIcon_userItem)
        private val tvTotalTopic: TextView = itemView.findViewById(R.id.tvTotalTopic_userItem)

        fun bind(item: UserItem) {
            tvName.text = item.user.name
            val topicsCountText = "${item.totalTopic} học phần" // Only Public topic
            tvTotalTopic.text = topicsCountText
            if (item.user.avatar.isNotEmpty()) {
                Picasso.get().load(item.user.avatar).into(avatar)
            }
            itemView.setOnClickListener {
                itemClickListener?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setOnItemClickListener(listener: (UserItem) -> Unit) {
        itemClickListener = listener
    }
}