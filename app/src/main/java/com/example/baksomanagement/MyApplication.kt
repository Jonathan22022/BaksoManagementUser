package com.example.baksomanagement

import android.app.Application
import android.util.Log
import com.midtrans.sdk.uikit.external.UiKitApi

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("MIDTRANS", "Initializing UiKit")
        Log.d("MIDTRANS", "================================")
        Log.d("MIDTRANS", "MyApplication ON CREATE")
        Log.d("MIDTRANS", "================================")
        UiKitApi.Builder()
            .withMerchantClientKey("Mid-client-rOV6A5NLvAjb17fQ")
            .withContext(applicationContext)
            .withMerchantUrl("http://10.0.2.2:3000/")
            .enableLog(true)
            .build()

        Log.d("MIDTRANS", "UiKit initialized")
    }
}