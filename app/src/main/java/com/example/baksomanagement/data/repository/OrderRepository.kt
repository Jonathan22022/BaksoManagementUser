package com.example.baksomanagement.data.repository

import android.os.Bundle
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.model.OrderItem
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject

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
                        "addons" to item.addons
                    )

                    batch.set(itemRef, itemData)
                }

                batch.commit().addOnSuccessListener {
                    onSuccess()
                }
            }
    }
}