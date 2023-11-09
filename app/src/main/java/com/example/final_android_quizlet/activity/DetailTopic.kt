package com.example.final_android_quizlet.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.DetailTopicHoriAdapter
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.HorizontalSpaceItemDecoration
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator2

class DetailTopic : AppCompatActivity() {

    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()


    private var toolbar: Toolbar? = null
    private var tvTerm: TextView? = null
    private var indicator2: CircleIndicator2? = null
    private var cvFlashCard: CardView? = null
    private var cvChoice: CardView? = null
    private var cvWriteText: CardView? = null
    private var tvTopicName: TextView? = null
    private var tvUserName: TextView? = null
    private var avatarUser: CircleImageView? = null
    private var tvDecription: TextView? = null
    private var tvTotalTerm: TextView? = null

    // Adapter
    private var recyclerViewHorizontal: RecyclerView? = null
    private val items: MutableList<Term> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hocphan)

        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
            actionTransition.moveNextTransition()
        }


        toolbar = findViewById(R.id.toolbar_detail_hocphan)
//        tvTerm = findViewById(R.id.tv_Term_TopicDetail)
        recyclerViewHorizontal = findViewById(R.id.recyclerView_DetailTopic)
        cvFlashCard = findViewById(R.id.cardview_flashcard)
        cvChoice = findViewById(R.id.cardview_choice)
        cvWriteText = findViewById(R.id.cardview_writeText)

        tvTopicName = findViewById(R.id.tvTopicName_DetailTopic)
        tvDecription = findViewById(R.id.tvDescriptionTopic_DetailTopic)
        tvTotalTerm = findViewById(R.id.tvTotalTerm_DetailTopic)
        avatarUser = findViewById(R.id.imgAvatarIcon_DetailTopic)
        tvUserName = findViewById(R.id.tvUserName_DetailTopic)

        val imgMenuTopic = findViewById<ImageView>(R.id.imgMenuTopic_DetailTopic)
        val imgBack = findViewById<ImageView>(R.id.imgBack_DetailTopic)

        imgMenuTopic.setOnClickListener {
            showMenuDetailTopic()
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }


        if(intent.getStringExtra("topicId") == null || intent.getStringExtra("topicId")!!.isEmpty()){
            Toast.makeText(this, "Something error... Try again!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }

        cvChoice!!.setOnClickListener {
            val intent = Intent(this, MainQuizActivity::class.java)
            intent.putExtra("exercise_type", "quiz")
            startActivity(intent)
        }

        cvWriteText!!.setOnClickListener {
            val intent = Intent(this, MainQuizActivity::class.java)
            intent.putExtra("exercise_type", "write")
            startActivity(intent)
        }

        // Recycler View
        val adapter = DetailTopicHoriAdapter(items)
        recyclerViewHorizontal = findViewById(R.id.recyclerView_DetailTopic)
        recyclerViewHorizontal!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHorizontal!!.adapter = adapter
        val horizontalSpaceItemDecoration = HorizontalSpaceItemDecoration()
        recyclerViewHorizontal!!.addItemDecoration(horizontalSpaceItemDecoration)
        // CircleIndicator2
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerViewHorizontal)

        indicator2 = findViewById(R.id.indicator2_DetailTopic)
        indicator2!!.attachToRecyclerView(recyclerViewHorizontal!!, pagerSnapHelper)
        adapter.registerAdapterDataObserver(indicator2!!.adapterDataObserver); // Need to have this line to update data


        cvFlashCard!!.setOnClickListener {
            val intent = Intent(this, FlashcardActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val topicId = intent.getStringExtra("topicId")!!
            val topic = topicService.TopicForUserLogged().getTopicById(topicId).topic!!
            val user = authService.getUserLogin().user!!
            tvTopicName!!.text = topic.title
            tvDecription!!.text = topic.description
            tvTotalTerm!!.text = "${topic.terms.size} thuật ngữ"
            tvUserName!!.text = user.name
            Picasso.get().load(user.avatar).into(avatarUser)
            items.addAll(topic.terms)
            adapter.notifyDataSetChanged()
        }

    }

    private fun showMenuDetailTopic() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_menu_topic)

        val addInFolder: LinearLayout = dialog.findViewById(R.id.liAddInFolder_DetailTopic)
        val editDetailTopic: LinearLayout = dialog.findViewById(R.id.liEdit_DetailTopic)
        val statusDetailTopic: LinearLayout = dialog.findViewById(R.id.liEditStatus_DetailTopic)
        val removeDetailTopic: LinearLayout = dialog.findViewById(R.id.liRemove_DetailTopic)
        val cancelDetailTopic: ImageView = dialog.findViewById(R.id.imgCancel_DetailTopic)




        addInFolder.setOnClickListener {
            val intent = Intent(this, AddTopicInFolderActivity::class.java)
            startActivity(intent)
        }

        editDetailTopic.setOnClickListener {

            dialog.dismiss()
        }

        statusDetailTopic.setOnClickListener {
            statusActivity()
            dialog.dismiss()
        }

        removeDetailTopic.setOnClickListener {
            dialog.dismiss()
        }

        cancelDetailTopic.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun statusActivity() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Status")
        val statusArray = resources.getStringArray(R.array.status)
        builder.setSingleChoiceItems(statusArray, 0) { dialog, i ->
            val selectedStatus = statusArray[i]
            Toast.makeText(this, "You Selected $selectedStatus status", Toast.LENGTH_SHORT).show()
        }

        builder.setPositiveButton("OK") { dialog, i ->

        }

        builder.setNegativeButton("Cancel") { dialog, i ->

        }

        builder.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}