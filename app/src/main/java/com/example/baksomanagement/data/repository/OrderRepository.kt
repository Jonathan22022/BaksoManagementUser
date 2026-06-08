package com.example.baksomanagement.data.repository

import android.os.Bundle
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.model.OrderItem
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.QueryDocumentSnapshot

class OrderRepository {

    private val firestore = FirebaseClient.firestore

    fun getOrderById(id: String, onResult: (Order?) -> Unit) {
        firestore.collection("order").document(id)
            .get()
            .addOnSuccessListener {
                val order = it.toObject(Order::class.java)?.copy(id = it.id)
                onResult(order)
            }
    }

    fun getTopPopularMenus(
        onResult: (List<String>) -> Unit
    ) {

        firestore.collection("orders")
            .get()
            .addOnSuccessListener { orderResult ->

                val menuCounter =
                    mutableMapOf<String, Int>()

                val totalOrders =
                    orderResult.documents.size

                if (totalOrders == 0) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var processed = 0

                orderResult.documents.forEach { orderDoc ->

                    firestore.collection("orders")
                        .document(orderDoc.id)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { itemResult ->

                            itemResult.documents.forEach { item ->

                                val menuId =
                                    item.getString("menu_id")
                                        ?: return@forEach

                                val qty =
                                    item.getLong("quantity")
                                        ?.toInt() ?: 0

                                menuCounter[menuId] =
                                    (menuCounter[menuId] ?: 0) + qty
                            }

                            processed++

                            if (processed == totalOrders) {

                                val top3 =
                                    menuCounter.toList()
                                        .sortedByDescending {
                                            it.second
                                        }
                                        .take(3)
                                        .map {
                                            it.first
                                        }

                                onResult(top3)
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun createOrder(
        order: Order,
        items: List<OrderItem>,
        onSuccess: () -> Unit
    ) {
        val orderRef = firestore.collection("orders").document()

        val orderData = hashMapOf(
            "userID" to order.userID,
            "createdAt" to order.createdAt,
            "status" to order.status,
            "total" to order.total
        )

        orderRef.set(orderData)
            .addOnSuccessListener {

                val batch = firestore.batch()

                items.forEach { item ->
                    val itemRef = orderRef.collection("items").document()

                    val itemData = hashMapOf(
                        "menu_id" to item.menu_id,
                        "nama" to item.nama,
                        "harga" to item.harga,
                        "quantity" to item.quantity,
                        "catatan" to item.catatan,
                        "addons" to item.addons,
                        "imageUrl" to item.imageUrl
                    )

                    batch.set(itemRef, itemData)
                }

                batch.commit().addOnSuccessListener {
                    onSuccess()
                }
            }
    }

    fun getFinishedOrders(
        userId: String,
        onResult: (List<String>) -> Unit
    ) {

        firestore.collection("orders")
            .whereEqualTo("userID", userId)
            .whereEqualTo("status", "selesai")
            .orderBy(
                "createdAt",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            .get()
            .addOnSuccessListener { result ->

                val menuIds =
                    mutableListOf<String>()

                val orderDocs =
                    result.documents

                if (orderDocs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var processed = 0

                orderDocs.forEach { orderDoc ->

                    firestore.collection("orders")
                        .document(orderDoc.id)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { itemResult ->

                            itemResult.documents.forEach { item ->

                                val menuId =
                                    item.getString("menu_id")
                                        ?: return@forEach

                                if (!menuIds.contains(menuId)) {

                                    if (menuIds.size >= 3) {

                                        menuIds.removeLast()
                                    }

                                    menuIds.add(menuId)
                                }
                            }

                            processed++

                            if (processed == orderDocs.size) {
                                onResult(menuIds)
                            }
                        }
                }
            }
    }
}