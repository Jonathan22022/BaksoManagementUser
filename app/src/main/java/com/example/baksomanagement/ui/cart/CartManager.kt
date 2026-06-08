package com.example.baksomanagement.ui.cart

import android.util.Log
import com.example.baksomanagement.data.model.OrderItem

object CartManager {

    val items = mutableListOf<OrderItem>()

    fun addItem(item: OrderItem) {
        items.add(item)
    }

    fun updateItem(
        position: Int,
        item: OrderItem
    ) {
        if (
            position >= 0 &&
            position < items.size
        ) {
            items[position] = item
        }
    }

    fun getTotalQuantity(): Int {
        return items.sumOf {
            it.quantity
        }
    }

    fun clear() {
        items.clear()
    }
}