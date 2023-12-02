package com.example.final_android_quizlet.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.example.final_android_quizlet.common.EOrientationRecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class TopicAdapter(private val flow: EOrientationRecyclerView, private val items: List<LibraryTopicAdapterItem>) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {
    private var itemClickListener: ((LibraryTopicAdapterItem) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTopicTitle: TextView = if (flow.name == EOrientationRecyclerView.VERTICAL.name) itemView.findViewById(R.id.tvTitleName_library) else itemView.findViewById(R.id.tvTitleName_itemTopicHorizon)
        val tvTotalTerm: TextView = if (flow.name == EOrientationRecyclerView.VERTICAL.name) itemView.findViewById(R.id.tvTotalTerm_library) else itemView.findViewById(R.id.tvTotalTerm_itemTopicHorizon)
        val tvUserName: TextView = if (flow.name == EOrientationRecyclerView.VERTICAL.name) itemView.findViewById(R.id.tvUserName_library) else itemView.findViewById(R.id.tvUserName_itemTopicHorizon)
        val imgAvatar: CircleImageView =  if (flow.name == EOrientationRecyclerView.VERTICAL.name) itemView.findViewById(R.id.imgAvatarIcon_library) else itemView.findViewById(R.id.imgAvatarIcon_itemTopicHorizon)
        fun bind(item: LibraryTopicAdapterItem) {
            Log.i("TAG", "bind: $item")
            tvTopicTitle.text = item.topic.title
            tvTotalTerm.text = "${item.topic.terms.size} thuật ngữ"
            tvUserName.text = item.user?.name
            if (item.user?.avatar!!.isNotEmpty()) {
                Picasso.get().load(item.user.avatar).into(imgAvatar)
            }
            itemView.setOnClickListener {
                itemClickListener?.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = if (flow.name == EOrientationRecyclerView.VERTICAL.name) LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tien_trinh, parent, false)
        else LayoutInflater.from(parent.context).inflate(R.layout.item_topic_horizontal, parent, false)

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