package com.example.baksomanagement.data.model

data class SnapResponse(

    val success: Boolean,

    val message: String,

    val data: SnapData

)

data class SnapData(

    val token: String,

    val redirectUrl: String

)