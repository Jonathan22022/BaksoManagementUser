package com.example.baksomanagement.data.repository

import com.example.baksomanagement.data.model.History
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.auth.FirebaseAuth

class HistoryRepository {

    private val firestore =
        FirebaseClient.firestore

    fun getHistoryOrders(
        status: String,
        onResult: (List<History>) -> Unit
    ) {

        val userId =
            FirebaseAuth.getInstance()
                .currentUser?.uid ?: return

        firestore.collection("orders")
            .whereEqualTo(
                "userID",
                userId
            )
            .whereEqualTo(
                "status",
                status
            )
            .get()
            .addOnSuccessListener { orders ->

                val historyList =
                    mutableListOf<History>()

                if (orders.isEmpty) {

                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var processed = 0

                orders.documents.forEach { orderDoc ->

                    val orderId =
                        orderDoc.id

                    val createdAt =
                        orderDoc.getLong("createdAt")
                            ?: 0L

                    val total =
                        orderDoc.getLong("total")
                            ?.toInt() ?: 0

                    firestore.collection("orders")
                        .document(orderId)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { items ->

                            val firstItem =
                                items.documents.firstOrNull()

                            if (firstItem != null) {

                                historyList.add(
                                    History(
                                        orderId = orderId,
                                        nama = firstItem.getString("nama") ?: "",
                                        imageUrl = firstItem.getString("imageUrl") ?: "",
                                        quantity = firstItem.getLong("quantity")?.toInt() ?: 0,
                                        total = total,
                                        status = status,
                                        createdAt = createdAt
                                    )
                                )
                            }

                            processed++

                            if (processed == orders.size()) {

                                onResult(
                                    historyList.sortedByDescending {
                                        it.createdAt
                                    }
                                )
                            }
                        }
                }
            }
    }

    fun deleteHistoryOrder(
        orderId: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .collection("items")
            .get()
            .addOnSuccessListener { items ->

                val batch = firestore.batch()

                items.documents.forEach {

                    batch.delete(it.reference)
                }

                batch.commit()
                    .addOnSuccessListener {

                        firestore.collection("orders")
                            .document(orderId)
                            .delete()
                            .addOnSuccessListener {

                                onSuccess()
                            }
                    }
            }
    }
}