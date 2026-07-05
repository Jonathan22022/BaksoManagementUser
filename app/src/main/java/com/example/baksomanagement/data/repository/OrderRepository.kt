package com.example.baksomanagement.data.repository

import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.model.OrderItem
import com.example.baksomanagement.data.remote.FirebaseClient
import android.util.Log
import com.example.baksomanagement.data.model.PaymentRequest
import com.example.baksomanagement.data.model.PaymentResponse
import com.example.baksomanagement.data.model.SnapResponse
import com.example.baksomanagement.data.remote.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        onSuccess: (String, String) -> Unit
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

        val dummyTransactionId = "DUMMY-${orderRef.id}"

        val dummyQrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=$dummyTransactionId"

        val orderData = hashMapOf(
            "userID" to order.userID,
            "createdAt" to order.createdAt,
            "status" to order.status,
            "total" to order.total,
            "completed" to false,
            "pickupType" to order.pickupType,
            "deliveryAddress" to order.deliveryAddress,
            "latitude" to order.latitude,
            "longitude" to order.longitude,
            "paymentStatus" to "waiting",
            "qrUrl" to dummyQrUrl,
            "transactionId" to dummyTransactionId
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

                    onSuccess(orderRef.id, dummyQrUrl)
                }
            }
    }

    fun confirmDummyPayment(
        orderId: String,
        onSuccess: () -> Unit,
        onFailed: (String) -> Unit
    ) {

        Log.d("DummyPayment", "Konfirmasi pembayaran dummy untuk orderId=$orderId")

        firestore.collection("orders")
            .document(orderId)
            .update(
                mapOf(
                    "paymentStatus" to "success",
                    "status" to "diproses"
                )
            )
            .addOnSuccessListener {

                Log.d("DummyPayment", "Update paymentStatus=success & status=diproses BERHASIL")
                onSuccess()
            }
            .addOnFailureListener { e ->

                Log.e("DummyPayment", "GAGAL update pembayaran dummy: ${e.message}")
                onFailed(e.message ?: "Gagal update status pembayaran")
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
                    "dalam_perjalanan",
                    "sampai_tujuan",
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

    fun createSnapPayment(

        orderId: String,

        total: Int,

        onSuccess: (String) -> Unit,

        onFailed: (String) -> Unit

    ) {
        Log.d("SnapDebug", "===================================")
        Log.d("SnapDebug", "POST /api/snap/create")
        Log.d("SnapDebug", "OrderId = $orderId")
        Log.d("SnapDebug", "Total = $total")
        Log.d("SnapDebug", "===================================")

        val request = PaymentRequest(
            orderId,
            total
        )

        ApiClient.api
            .createSnap(request)
            .enqueue(

                object : Callback<SnapResponse> {

                    override fun onResponse(

                        call: Call<SnapResponse>,

                        response: Response<SnapResponse>

                    ) {
                        Log.d("SnapDebug", "HTTP = ${response.code()}")
                        Log.d("SnapDebug", "Body = ${response.body()}")
                        Log.d("SnapDebug", "Error = ${response.errorBody()?.string()}")
                        if (response.isSuccessful) {

                            val body = response.body()

                            if (body != null) {

                                onSuccess(
                                    body.data.token
                                )

                            } else {

                                onFailed("Body kosong")

                            }

                        } else {

                            onFailed(response.message())

                        }

                    }

                    override fun onFailure(

                        call: Call<SnapResponse>,

                        t: Throwable

                    ) {
                        Log.e("SnapDebug", "NETWORK ERROR")
                        Log.e("SnapDebug", t.localizedMessage ?: "")
                        t.printStackTrace()
                        onFailed(
                            t.message ?: "Unknown"
                        )

                    }

                }

            )

    }
    /*fun createQrisPayment(
        orderId: String,
        total: Int,
        onSuccess: (String, String) -> Unit,
        onFailed: (String) -> Unit
    ) {

        val request = PaymentRequest(
            orderId,
            total
        )

        ApiClient.api
            .createQris(request)
            .enqueue(object : Callback<PaymentResponse> {

                override fun onResponse(
                    call: Call<PaymentResponse>,
                    response: Response<PaymentResponse>
                ) {

                    if (response.isSuccessful) {

                        val body = response.body()

                        if (body != null) {

                            onSuccess(
                                body.data.bank,
                                body.data.vaNumber
                            )

                        } else {

                            onFailed("Body null")
                        }

                    } else {

                        onFailed(response.message())
                    }

                }

                override fun onFailure(
                    call: Call<PaymentResponse>,
                    t: Throwable
                ) {

                    onFailed(
                        t.message ?: "Unknown Error"
                    )

                }

            })

    }
    */
}