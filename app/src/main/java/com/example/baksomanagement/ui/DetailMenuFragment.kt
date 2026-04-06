package com.example.baksomanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.baksomanagement.R

class DetailMenuFragment : Fragment() {

    private var quantity = 1
    private val basePrice = 28000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_detail_menu, container, false)
        val tvQty = view.findViewById<TextView>(R.id.tvQty)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotal)
        val btnPlus = view.findViewById<Button>(R.id.btnPlus)
        val btnMinus = view.findViewById<Button>(R.id.btnMinus)
        val btnOrder = view.findViewById<Button>(R.id.btnOrder)

        // SET DEFAULT TOTAL
        tvTotal.text = "Rp. ${basePrice}"

        // BUTTON PLUS
        btnPlus.setOnClickListener {
            quantity++
            tvQty.text = quantity.toString()
            val total = quantity * basePrice
            tvTotal.text = "Rp. $total"
        }

        // BUTTON MINUS
        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQty.text = quantity.toString()
                val total = quantity * basePrice
                tvTotal.text = "Rp. $total"
            }
        }

        // BUTTON ORDER
        btnOrder.setOnClickListener {
            // nanti bisa dihubungkan ke cart atau order page
            android.widget.Toast.makeText(
                requireContext(),
                "Pesanan ditambahkan",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        return view
    }
}