package com.example.final_android_quizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.models.Term
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class LibraryTopicAdapter(private val items: List<LibraryTopicAdapterItem>) : RecyclerView.Adapter<LibraryTopicAdapter.ViewHolder>() {
    private var itemClickListener: ((LibraryTopicAdapterItem) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTopicTitle: TextView = itemView.findViewById(R.id.tvTitleName_library)
        val tvTotalTerm: TextView = itemView.findViewById(R.id.tvTotalTerm_library)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName_library)
        val imgAvatar: CircleImageView = itemView.findViewById(R.id.imgAvatarIcon_library)
        fun bind(item: LibraryTopicAdapterItem) {
            tvTopicTitle.text = item.topic.title
            tvTotalTerm.text = "${item.topic.terms.size} thuật ngữ"
            tvUserName.text = item.user.name
            if(item.user.avatar!!.isNotEmpty()){
                Picasso.get().load(item.user.avatar).into(imgAvatar)
            }
            itemView.setOnClickListener {
                itemClickListener?.invoke(item)
            }
            val position = adapterPosition
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tien_trinh, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(listener: (LibraryTopicAdapterItem) -> Unit) {
        itemClickListener = listener
    }
}