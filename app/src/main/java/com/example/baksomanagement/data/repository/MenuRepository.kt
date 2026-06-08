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

    fun searchMenu(
        keyword: String,
        onResult: (List<Menu>) -> Unit
    ) {
        firestore.collection("bakso")
            .get()
            .addOnSuccessListener { result ->

                val menuList = result.documents.mapNotNull { doc ->
                    doc.toObject(Menu::class.java)?.copy(id = doc.id)
                }

                val filteredList = menuList.filter {
                    it.namaMenu.contains(keyword, ignoreCase = true)
                }

                onResult(filteredList)
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