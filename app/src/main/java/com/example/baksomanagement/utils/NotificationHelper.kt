package com.example.baksomanagement.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.baksomanagement.R

object NotificationHelper {

    private const val CHANNEL_ID = "bakso_order_channel"

    fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Order Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi order masuk"
            }

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(channel)
        }
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(
            "app_settings",
            Context.MODE_PRIVATE
        )
        return sharedPref.getBoolean(
            "global_notification",
            true
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showOrderNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.baksoku_foreground)
            .setContentTitle("BaksoKu")
            .setContentText("Orderan telah diterima masuk")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat
            .from(context)
            .notify(1001, notification)
    }

    @RequiresPermission(
        Manifest.permission.POST_NOTIFICATIONS
    )
    fun showStatusNotification(
        context: Context,
        title: String,
        body: String
    ) {

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.mipmap.baksoku_foreground
                )
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(
                System.currentTimeMillis().toInt(),
                notification
            )
    }
}