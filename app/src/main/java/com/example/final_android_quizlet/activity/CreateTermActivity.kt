package com.example.final_android_quizlet.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.AlgorithmAdapter
import com.example.final_android_quizlet.models.AlgorithmItem

class CreateTermActivity : AppCompatActivity() {
    private val algorithmList = ArrayList<AlgorithmItem>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlgorithmAdapter

    private lateinit var expandDescriptionLayout: LinearLayout
    private lateinit var expandDescriptionIcon: ImageView
    private lateinit var editTextDescription: EditText
    private var isDescriptionExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_hocphan)

        // Khai báo nút quay lại
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Đặt sự kiện click cho nút quay lại
        backButton.setOnClickListener {
            // Xử lý sự kiện khi nút quay lại được nhấn
            onBackPressed() // Để thoát khỏi CreateTermActivity và quay lại màn hình trước đó
        }

        recyclerView = findViewById(R.id.algorithmRecyclerView)
        adapter = AlgorithmAdapter(algorithmList)
        recyclerView.adapter = adapter

        val addAlgorithmButton = findViewById<ImageView>(R.id.addAlgorithmButton)

        addAlgorithmButton.setOnClickListener {
            val newItem = AlgorithmItem("", "") // Tạo một mục mới với thuật toán và định nghĩa rỗng
            algorithmList.add(newItem) // Thêm mục mới vào danh sách
            adapter.notifyItemInserted(algorithmList.size - 1) // Cập nhật RecyclerView
        }

        expandDescriptionLayout = findViewById(R.id.expandDescriptionLayout)
        expandDescriptionIcon = findViewById(R.id.expandDescriptionIcon)
        editTextDescription = findViewById(R.id.editTextDescription)

        expandDescriptionIcon.setOnClickListener {
            toggleDescriptionVisibility()
        }


    }

    private fun toggleDescriptionVisibility() {
        if (isDescriptionExpanded) {
            expandDescriptionLayout.visibility = View.GONE
            isDescriptionExpanded = false
        } else {
            expandDescriptionLayout.visibility = View.VISIBLE
            isDescriptionExpanded = true
        }
    }
}
