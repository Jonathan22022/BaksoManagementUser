package com.example.baksomanagement.ui.cart

import com.example.baksomanagement.data.model.OrderItem

object CartManager {

    val items = mutableListOf<OrderItem>()

    fun addItem(item: OrderItem) {
        items.add(item)
    }

    fun getTotalQuantity(): Int {
        return items.sumOf { it.quantity }
    }

    fun clear() {
        items.clear()
    }
}