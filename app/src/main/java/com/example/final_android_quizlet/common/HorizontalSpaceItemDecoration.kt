package com.example.final_android_quizlet.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state);
        val position = parent.getChildAdapterPosition(view)
        if(position == 0){
            outRect.left = 20
        }
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.right = 2
        }
    }
}