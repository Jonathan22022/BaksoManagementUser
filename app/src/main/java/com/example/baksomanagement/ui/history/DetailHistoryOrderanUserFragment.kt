package com.example.baksomanagement.ui.history

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.cart.CartManager

class DetailHistoryOrderanUserFragment :
    Fragment(
        R.layout.fragment_detail_history_orderan_user
    ) {

    private lateinit var rvHistoryItems:
            RecyclerView

    private lateinit var btnPesanLagi:
            Button

    private val repository =
        OrderRepository()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        rvHistoryItems =
            view.findViewById(
                R.id.rvHistoryItems
            )

        btnPesanLagi =
            view.findViewById(
                R.id.btnPesanLagi
            )

        rvHistoryItems.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        val orderId =
            arguments?.getString(
                "ORDER_ID"
            ) ?: return

        loadItems(orderId)

        btnPesanLagi.setOnClickListener {

            repeatOrder(orderId)
        }
    }

    private fun loadItems(
        orderId: String
    ) {

        repository.getOrderItems(
            orderId
        ){ items ->

            rvHistoryItems.adapter =
                DetailHistoryOrderanUserAdapter(
                    items
                )
        }
    }

    private fun repeatOrder(
        orderId: String
    ) {

        repository.getOrderItems(
            orderId
        ){ items ->

            CartManager.clear()

            CartManager.addItems(items)

            Toast.makeText(
                requireContext(),
                "Menu berhasil ditambahkan ke checkout",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigate(
                R.id.action_detailHistoryOrderanUserFragment_to_checkoutFragment
            )
        }
    }
}