package com.example.baksomanagement.data.model

data class User(
    val userId: String = "",
    val nama: String = "",
    val email: String = "",
    val noTelp: String = "",
    val role: String = "user",
    val profilePicture: String = "ic_account_",
    //default profile picture di res/drawable/ic_account_.xml
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)