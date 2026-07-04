package com.example.baksomanagement

import android.app.Application
import android.util.Log
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.external.UiKitApi

class Baksoku : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("MIDTRANS", "==============")
        Log.d("MIDTRANS", "Init Midtrans")
        Log.d("MIDTRANS", "==============")

        UiKitApi.Builder()
            .withMerchantClientKey("Mid-client-rOV6A5NLvAjb17fQ")
            .withContext(this)
            .withMerchantUrl("http://10.0.2.2:3000/")
            .enableLog(true)
            .withColorTheme(
                CustomColorTheme(
                    "#F57C00",
                    "#F57C00",
                    "#000000"
                )
            )
            .build()

        Log.d("MIDTRANS", "==============")
        Log.d("MIDTRANS", "SDK READY")
        Log.d("MIDTRANS", "==============")
    }
}