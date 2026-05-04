package com.example.baksomanagement.data.repository

import android.os.Bundle
import com.example.baksomanagement.data.model.Menu
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject

class MenuRepository {

    private val firestore = FirebaseClient.firestore

    fun getMenuList(onResult: (List<Menu>) -> Unit) {
        firestore.collection("bakso")
            .get()
            .addOnSuccessListener { result ->
                val menuList = result.documents.mapNotNull { doc ->
                    val menu = doc.toObject<Menu>()
                    menu?.copy(id = doc.id)
                }
                onResult(menuList)
            }
    }


    fun getMenuById(id: String, onResult: (Menu?) -> Unit) {
        firestore.collection("bakso").document(id)
            .get()
            .addOnSuccessListener {
                val menu = it.toObject(Menu::class.java)?.copy(id = it.id)
                onResult(menu)
            }
    }
}