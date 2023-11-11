package com.example.final_android_quizlet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.data.LibraryTopicAdapterItem
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AddTopicToFolderApdater_CreatedAndLearned(
    private val items: List<LibraryTopicAdapterItem>,
    var itemsChosen: MutableList<Int>,
    private val ctx: Context) : RecyclerView.Adapter<AddTopicToFolderApdater_CreatedAndLearned.ViewHolder>()  {

    private var itemClickListener: ((LibraryTopicAdapterItem) -> Unit)? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTopicTitle: TextView = itemView.findViewById(R.id.tvTitleName_library)
        val tvTotalTerm: TextView = itemView.findViewById(R.id.tvTotalTerm_library)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName_library)
        val imgAvatar: CircleImageView = itemView.findViewById(R.id.imgAvatarIcon_library)
        val layout: ConstraintLayout = itemView.findViewById(R.id.itemAdapterLibraryTopic)
        private var adapter: AddTopicToFolderApdater_CreatedAndLearned? = null
        init {
            adapter = this@AddTopicToFolderApdater_CreatedAndLearned
        }

        fun bind(item: LibraryTopicAdapterItem, ctx: Context, itemsChosen: MutableList<Int>) {
            tvTopicTitle.text = item.topic.title
            tvTotalTerm.text = "${item.topic.terms.size} thuật ngữ"
            tvUserName.text = item.user.name
            if(item.user.avatar!!.isNotEmpty()){
                Picasso.get().load(item.user.avatar).into(imgAvatar)
            }
            layout.foreground = if(itemsChosen.firstOrNull { it == adapterPosition } != null)
                AppCompatResources.getDrawable(ctx, R.drawable.bg_roundrect_ripple_light_border) else null

            itemView.setOnClickListener {
                if (itemView.foreground == null) {
                    itemView.foreground =
                        AppCompatResources.getDrawable(ctx, R.drawable.bg_roundrect_ripple_light_border)
                    itemsChosen.add(adapterPosition)
                } else {
                    itemsChosen.remove(adapterPosition)
                    itemView.foreground = null
                }
                adapter!!.notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tien_trinh, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, ctx, itemsChosen)

    }


    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(listener: (LibraryTopicAdapterItem) -> Unit) {
        itemClickListener = listener
    }
}