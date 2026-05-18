package com.example.baksomanagement.data.repository

import com.example.baksomanagement.data.model.BahanBaku
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject

class BahanBakuRepository {

    private val firestore = FirebaseClient.firestore

    fun getBahanById(
        id: String,
        onResult: (BahanBaku?) -> Unit
    ) {

        firestore.collection("bahan_baku")
            .document(id)
            .get()
            .addOnSuccessListener {

                val bahan =
                    it.toObject(BahanBaku::class.java)

                onResult(bahan)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}