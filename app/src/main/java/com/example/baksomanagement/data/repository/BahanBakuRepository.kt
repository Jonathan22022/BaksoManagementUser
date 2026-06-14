package com.example.baksomanagement.data.repository

import android.util.Log
import com.example.baksomanagement.data.model.BahanBaku
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject

class BahanBakuRepository {

    private val firestore = FirebaseClient.firestore

    fun getBahanById(
        id: String,
        onResult: (BahanBaku?) -> Unit
    ) {

        firestore.collection("bahanbaku")
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

    fun getAllBahan(
        onResult: (List<BahanBaku>) -> Unit
    ) {

        firestore.collection("bahanbaku")
            .get()
            .addOnSuccessListener { result ->

                Log.d(
                    "BAHAN_DEBUG",
                    "Document count=${result.size()}"
                )

                val list =
                    result.documents.mapNotNull {
                        it.toObject(BahanBaku::class.java)
                            ?.copy(id = it.id)
                    }

                onResult(list)
            }
            .addOnFailureListener {

                Log.e(
                    "BAHAN_DEBUG",
                    "GET BAHAN GAGAL",
                    it
                )

                onResult(emptyList())
            }
    }
}