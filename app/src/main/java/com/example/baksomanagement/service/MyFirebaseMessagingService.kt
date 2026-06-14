package com.example.baksomanagement.service

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.baksomanagement.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(
            "FCM_DEBUG",
            "onNewToken() = $token"
        )

        val uid =
            FirebaseAuth.getInstance()
                .currentUser?.uid

        Log.d(
            "FCM_DEBUG",
            "UID = $uid"
        )

        if (uid == null) {
            Log.d(
                "FCM_DEBUG",
                "User belum login"
            )
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
            .addOnSuccessListener {

                Log.d(
                    "FCM_DEBUG",
                    "Token update success"
                )
            }
            .addOnFailureListener {

                Log.e(
                    "FCM_DEBUG",
                    "Token update failed",
                    it
                )
            }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(
        message: RemoteMessage
    ) {

        Log.d(
            "FCM_DEBUG",
            "======================="
        )

        Log.d(
            "FCM_DEBUG",
            "MESSAGE RECEIVED"
        )

        Log.d(
            "FCM_DEBUG",
            "FROM = ${message.from}"
        )

        Log.d(
            "FCM_DEBUG",
            "TITLE = ${message.notification?.title}"
        )

        Log.d(
            "FCM_DEBUG",
            "BODY = ${message.notification?.body}"
        )

        Log.d(
            "FCM_DEBUG",
            "ORDER_ID = ${message.data["orderId"]}"
        )

        Log.d(
            "FCM_DEBUG",
            "STATUS = ${message.data["status"]}"
        )

        Log.d(
            "FCM_DEBUG",
            "======================="
        )

        val title =
            message.notification?.title
                ?: "BaksoKu"

        val body =
            message.notification?.body
                ?: ""

        NotificationHelper.showStatusNotification(
            this,
            title,
            body
        )
    }
}