package com.example.baksomanagement.data.repository

import com.example.baksomanagement.data.model.Favourite
import com.example.baksomanagement.data.remote.FirebaseClient
import com.google.firebase.auth.FirebaseAuth

class FavouriteRepository {

    private val firestore = FirebaseClient.firestore
    private val auth = FirebaseAuth.getInstance()

    fun addFavourite(
        favourite: Favourite,
        onResult: (Boolean) -> Unit
    ) {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("favourites")
            .document(favourite.menuId)
            .set(favourite)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun removeFavourite(
        menuId: String,
        onResult: (Boolean) -> Unit
    ) {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("favourites")
            .document(menuId)
            .delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun isFavourite(
        menuId: String,
        onResult: (Boolean) -> Unit
    ) {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("favourites")
            .document(menuId)
            .get()
            .addOnSuccessListener {
                onResult(it.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getFavouriteList(
        onResult: (List<Favourite>) -> Unit
    ) {

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("favourites")
            .get()
            .addOnSuccessListener { result ->

                val list = result.documents.mapNotNull {
                    it.toObject(Favourite::class.java)
                }

                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun getTopFavouriteMenus(
        onResult: (List<String>) -> Unit
    ) {

        firestore.collectionGroup("favourites")
            .get()
            .addOnSuccessListener { result ->

                val countMap = mutableMapOf<String, Int>()

                result.documents.forEach { doc ->

                    val menuId =
                        doc.getString("menuId") ?: return@forEach

                    countMap[menuId] =
                        (countMap[menuId] ?: 0) + 1
                }

                val top3Ids = countMap
                    .toList()
                    .sortedByDescending { it.second }
                    .take(3)
                    .map { it.first }

                onResult(top3Ids)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}