package com.example.final_android_quizlet.activity


import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.auth.ChangePassword
import com.example.final_android_quizlet.auth.Login
import com.example.final_android_quizlet.common.ActionTransition
import com.example.final_android_quizlet.common.DialogClickedEvent
import com.example.final_android_quizlet.common.Session
import com.example.final_android_quizlet.fragments.dialog.DialogLogout
import com.example.final_android_quizlet.fragments.dialog.UpdateNameDialog
import com.example.final_android_quizlet.service.AuthService
import com.example.final_android_quizlet.service.UserService
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private var imgAvatar: CircleImageView? = null
    private var tvUserName: TextView? = null
    private var tvEmail: TextView? = null
    private var tvChangePwd: TextView? = null
    private var tvChangeName: TextView? = null
    private var layoutLogout: ConstraintLayout? = null

    private val userService: UserService = UserService()
    private val authService: AuthService = AuthService()
    private val actionTransition: ActionTransition = ActionTransition(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)


        if(!authService.isLogin()){
            startActivity(Intent(this, Login::class.java))
        }else{

            imgAvatar = findViewById(R.id.imgAvatar_profile)
            tvUserName = findViewById(R.id.tvUserName_profile)
            tvEmail = findViewById(R.id.tvEmail_profile)
            tvChangePwd = findViewById(R.id.tvChangePwd_profile)
            tvChangeName = findViewById(R.id.tvChangeName_profile)
            layoutLogout = findViewById(R.id.imgLogout_profile)

            val session = Session.getInstance(this)

            val user = session.user!!
            tvUserName!!.text = user.name
            tvEmail!!.text = user.email
            if(user.avatar.isNotEmpty()){
                Picasso.get().load(user.avatar).into(imgAvatar)
            }

            tvChangeName!!.setOnClickListener {
                val updateNameDialog = UpdateNameDialog(this, tvUserName?.text.toString()) { newName ->
                    lifecycleScope.launch {
                        val res = userService.updateUserName(user.uid, newName)
                        if (res.status) {
                            val user1 = session.user!!
                            user1.name = newName
                            session.user = user1
                            tvUserName?.text = newName
                        }
                    }
                }
                updateNameDialog.show()
            }

            tvChangePwd!!.setOnClickListener {
                val intent = Intent(this, ChangePassword::class.java)
                startActivity(intent)
                actionTransition.moveNextTransition()
            }

            layoutLogout!!.setOnClickListener {
                openLogoutDialog()
            }
            imgAvatar!!.setOnClickListener {
                CropImage.activity().setAspectRatio(1, 1).start(this)
            }

        }

    }

    private fun openLogoutDialog() {
        val folder = DialogLogout(this, this, object : DialogClickedEvent {

        })
        folder.show(supportFragmentManager, "Logout Dialog")
    }

    private suspend fun uploadAvatar(uid: String, imageUri: Uri){
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Updating...")
        progressDialog.setMessage("Please wait, we're setting your data...")
        progressDialog.show()
        val res = userService.uploadAvatar(uid, imageUri)
        progressDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val session = Session.getInstance(this)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)!!
            val imageUri = result.uri
            imgAvatar!!.setImageURI(imageUri)
            lifecycleScope.launch{
                uploadAvatar(session.user!!.uid, imageUri)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}