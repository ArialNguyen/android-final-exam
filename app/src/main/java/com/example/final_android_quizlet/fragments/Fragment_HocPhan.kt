package com.example.final_android_quizlet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.activity.Detail_HocPhan_Activity
import com.example.final_android_quizlet.adapter.RecyclerViewAdapter_library_tienTrinh
import com.example.final_android_quizlet.models.Library_tientrinh

class Fragment_HocPhan : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__hoc_phan, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.hocPhanRecyclerView)

        val items = listOf(
            Library_tientrinh(
                "Tiếng Anh",
                "3 thuật ngữ",
                R.drawable.ic_baseline_profile,
                "quizlette23254363"
            ),
            Library_tientrinh(
                "Tiếng Anh",
                "3 thuật ngữ",
                R.drawable.ic_baseline_profile,
                "quizlette23254363"
            ),
        )

        val adapter = RecyclerViewAdapter_library_tienTrinh(items)
        adapter.setOnItemClickListener { hocPhan ->
            val intent = Intent(context, Detail_HocPhan_Activity::class.java)
            // Truyền dữ liệu của học phần sang Detail_HocPhan_Activity
            intent.putExtra("tenHocPhan", hocPhan.name)
            intent.putExtra("thuocTinh", hocPhan.numberAlgorithms)
            intent.putExtra("maHocPhan", hocPhan.codeQuiz)
            intent.putExtra("hinhAnh", hocPhan.imageResource)
            startActivity(intent)
        }


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }
}