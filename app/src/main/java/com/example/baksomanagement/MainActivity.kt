package com.example.baksomanagement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.baksomanagement.data.remote.CloudinaryClient
import com.example.baksomanagement.ui.FirstPageFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CloudinaryClient.init(this)
        setContentView(R.layout.activity_main)
    }
}