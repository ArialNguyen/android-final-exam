package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val users: MutableList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    private var itemClickListener: ((User) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvUsername_userItem)
        private val avatar: CircleImageView = itemView.findViewById(R.id.imgAvatarIcon_userItem)

        fun bind(item: User) {
            tvName.text = item.name
            if (item.avatar!!.isNotEmpty()) {
                Picasso.get().load(item.avatar).into(avatar)
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

    fun setOnItemClickListener(listener: (User) -> Unit) {
        itemClickListener = listener
    }
}