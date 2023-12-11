package com.example.final_android_quizlet.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.adapter.*
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.GetBackAdapterFromViewPager
import com.example.final_android_quizlet.common.ManageScopeApi
import com.example.final_android_quizlet.fragments.DefaultFragmentRv
import com.example.final_android_quizlet.models.EModeTopic
import com.example.final_android_quizlet.models.Enum.ETermList
import com.example.final_android_quizlet.models.Term
import com.example.final_android_quizlet.models.Topic
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.TopicService
import com.example.final_android_quizlet.service.UserService
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.relex.circleindicator.CircleIndicator2

class DetailTopic : AppCompatActivity() {
    // Service
    private val topicService: TopicService = TopicService()
    private val manageScopeApi: ManageScopeApi = ManageScopeApi()
    private val actionTransition: ActionTransition = ActionTransition(this)
    private val authService: AuthService = AuthService()
    private val userService: UserService = UserService()


    private var toolbar: Toolbar? = null
    private lateinit var imgRanking: ImageView
    private lateinit var tvMode: TextView
    private var indicator2: CircleIndicator2? = null
    private var cvFlashCard: CardView? = null
    private var cvChoice: CardView? = null
    private var cvWriteText: CardView? = null
    private var tvTopicName: TextView? = null
    private var tvUserName: TextView? = null
    private var avatarUser: CircleImageView? = null
    private var tvDecription: TextView? = null
    private var tvTotalTerm: TextView? = null
    private lateinit var viewPagerTerm: ViewPager2
    private lateinit var recyclerViewLeft: RecyclerView
    private lateinit var recyclerViewRight: RecyclerView

    // Adapter For Term
    private lateinit var tabDetailTopic: TabLayout
    private lateinit var adapterVP: VPCommunityAdapter
    private lateinit var termAdapterAll: TermStarAdapter
    private lateinit var termAdapterStar: TermStarAdapter
    private val termsItem: MutableList<TermStarAdapterItem> = mutableListOf()
    private val termsStarItem: MutableList<TermStarAdapterItem> = mutableListOf()

    // Adapter
    private var recyclerViewHorizontal: RecyclerView? = null
    private val items: MutableList<Term> = mutableListOf()

    // Info Current
    private lateinit var currentTopic: Topic
    private lateinit var currentUserId: String
    private var currentTab: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hocphan)

        if (!authService.isLogin()) {
            startActivity(Intent(this, Login::class.java))
            actionTransition.moveNextTransition()
        }

        // Handle intent
        if (intent.getStringExtra("openExamChoice") != null) {

        }

        if (intent.getStringExtra("topicId") == null || intent.getStringExtra("topicId")!!.isEmpty()) {
            Toast.makeText(this, "Something error... Try again!", Toast.LENGTH_LONG).show()
            finish()
            actionTransition.rollBackTransition()
        }
        // Handle preData
        val topicId = intent.getStringExtra("topicId")!!
        currentUserId = authService.getCurrentUser().uid

        toolbar = findViewById(R.id.toolbar_detail_hocphan)
//        tvTerm = findViewById(R.id.tv_Term_TopicDetail)
        imgRanking = findViewById(R.id.imgRank_DetailTopic)
        tvMode = findViewById(R.id.tvMode_DetailTopic)
        recyclerViewHorizontal = findViewById(R.id.recyclerView_DetailTopic)
        cvFlashCard = findViewById(R.id.cardview_flashcard)
        cvChoice = findViewById(R.id.cardview_choice)
        cvWriteText = findViewById(R.id.cardview_writeText)

        tvTopicName = findViewById(R.id.tvTopicName_DetailTopic)
        tvDecription = findViewById(R.id.tvDescriptionTopic_DetailTopic)
        tvTotalTerm = findViewById(R.id.tvTotalTerm_DetailTopic)
        avatarUser = findViewById(R.id.imgAvatarIcon_DetailTopic)
        tvUserName = findViewById(R.id.tvUserName_DetailTopic)

        viewPagerTerm = findViewById(R.id.viewPager2_DetailTopic)
        tabDetailTopic = findViewById(R.id.tabDetailTopic)

        val imgMenuTopic = findViewById<ImageView>(R.id.imgMenuTopic_DetailTopic)
        val imgBack = findViewById<ImageView>(R.id.imgBack_DetailTopic)

        imgRanking.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java).putExtra("topicId", topicId))
            actionTransition.moveNextTransition()
        }

        imgMenuTopic.setOnClickListener {
            showMenuDetailTopic()
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }


        cvFlashCard!!.setOnClickListener {
//            val intent = Intent(this, MainQuizActivity::class.java)
//            intent.putExtra("classDestination", FlashcardActivity::class.simpleName)
//            intent.putExtra("topicId", currentTopic.uid)
//            startActivity(intent)
            val intent = Intent(this, SettingFlash::class.java)
            intent.putExtra("topic", currentTopic)
            intent.putExtra("exam", FlashcardActivity::class.simpleName)
            startActivity(intent)
            actionTransition.moveNextTransition()
        }

        cvChoice!!.setOnClickListener {
            val intent = Intent(this, OptionExam::class.java)
            currentTopic.starList.clear()
            currentTopic.starList.addAll(termsStarItem.map { it.term })
            intent.putExtra("topic", currentTopic)
            intent.putExtra("exam", ChoiceTest::class.simpleName)
            intent.putExtra("typeTerm", if (currentTab == 0)  ETermList.NORMAL_TERMS else ETermList.STAR_TERMS)
            startActivity(intent)
            actionTransition.moveNextTransition()
        }

        cvWriteText!!.setOnClickListener {
            val intent = Intent(this, OptionExam::class.java)
            currentTopic.starList.clear()
            currentTopic.starList.addAll(termsStarItem.map { it.term })
            intent.putExtra("topic", currentTopic)
            intent.putExtra("exam", WriteQuizActivity::class.simpleName)
            intent.putExtra("typeTerm", if (currentTab == 0)  ETermList.NORMAL_TERMS else ETermList.STAR_TERMS)
            startActivity(intent)
            actionTransition.moveNextTransition()
        }

        // Recycler View
        val adapter = DetailTopicHoriAdapter(items)
        recyclerViewHorizontal = findViewById(R.id.recyclerView_DetailTopic)
        recyclerViewHorizontal!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHorizontal!!.adapter = adapter

        val spacingInPixels = -30
        recyclerViewHorizontal!!.addItemDecoration(HorizontalSpaceItemDecoration(spacingInPixels))
        // CircleIndicator2
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerViewHorizontal)

        indicator2 = findViewById(R.id.indicator2_DetailTopic)
        indicator2!!.attachToRecyclerView(recyclerViewHorizontal!!, pagerSnapHelper)
        adapter.registerAdapterDataObserver(indicator2!!.adapterDataObserver) // Need to have this line to update data

        // Handle View Term
        val tab1 = tabDetailTopic.newTab()
        tab1.setText("Học hết")
        val tab2 = tabDetailTopic.newTab()
        tab2.setText("Học 1")

        val termAll = DefaultFragmentRv(object : GetBackAdapterFromViewPager {
            override fun onActionBack(view: View) {
                actionOnTermAll(view)
            }
        })
        val termStar = DefaultFragmentRv(object : GetBackAdapterFromViewPager {
            override fun onActionBack(view: View) {
                actionOnTermStar(view)
            }
        })

        adapterVP = VPCommunityAdapter(this)
        termAdapterAll = TermStarAdapter(termsItem)
        termAdapterStar = TermStarAdapter(termsStarItem)

        adapterVP.addFragment(termAll, "Học hết")
        adapterVP.addFragment(termStar, "Học ${termsStarItem.size}")

        viewPagerTerm.adapter = adapterVP
        TabLayoutMediator(tabDetailTopic, viewPagerTerm) { tab, position ->
            tab.text = adapterVP.getTabTitle(position)
            tab.view.setOnClickListener {
                currentTab = position
            }
        }.attach() // Connect viewPager and Tab

        termAdapterAll.setOnItemStarClickListener { it , position ->
            Log.i("TAG", "Handle click: $it")
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    if(it.selected){
                        termsStarItem.add(it)
                        runOnUiThread {
                            tabDetailTopic.getTabAt(1)!!.text = "Học ${termsStarItem.size}"
                            termAdapterStar.notifyItemInserted(termsStarItem.size)
                        }
                        topicService.addTermToStarTopic(currentTopic.uid, it.term)
                    }else{
                        // Remove
                        val idxStar = termsStarItem.indexOfFirst { it1 -> it.term.uid == it1.term.uid }
                        termsStarItem.removeAt(idxStar)
                        runOnUiThread {
                            tabDetailTopic.getTabAt(1)!!.text = "Học ${termsStarItem.size}"
                            termAdapterStar.notifyItemRemoved(idxStar)
                        }
                        topicService.removeTermToStarTopic(currentTopic.uid, it.term)
                    }
                    runOnUiThread {
                        termAdapterAll.notifyItemChanged(position)
                    }
                }
            }
        }
        termAdapterStar.setOnItemStarClickListener { it, i ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    termsStarItem.removeAt(i)
                    val idxOfTermAll = termsItem.indexOfFirst { it1 -> it1.term.uid == it.term.uid }
                    termsItem[idxOfTermAll].selected = false
                    runOnUiThread {
                        termAdapterStar.notifyItemRemoved(i)
                        tabDetailTopic.getTabAt(1)!!.text = "Học ${termsStarItem.size}"
                        termAdapterAll.notifyItemChanged(idxOfTermAll)
                    }
                    topicService.removeTermToStarTopic(currentTopic.uid, it.term)
                }
            }
        }


        // Fetch data
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val fetchTopic = topicService.getTopicById(topicId)
                if(fetchTopic.status){
                    currentTopic = fetchTopic.topic!!
                    termsItem.addAll(currentTopic.terms.map {
                        TermStarAdapterItem(it, currentTopic.starList.firstOrNull { it2 -> it2.uid == it.uid } != null)
                    })
                    termsStarItem.addAll(currentTopic.starList.map { TermStarAdapterItem(it, true) })
                    runOnUiThread {
                        if(::termAdapterAll.isInitialized){
                            termAdapterAll.notifyDataSetChanged()
                        }
                        if(::termAdapterStar.isInitialized){
                            tabDetailTopic.getTabAt(1)!!.text = "Học ${termsStarItem.size}"
                            termAdapterStar.notifyDataSetChanged()
                        }
                    }
                    val fetchUser = userService.getUserByField("uid", currentTopic.userId)
                    if(fetchUser.status){
                        items.addAll(currentTopic.terms)
                        runOnUiThread {
                            Picasso.get().load(fetchUser.user!!.avatar).into(avatarUser)
                            tvTopicName!!.text = currentTopic.title
                            tvDecription!!.text = currentTopic.description
                            tvTotalTerm!!.text = "${currentTopic.terms.size} thuật ngữ"
                            tvUserName!!.text = fetchUser.user!!.name
                            tvMode.text = currentTopic.mode.name[0].toString() + currentTopic.mode.name.substring(1).lowercase()
                            adapter.notifyDataSetChanged()
                        }
                    }else{
                        Toast.makeText(this@DetailTopic, fetchUser.data.toString(), Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this@DetailTopic, fetchTopic.data.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    }
    private fun actionOnTermAll(view: View){
        recyclerViewLeft = view.findViewById(R.id.recyclerViewUser_defaultFG)
        recyclerViewLeft.adapter = termAdapterAll
        recyclerViewLeft.layoutManager = LinearLayoutManager(this)
        if(termsItem.isNotEmpty()){
            recyclerViewLeft.setItemViewCacheSize(termsItem.size)
            termAdapterAll.notifyDataSetChanged()
        }
        termAdapterAll.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                updateHeight()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                updateHeight()
            }

            private fun updateHeight() {
                val adapter = recyclerViewLeft.adapter ?: return
                val itemCount = adapter.itemCount
                if (itemCount > 0) {
                    val child = adapter.createViewHolder(recyclerViewLeft, adapter.getItemViewType(0)).itemView
                    child.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                    val itemHeight = child.measuredHeight
                    recyclerViewLeft.layoutParams.height = itemHeight * itemCount
                    recyclerViewLeft.requestLayout()
                }
            }
        })
    }
    private fun actionOnTermStar(view: View){
        recyclerViewRight = view.findViewById(R.id.recyclerViewUser_defaultFG)
        recyclerViewRight.adapter = termAdapterStar
        recyclerViewRight.layoutManager = LinearLayoutManager(this)
        if(termsStarItem.isNotEmpty()){
            recyclerViewRight.setItemViewCacheSize(termsStarItem.size)
            termAdapterStar.notifyDataSetChanged()
        }
        termAdapterStar.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                updateHeight()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                updateHeight()
            }

            private fun updateHeight() {
                val adapter = recyclerViewRight.adapter ?: return
                val itemCount = adapter.itemCount
                if (itemCount > 0) {
                    val child = adapter.createViewHolder(recyclerViewRight, adapter.getItemViewType(0)).itemView
                    child.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                    val itemHeight = child.measuredHeight
                    Log.i("TAG", "measuredHeight: $itemHeight")
                    recyclerViewRight.layoutParams.height = itemHeight * itemCount * child.marginBottom
                    recyclerViewRight.requestLayout()
                }
            }
        })
    }

    private fun showMenuDetailTopic() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_menu_topic)
        val cancelDetailTopic: ImageView = dialog.findViewById(R.id.imgCancel_DetailTopic)
        if(currentTopic.userId != currentUserId){
            dialog.findViewById<LinearLayout>(R.id.llForOwner_menuTopic).visibility = View.GONE
            dialog.findViewById<LinearLayout>(R.id.llForGuest_menuTopic).visibility = View.VISIBLE
            val saveTopic: LinearLayout = dialog.findViewById(R.id.llSaveForGuest_DetailTopic)

            saveTopic.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        val saveTopic = userService.updateProfile(currentUserId, hashMapOf(
                           "topicSaved" to FieldValue.arrayUnion(currentTopic.uid)
                        ))
                        runOnUiThread {
                            if(saveTopic.status){
                                Toast.makeText(this@DetailTopic, "Save Topic Successfully!!", Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(this@DetailTopic, saveTopic.data.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }else{
            val addInFolder: LinearLayout = dialog.findViewById(R.id.liAddInFolder_DetailTopic)
            val editDetailTopic: LinearLayout = dialog.findViewById(R.id.liEdit_DetailTopic)
            val statusDetailTopic: LinearLayout = dialog.findViewById(R.id.liEditStatus_DetailTopic)
            val removeDetailTopic: LinearLayout = dialog.findViewById(R.id.liRemove_DetailTopic)

            addInFolder.setOnClickListener {
                val intent = Intent(this, AddTopicInFolderActivity::class.java)
                intent.putExtra("topicId", currentTopic.uid)
                startActivity(intent)
            }

            editDetailTopic.setOnClickListener {
                val intent = Intent(this, CreateTermActivity::class.java)
                intent.putExtra("topicData", currentTopic) // Gửi đối tượng Topic sang CreateTermActivity
                startActivity(intent)
                dialog.dismiss()
            }

            statusDetailTopic.setOnClickListener {
                statusActivity()
                dialog.dismiss()
            }

            removeDetailTopic.setOnClickListener {
                removeDetailTopic()
                dialog.dismiss()
            }
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

    private fun removeDetailTopic() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this topic?")

        builder.setPositiveButton("Yes") { dialog, which ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val deleteTopicResult = topicService.deleteTopic(currentTopic.uid)
                    runOnUiThread {
                        if (deleteTopicResult.status) {
                            Toast.makeText(this@DetailTopic, "Topic deleted successfully", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@DetailTopic, "Failed to delete topic", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun statusActivity() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Status")
        val statusArray = resources.getStringArray(R.array.status)
        var selectedStatus = 0
        val checkedItem = statusArray.indexOfFirst {
            currentTopic.mode.name.equals(it, ignoreCase = true)
        }
        builder.setSingleChoiceItems(statusArray, checkedItem) { dialog, i ->
            selectedStatus = i
        }
        builder.setPositiveButton("OK") { dialog, i ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val mode = if (statusArray[selectedStatus].equals(
                            EModeTopic.PUBLIC.name,
                            ignoreCase = true
                        )
                    ) EModeTopic.PUBLIC.name
                    else EModeTopic.PRIVATE.name
                    val updateMode =
                        topicService.TopicForUserLogged().updateInfo(currentTopic.uid, hashMapOf("mode" to mode))
                    runOnUiThread {
                        if(updateMode.status){
                            tvMode.text = mode[0] + mode.substring(1).lowercase()
                            Toast.makeText(this@DetailTopic, "Update Successfully", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@DetailTopic, updateMode.data.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, i ->
            dialog.dismiss()
        }
        builder.show()
    }

}

class HorizontalSpaceItemDecoration(private val spaceInPixels: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (itemPosition == 0) {
            outRect.right = spaceInPixels
        }
        /** last position */
        else if (itemCount > 0 && itemPosition == itemCount - 1) {
        }
        /** positions between first and last */
        else {
            outRect.right = spaceInPixels
        }
    }
}