package com.example.baksomanagement

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baksomanagement.data.remote.CloudinaryClient
import com.example.baksomanagement.ui.firstPage.FirstPageFragment
import com.example.baksomanagement.utils.NotificationHelper
import com.example.baksomanagement.utils.SessionManager
import com.example.baksomanagement.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(
            ThemeManager.getTheme(this)
        )
        super.onCreate(savedInstanceState)
        CloudinaryClient.init(this)
        NotificationHelper.createChannel(this)
        if (FirebaseAuth.getInstance().currentUser != null) {

            val intent = Intent(this, HomepageActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)
    }
}