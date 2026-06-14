package com.example.baksomanagement.data.repository

import com.google.firebase.firestore.FirebaseFirestore

class AdminRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    fun getAdminTokens(
        onResult: (List<String>) -> Unit
    ) {

        firestore.collection("users")
            .whereEqualTo("role", "admin")
            .get()
            .addOnSuccessListener { result ->

                val tokens =
                    result.documents.mapNotNull {
                        it.getString("fcmToken")
                    }

                onResult(tokens)
            }
    }
}