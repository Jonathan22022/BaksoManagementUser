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
import com.example.baksomanagement.ui.cart.CartManager
import com.example.baksomanagement.ui.orderStatus.OrderSessionManager
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.example.baksomanagement.utils.NotificationHelper
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

        rvCheckout = view.findViewById(R.id.rvCheckout)
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal)

        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)

        rvCheckout.layoutManager = LinearLayoutManager(requireContext())
        requestNotificationPermission()
        loadCheckoutItems()

        btnCheckout.setOnClickListener {
            showConfirmationDialog()
        }

        return view
    }

    private fun loadCheckoutItems() {

        val cartItems = CartManager.items

        val adapter = CheckoutAdapter(
            cartItems,
            onEditClick = { position ->

                val selectedItem = cartItems[position]

                val bundle = Bundle().apply {
                    putString("MENU_ID", selectedItem.menu_id)
                }

                findNavController().navigate(
                    R.id.action_checkoutFragment_to_detailMenuFragment,
                    bundle
                )
            }
        )

        rvCheckout.adapter = adapter

        val grandTotal = cartItems.sumOf { item ->
            (item.harga + item.addons.sumOf { it.price }) * item.quantity
        }

        tvGrandTotal.text = "Rp $grandTotal"

        Log.e("CheckoutFragment", "Grand Total: $grandTotal")
    }

    private fun showConfirmationDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Order")
            .setMessage("Apakah orderan sudah sesuai?")
            .setPositiveButton("Ya") { _, _ ->

                val userId =
                    FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

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

                    OrderSessionManager.lastOrderItems =
                        cartItems.toMutableList()

                    CartManager.clear()

                    requireActivity().runOnUiThread {

                        findNavController().navigate(
                            R.id.action_checkoutFragment_to_homepageFragment
                        )
                    }

                    Log.e("CheckoutFragment", "Order berhasil dibuat")
                    //tambahkan notification yang akan muncul di hp setelah menekan orderan sesuai dan di notificatiopn akan berisi orderan telah diterima masuk
                    if (
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        NotificationHelper.showOrderNotification(requireContext())
                    }
                }
            }
            .setNegativeButton("Cek lagi", null)
            .show()
    }

    private fun requestNotificationPermission() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}