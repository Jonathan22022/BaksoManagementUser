package com.example.baksomanagement.ui.checkout

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.cart.CartManager
import com.example.baksomanagement.ui.orderStatus.OrderSessionManager
import com.example.baksomanagement.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth

class CheckoutFragment : Fragment() {

    companion object {
        private const val TAG = "CheckoutDebug"
    }

    private lateinit var rvCheckout: RecyclerView
    private lateinit var tvGrandTotal: TextView
    private lateinit var btnTambahPesanan: Button
    private lateinit var btnCheckout: Button

    private val orderRepository = OrderRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView")

        val view = inflater.inflate(
            R.layout.fragment_checkout,
            container,
            false
        )

        rvCheckout = view.findViewById(R.id.rvCheckout)
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal)

        btnTambahPesanan =
            view.findViewById(R.id.btnTambahPesanan)

        btnCheckout =
            view.findViewById(R.id.btnCheckout)

        btnTambahPesanan.setOnClickListener {

            findNavController().navigate(
                R.id.action_checkoutFragment_to_menuFragment
            )
        }

        btnCheckout.setOnClickListener {

            Log.d(TAG, "Button Checkout ditekan")
            showConfirmationDialog()
        }

        rvCheckout.layoutManager =
            LinearLayoutManager(requireContext())

        requestNotificationPermission()

        loadCheckoutItems()

        return view
    }

    private fun loadCheckoutItems() {

        Log.d(TAG, "loadCheckoutItems()")

        val cartItems = CartManager.items

        btnCheckout.visibility =
            if (cartItems.isNotEmpty())
                View.VISIBLE
            else
                View.GONE

        Log.d(
            TAG,
            "Jumlah item dalam cart = ${cartItems.size}"
        )

        cartItems.forEachIndexed { index, item ->

            val addonPrice =
                item.addons.sumOf { it.price }

            Log.d(
                TAG,
                """
                Item #$index
                Menu ID = ${item.menu_id}
                Nama = ${item.nama}
                Harga = ${item.harga}
                Qty = ${item.quantity}
                Addon Count = ${item.addons.size}
                Addon Total = $addonPrice
                """.trimIndent()
            )
        }

        val adapter = CheckoutAdapter(
            cartItems,
            onEditClick = { position ->

                Log.d(
                    TAG,
                    "Edit item position = $position"
                )

                val selectedItem =
                    cartItems[position]

                val bundle = Bundle().apply {

                    putString(
                        "MENU_ID",
                        selectedItem.menu_id
                    )

                    putInt(
                        "EDIT_POSITION",
                        position
                    )
                }

                findNavController().navigate(
                    R.id.action_checkoutFragment_to_detailMenuFragment,
                    bundle
                )
            }
        )

        rvCheckout.adapter = adapter

        val grandTotal =
            cartItems.sumOf { item ->
                (
                        item.harga +
                                item.addons.sumOf { it.price }
                        ) * item.quantity
            }

        tvGrandTotal.text = "Rp $grandTotal"

        Log.d(
            TAG,
            "Grand Total = $grandTotal"
        )
    }

    private fun showConfirmationDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Order")
            .setMessage("Apakah orderan sudah sesuai?")
            .setPositiveButton("Ya") { _, _ ->

                val userId =
                    FirebaseAuth.getInstance()
                        .currentUser?.uid ?: "guest"

                val cartItems = CartManager.items

                val total =
                    cartItems.sumOf {
                        (
                                it.harga +
                                        it.addons.sumOf { addon ->
                                            addon.price
                                        }
                                ) * it.quantity
                    }

                val order = Order(
                    userID = userId,
                    total = total,
                    status = "pending"
                )

                orderRepository.createOrder(
                    order,
                    cartItems
                ) {

                    OrderSessionManager.lastOrderItems =
                        cartItems.map { item ->
                            item.copy(
                                addons = item.addons.toList()
                            )
                        }

                    CartManager.clear()

                    requireActivity().runOnUiThread {

                        findNavController().navigate(
                            R.id.action_checkoutFragment_to_homepageFragment
                        )
                    }

                    if (
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        NotificationHelper.showOrderNotification(
                            requireContext()
                        )
                    }
                }
            }
            .setNegativeButton("Cek Lagi", null)
            .show()
    }

    private fun requestNotificationPermission() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    100
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }
}