package com.example.baksomanagement.ui.orderStatus

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.repository.OrderRepository

class StatusOrderanFragment :
    Fragment(R.layout.fragment_status_orderan) {
    private lateinit var btnPesanLagi: Button
    private lateinit var rvStatus: RecyclerView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvGrandTotal: TextView
    private lateinit var btnSelesai: Button
    private lateinit var btnCancelOrder: Button

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

        rvStatus =
            view.findViewById(R.id.rvStatus)

        tvOrderStatus =
            view.findViewById(R.id.tvOrderStatus)

        tvGrandTotal =
            view.findViewById(R.id.tvGrandTotal)

        btnSelesai =
            view.findViewById(
                R.id.btnSelesaikanPesanan
            )

        btnPesanLagi =
            view.findViewById(
                R.id.btnPesanLagi
            )

        btnCancelOrder =
            view.findViewById(
                R.id.btnCancelOrder
            )

        rvStatus.layoutManager =
            LinearLayoutManager(requireContext())

        loadOrder()
    }

    private fun loadOrder() {

        val orderId =
            OrderSessionManager.lastOrderId

        if (orderId == null) {

            tvOrderStatus.text =
                "Tidak ada pesanan aktif"

            return
        }

        repository.getOrderById(orderId) { order ->

            if (order == null) return@getOrderById

            tvGrandTotal.text =
                "Rp ${order.total}"

            updateStatus(order.status)

            repository.observeOrderStatus(
                orderId
            ) { status ->

                updateStatus(status)

                if (status == "siap_diambil") {

                    btnSelesai.visibility =
                        View.VISIBLE
                }
            }
        }

        repository.getOrderItems(orderId) { items ->

            rvStatus.adapter =
                OrderStatusAdapter(items)
        }

        btnSelesai.setOnClickListener {

            repository.completeOrder(orderId) {

                OrderSessionManager.lastOrderId = null

                findNavController().navigate(
                    R.id.action_statusOrderanFragment_to_homepageFragment
                )
            }
        }

        btnPesanLagi.setOnClickListener {

            findNavController().navigate(
                R.id.action_statusOrderanFragment_to_menuFragment
            )
        }

        btnCancelOrder.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setTitle("Batalkan Pesanan")
                .setMessage(
                    "Yakin ingin membatalkan pesanan?"
                )
                .setPositiveButton("Ya") { _, _ ->

                    repository.cancelOrder(
                        orderId
                    ) {

                        OrderSessionManager.lastOrderId =
                            null

                        Toast.makeText(
                            requireContext(),
                            "Anda telah mengcancel orderan",
                            Toast.LENGTH_LONG
                        ).show()

                        findNavController().navigate(
                            R.id.action_statusOrderanFragment_to_homepageFragment
                        )
                    }
                }
                .setNegativeButton(
                    "Tidak",
                    null
                )
                .show()
        }
    }

    private fun updateStatus(
        status: String
    ) {

        when(status){

            "pending" -> {
                tvOrderStatus.text =
                    "● Pesanan Diterima"
            }

            "diproses" -> {
                tvOrderStatus.text =
                    "● Sedang Diproses"
            }

            "siap_diambil" -> {

                tvOrderStatus.text =
                    "● Siap Diambil"

                btnSelesai.visibility =
                    View.VISIBLE
            }

            "cancel" -> {

                tvOrderStatus.text =
                    "Pesanan Dibatalkan"

                btnSelesai.visibility =
                    View.GONE

                btnPesanLagi.visibility =
                    View.VISIBLE

                tvOrderStatus.append(
                    "\n\nCoba pesan menu lainnya 😊"
                )
            }

            "selesai" -> {

                tvOrderStatus.text =
                    "Pesanan Selesai"

                btnSelesai.visibility =
                    View.GONE
            }
        }
    }
}