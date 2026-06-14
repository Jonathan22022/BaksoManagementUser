package com.example.baksomanagement.data.model

data class AddOnStok(
    val addon: AddOn,
    val tersedia: Boolean,
    val kekurangan: List<KekuranganBahan>
)
