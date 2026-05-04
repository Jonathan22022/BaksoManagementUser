package com.example.baksomanagement.ui.checkout

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.checkout.CheckoutAdapter
import com.example.baksomanagement.ui.OrderSessionManager
import com.example.baksomanagement.ui.cart.CartManager
import com.google.firebase.auth.FirebaseAuth

class CheckoutFragment : Fragment() {

    private lateinit var rvCheckout: RecyclerView
    private lateinit var tvGrandTotal: TextView
    private val orderRepository = OrderRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_checkout, container, false)

        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)

        btnCheckout.setOnClickListener {
            showConfirmationDialog()
            Log.e("CheckoutFragment", "Tombol Checkout ditekan")
        }

        rvCheckout = view.findViewById(R.id.rvCheckout)
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal)

        rvCheckout.layoutManager = LinearLayoutManager(requireContext())

        val cartItems = CartManager.items

        val adapter = CheckoutAdapter(cartItems)
        rvCheckout.adapter = adapter

        val grandTotal = cartItems.sumOf { item ->
            (item.harga + item.addons.sumOf { it.price }) * item.quantity
            Log.e("CheckoutFragment", "Harga item: ${item.harga}")
        }

        tvGrandTotal.text = "Rp $grandTotal"

        return view
    }

    private fun showConfirmationDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Order")
            .setMessage("Apakah orderan sudah sesuai?")
            .setPositiveButton("Ya") { _, _ ->
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
                val cartItems = CartManager.items
                val total = cartItems.sumOf {
                    (it.harga + it.addons.sumOf { a -> a.price }) * it.quantity
                }
                val order = Order(
                    userID = userId,
                    total = total,
                    status = "pending"
                )
                orderRepository.createOrder(order, cartItems) {
                    OrderSessionManager.lastOrderItems = cartItems
                    CartManager.clear()
                    requireActivity().runOnUiThread {
                        findNavController().navigate(R.id.action_checkoutFragment_to_homepageFragment)
                    }
                    Log.e("CheckoutFragment", "Order berhasil dibuat")
                }
            }
            .setNegativeButton("Cek lagi", null)
            .show()
    }
}