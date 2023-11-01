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

        val backButton = findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.algorithmRecyclerView)
        adapter = AlgorithmAdapter(algorithmList)
        recyclerView.adapter = adapter

        val addAlgorithmButton = findViewById<ImageView>(R.id.addAlgorithmButton)

        addAlgorithmButton.setOnClickListener {
            val newItem = AlgorithmItem("", "")
            algorithmList.add(newItem)
            adapter.notifyItemInserted(algorithmList.size - 1)
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
