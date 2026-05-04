package com.example.baksomanagement.data.repository


import android.os.Bundle
import com.example.baksomanagement.data.model.AddOn
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject

class AddOnRepository {

    private val firestore = FirebaseClient.firestore

    fun getAddOnList(onResult: (List<AddOn>) -> Unit) {
        firestore.collection("addons")
            .get()
            .addOnSuccessListener { result ->
                val addOnList = result.documents.mapNotNull { doc ->
                    val addon = doc.toObject<AddOn>()
                    addon?.copy(id = doc.id)
                }
                onResult(addOnList)
            }
    }

    fun getAddOnById(id: String, onResult: (AddOn?) -> Unit) {
        firestore.collection("addons").document(id)
            .get()
            .addOnSuccessListener {
                val addon = it.toObject(AddOn::class.java)?.copy(id = it.id)
                onResult(addon)
            }
    }
}