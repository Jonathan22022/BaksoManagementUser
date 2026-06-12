package com.example.baksomanagement.data.model

data class History(

    val orderId: String = "",

    val nama: String = "",

    val imageUrl: String = "",

    val quantity: Int = 0,

    val total: Int = 0,

    val status: String = "",

    val createdAt: Long = System.currentTimeMillis(),
    var selected: Boolean = false
)