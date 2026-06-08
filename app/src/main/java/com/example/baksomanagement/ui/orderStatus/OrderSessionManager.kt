package com.example.baksomanagement.ui.orderStatus

import android.util.Log
import com.example.baksomanagement.data.model.OrderItem

object OrderSessionManager {

    private const val TAG = "OrderSessionManager"

    var lastOrderId: String? = null

    var lastOrderItems: List<OrderItem> =
        emptyList()

    fun printDebug() {

        Log.e(
            TAG,
            "lastOrderId = $lastOrderId"
        )

        Log.e(
            TAG,
            "total item = ${lastOrderItems.size}"
        )

        lastOrderItems.forEachIndexed { index, item ->

            Log.e(
                TAG,
                """
                Item #$index
                nama=${item.nama}
                qty=${item.quantity}
                """.trimIndent()
            )
        }
    }
}