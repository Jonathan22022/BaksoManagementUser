package com.example.baksomanagement.ui.orderStatus

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R

class StatusOrderanFragment : Fragment(R.layout.fragment_status_orderan) {

    companion object{
        private const val TAG = "StatusOrderanFragment"
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        Log.e(TAG, "onViewCreated()")

        val rv =
            view.findViewById<RecyclerView>(R.id.rvStatus)

        rv.layoutManager =
            LinearLayoutManager(requireContext())

        val data =
            OrderSessionManager.lastOrderItems

        Log.e(
            TAG,
            "Jumlah item order = ${data.size}"
        )

        val tvGrandTotal =
            view.findViewById<TextView>(R.id.tvGrandTotal)

        val grandTotal =
            data.sumOf { item ->
                (
                        item.harga +
                                item.addons.sumOf { it.price }
                        ) * item.quantity
            }

        data.forEachIndexed { index, item ->

            Log.e(
                TAG,
                """
                Item #$index
                menu_id=${item.menu_id}
                nama=${item.nama}
                qty=${item.quantity}
                image=${item.imageUrl}
                """.trimIndent()
            )
        }

        rv.adapter =
            OrderStatusAdapter(data)

        Log.e(
            TAG,
            "Adapter berhasil dipasang"
        )

        tvGrandTotal.text =
            "Rp ${String.format("%,d", grandTotal).replace(',', '.')}"
    }
}