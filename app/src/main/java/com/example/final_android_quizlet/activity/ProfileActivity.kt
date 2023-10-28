package com.example.final_android_quizlet.activity


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.final_android_quizlet.R
import com.example.final_android_quizlet.models.UserProfile

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info) // Thay thế bằng tên layout của bạn

        val userProfile = getSampleUserProfile()

        // Điền dữ liệu vào các TextView tương ứng
        findViewById<TextView>(R.id.textView6).text = userProfile.name
        findViewById<TextView>(R.id.textView9).text = userProfile.email
        findViewById<TextView>(R.id.textView15).text = userProfile.mobile
        findViewById<TextView>(R.id.textView17).text = userProfile.tell
        findViewById<TextView>(R.id.textView19).text = userProfile.address
    }

    private fun getSampleUserProfile(): UserProfile {
        return UserProfile(
            name = "Ali Son",
            email = "alison@gmail.com",
            mobile = "123-456-789",
            tell = "123-456-789",
            address = "123-456-789, XYZ Street"
        )
    }
}