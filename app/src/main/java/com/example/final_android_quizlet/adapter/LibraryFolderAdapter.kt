package com.example.final_android_quizlet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.LibraryFolderAdapterItem
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class LibraryFolderAdapter(private val items: List<LibraryFolderAdapterItem>) : RecyclerView.Adapter<LibraryFolderAdapter.ViewHolder>() {

    private var itemClickListener: ((LibraryFolderAdapterItem) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFolderName: TextView = itemView.findViewById(R.id.tvFolderName_FolderLibrary)
        val tvTotalTopic: TextView = itemView.findViewById(R.id.tvTotalTopic_FolderLibrary)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName_FolderLibrary)
        val imgAvatar: CircleImageView = itemView.findViewById(R.id.imgAvatarIcon_folderLibrary)

        fun bind(item: LibraryFolderAdapterItem) {
            tvFolderName.text = item.folder.name
            tvTotalTopic.text = "${item.totalTopic} học phần"
            tvUserName.text = item.user.name
            if(item.user.avatar!!.isNotEmpty()){
                Picasso.get().load(item.user.avatar).into(imgAvatar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thu_muc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        // Gọi sự kiện khi người dùng nhấn vào item
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun setOnItemClickListener(listener: (LibraryFolderAdapterItem) -> Unit) {
        itemClickListener = listener
    }
}