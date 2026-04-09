package com.example.baksomanagement.data.repository

import com.example.baksomanagement.data.model.Menu
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.firestore.ktx.toObject

class MenuRepository {

    private val firestore = FirebaseClient.firestore

    fun getMenuList(onResult: (List<Menu>) -> Unit) {

        firestore.collection("Menu")
            .get()
            .addOnSuccessListener { result ->

                val menuList = result.documents.mapNotNull { doc ->
                    val menu = doc.toObject<Menu>()
                    menu?.copy(id = doc.id)
                }

                onResult(menuList)
            }
    }
}