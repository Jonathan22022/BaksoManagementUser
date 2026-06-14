package com.example.baksomanagement.data.repository

import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.model.OrderItem
import com.example.baksomanagement.data.remote.FirebaseClient
import android.util.Log

class OrderRepository {

    private val firestore = FirebaseClient.firestore

    fun getOrderById(
        id: String,
        onResult: (Order?) -> Unit
    ) {
        firestore.collection("orders")
            .document(id)
            .get()
            .addOnSuccessListener {

                val order =
                    it.toObject(Order::class.java)
                        ?.copy(id = it.id)

                onResult(order)
            }
            .addOnFailureListener {
                onResult(null)
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
        onSuccess: (String) -> Unit
    ) {

        Log.d("ORDER_DEBUG", "================================")
        Log.d("ORDER_DEBUG", "createOrder() CALLED")
        Log.d("ORDER_DEBUG", "userID = ${order.userID}")
        Log.d("ORDER_DEBUG", "total = ${order.total}")
        Log.d("ORDER_DEBUG", "items = ${items.size}")
        Log.d("ORDER_DEBUG", "================================")

        val orderRef = firestore.collection("orders").document()

        Log.d(
            "ORDER_DEBUG",
            "Generated OrderId = ${orderRef.id}"
        )

        val orderData = hashMapOf(
            "userID" to order.userID,
            "createdAt" to order.createdAt,
            "status" to order.status,
            "total" to order.total,
            "completed" to false
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

                    onSuccess(orderRef.id)
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
                com.google.firebase.firestore.Query.Direction.ASCENDING
            )
            .get()
            .addOnSuccessListener { result ->

                val menuIds = mutableListOf<String>()

                val orderDocs = result.documents

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

                                // jika sudah ada jangan ditambahkan lagi
                                if (menuIds.contains(menuId))
                                    return@forEach

                                // maksimal 3 menu
                                if (menuIds.size >= 3) {

                                    // hapus menu PALING LAMA
                                    menuIds.removeAt(0)
                                }

                                menuIds.add(menuId)
                            }

                            processed++

                            if (processed == orderDocs.size) {

                                onResult(
                                    menuIds.reversed()
                                )
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getActiveOrder(
        userId: String,
        onResult: (Order?) -> Unit
    ) {

        firestore.collection("orders")
            .whereEqualTo("userID", userId)
            .whereEqualTo("completed", false)
            .whereIn(
                "status",
                listOf(
                    "pending",
                    "diproses",
                    "siap_diambil",
                    "selesai"
                )
            )
            .limit(1)
            .get()
            .addOnSuccessListener { result ->

                val document =
                    result.documents.firstOrNull()

                val order =
                    document?.toObject(Order::class.java)
                        ?.copy(id = document.id)

                onResult(order)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun completeOrder(
        orderId: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .update(
                mapOf(
                    "completed" to true,
                    "status" to "selesai"
                )
            )
            .addOnSuccessListener {

                onSuccess()
            }
    }

    fun getOrderItems(
        orderId: String,
        onResult: (List<OrderItem>) -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .collection("items")
            .get()
            .addOnSuccessListener { result ->

                val items =
                    result.toObjects(OrderItem::class.java)

                items.forEach {

                    Log.d(
                        "ORDER_ITEM_DEBUG",
                        """
                    menu_id = ${it.menu_id}
                    nama    = ${it.nama}
                    harga   = ${it.harga}
                    """.trimIndent()
                    )
                }

                onResult(items)
            }
    }

    fun observeOrderStatus(
        orderId: String,
        onChanged: (String) -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null && snapshot.exists()) {

                    val status =
                        snapshot.getString("status")
                            ?: "pending"

                    onChanged(status)
                }
            }
    }

    fun updateOrderStatus(
        orderId: String,
        status: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .update(
                "status",
                status
            )
            .addOnSuccessListener {
                onSuccess()
            }
    }

    fun cancelOrder(
        orderId: String,
        onSuccess: () -> Unit
    ) {

        firestore.collection("orders")
            .document(orderId)
            .update(
                mapOf(
                    "status" to "cancel"
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
    }

}