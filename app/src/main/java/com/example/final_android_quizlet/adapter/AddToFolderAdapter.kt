package com.example.final_android_quizlet.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.Folder

class AddToFolderAdapter(private val items: List<Folder>, var itemsChosen: MutableList<Int>, private val ctx: Context) :
    RecyclerView.Adapter<AddToFolderAdapter.ViewHolder>() {

    private var itemClickListener: ((Folder) -> Unit)? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFolderName: TextView = itemView.findViewById(R.id.tvFolderName_AddToFolder)
        val cv_AddToFolder: CardView = itemView.findViewById(R.id.cv_AddToFolder)
        private var adapter: AddToFolderAdapter? = null
        init {
            adapter = this@AddToFolderAdapter
        }

        fun bind(item: Folder, ctx: Context, itemsChosen: MutableList<Int>) {
            tvFolderName.text = item.name
            cv_AddToFolder.foreground = if(itemsChosen.firstOrNull { it == adapterPosition } != null)
                AppCompatResources.getDrawable(ctx, R.drawable.bg_roundrect_ripple_light_border) else null

            cv_AddToFolder.setOnClickListener {
                if (cv_AddToFolder.foreground == null) {
                    cv_AddToFolder.foreground =
                        AppCompatResources.getDrawable(ctx, R.drawable.bg_roundrect_ripple_light_border)
                    itemsChosen.add(adapterPosition)
                } else {
                    itemsChosen.remove(adapterPosition)
                    cv_AddToFolder.foreground = null
                }
                adapter!!.notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_to_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, ctx, itemsChosen)
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(listener: (Folder) -> Unit) {
        itemClickListener = listener
    }
}