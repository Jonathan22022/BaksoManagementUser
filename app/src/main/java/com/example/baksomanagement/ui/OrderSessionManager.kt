package com.example.baksomanagement.ui

import com.example.baksomanagement.data.model.OrderItem

object OrderSessionManager {
    var lastOrderId: String? = null
    var lastOrderItems: List<OrderItem> = emptyList()
}