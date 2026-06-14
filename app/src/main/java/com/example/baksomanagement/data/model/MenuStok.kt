package com.example.baksomanagement.data.model

data class MenuStok(
    val menu: Menu,
    val tersedia: Boolean,
    val kekurangan: List<KekuranganBahan>
)