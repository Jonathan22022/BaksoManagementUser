package com.example.baksomanagement.data.model

data class PaymentResponse(
    val success:Boolean,
    val message:String,
    val data:PaymentData
)

data class PaymentData(
    val transactionId:String,
    val bank:String,
    val vaNumber:String,
    val expiryTime:String
)