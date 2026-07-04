package com.example.baksomanagement.ui.orderStatus

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import com.example.baksomanagement.data.remote.FirebaseClient
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class StatusOrderanFragment :
    Fragment(R.layout.fragment_status_orderan) {

    companion object {
        private const val TAG = "StatusOrderanDebug"
    }

    private lateinit var btnPesanLagi: Button
    private lateinit var rvStatus: RecyclerView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvGrandTotal: TextView
    private lateinit var btnSelesai: Button
    private lateinit var btnCancelOrder: Button
    private lateinit var tvJarakWaktu: TextView
    private val repository = OrderRepository()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        Log.d(TAG, "===== onViewCreated() DIPANGGIL =====")

        rvStatus = view.findViewById(R.id.rvStatus)
        tvOrderStatus = view.findViewById(R.id.tvOrderStatus)
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal)
        tvJarakWaktu = view.findViewById(R.id.tvJarakWaktu)
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

        Log.d(TAG, "Semua view berhasil di-bind, memanggil loadOrder()")

        loadOrder()
    }

    private fun loadOrder() {

        val orderId =
            OrderSessionManager.lastOrderId

        Log.d(TAG, "loadOrder() dipanggil, orderId dari OrderSessionManager = $orderId")

        if (orderId == null) {

            Log.e(TAG, "orderId NULL, tidak ada pesanan aktif")

            tvOrderStatus.text =
                "Tidak ada pesanan aktif"

            return
        }

        repository.getOrderById(orderId) { order ->

            Log.d(TAG, "===== CALLBACK getOrderById() =====")

            if (order == null) {
                Log.e(TAG, "Order NULL untuk orderId = $orderId")
                return@getOrderById
            }

            Log.d(
                TAG,
                """
                DATA ORDER DITERIMA
                ------------------------
                id          = ${order.id}
                status      = ${order.status}
                pickupType  = ${order.pickupType}
                total       = ${order.total}
                latitude    = ${order.latitude}
                longitude   = ${order.longitude}
                ------------------------
                """.trimIndent()
            )

            tvGrandTotal.text =
                "Rp ${order.total}"

            updateStatus(order.status)

            if (order.pickupType == "delivery" &&
                order.latitude != 0.0 &&
                order.longitude != 0.0
            ) {

                Log.d(
                    TAG,
                    "Kondisi delivery terpenuhi, memanggil tampilkanEstimasiDelivery(" +
                            "lat=${order.latitude}, lng=${order.longitude})"
                )

                tampilkanEstimasiDelivery(
                    order.latitude,
                    order.longitude
                )
            } else {

                Log.e(
                    TAG,
                    "Kondisi delivery TIDAK terpenuhi -> pickupType=${order.pickupType}, " +
                            "latitude=${order.latitude}, longitude=${order.longitude}. " +
                            "tvJarakWaktu disembunyikan."
                )

                tvJarakWaktu.visibility = View.GONE
            }

            repository.observeOrderStatus(
                orderId
            ) { status ->

                Log.d(TAG, "observeOrderStatus -> status berubah menjadi: $status")

                updateStatus(status)

                if (status == "siap_diambil") {

                    btnSelesai.visibility =
                        View.VISIBLE
                }
            }
        }

        repository.getOrderItems(orderId) { items ->

            Log.d(TAG, "getOrderItems() -> jumlah item = ${items.size}")

            rvStatus.adapter =
                OrderStatusAdapter(items)
        }

        btnSelesai.setOnClickListener {

            Log.d(TAG, "btnSelesai diklik, orderId=$orderId")

            repository.completeOrder(orderId) {

                Log.d(TAG, "completeOrder SUKSES, navigasi ke homepage")

                OrderSessionManager.lastOrderId = null

                findNavController().navigate(
                    R.id.action_statusOrderanFragment_to_homepageFragment
                )
            }
        }

        btnPesanLagi.setOnClickListener {

            Log.d(TAG, "btnPesanLagi diklik, navigasi ke menuFragment")

            findNavController().navigate(
                R.id.action_statusOrderanFragment_to_menuFragment
            )
        }

        btnCancelOrder.setOnClickListener {

            Log.d(TAG, "btnCancelOrder diklik, orderId=$orderId")

            AlertDialog.Builder(requireContext())
                .setTitle("Batalkan Pesanan")
                .setMessage(
                    "Yakin ingin membatalkan pesanan?"
                )
                .setPositiveButton("Ya") { _, _ ->

                    Log.d(TAG, "Konfirmasi cancel = Ya, memanggil cancelOrder($orderId)")

                    repository.cancelOrder(
                        orderId
                    ) {

                        Log.d(TAG, "cancelOrder SUKSES, navigasi ke homepage")

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
                    "Tidak"
                ) { _, _ ->
                    Log.d(TAG, "Konfirmasi cancel = Tidak, dibatalkan")
                }
                .show()
        }
    }

    private fun updateStatus(
        status: String
    ) {

        Log.d(TAG, "updateStatus() dipanggil dengan status = $status")

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

            "dalam_perjalanan" -> {

                tvOrderStatus.text =
                    "● Kurir sedang menuju alamat Anda"

            }

            "sampai_tujuan" -> {

                tvOrderStatus.text =
                    "● Pesanan telah sampai"

                btnSelesai.visibility =
                    View.VISIBLE

            }

            "selesai" -> {

                tvOrderStatus.text =
                    "Pesanan Selesai"

                btnSelesai.visibility =
                    View.GONE
            }

            else -> {
                Log.e(TAG, "Status tidak dikenali oleh when-block: $status")
            }
        }
    }

    private fun hitungJarakKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {

        val R = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(lat1)) *
                    cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val hasil = R * c

        Log.d(
            TAG,
            "hitungJarakKm(lat1=$lat1, lon1=$lon1, lat2=$lat2, lon2=$lon2) = $hasil km"
        )

        return hasil
    }

    private fun estimasiWaktuMenit(jarakKm: Double): Int {

        val kecepatanRataRataKmPerJam = 30.0
        val jam = jarakKm / kecepatanRataRataKmPerJam

        val hasil = (jam * 60).toInt().coerceAtLeast(1)

        Log.d(
            TAG,
            "estimasiWaktuMenit(jarakKm=$jarakKm) = $hasil menit"
        )

        return hasil
    }

    private fun tampilkanEstimasiDelivery(
        orderLat: Double,
        orderLng: Double
    ) {

        Log.d(
            TAG,
            "tampilkanEstimasiDelivery() dipanggil dengan orderLat=$orderLat, orderLng=$orderLng"
        )

        FirebaseClient.firestore.collection("users")
            .whereEqualTo("role", "admin")
            .limit(1)
            .get()
            .addOnSuccessListener { result ->

                Log.d(
                    TAG,
                    "Query users where role=admin SUKSES, jumlah dokumen = ${result.documents.size}"
                )

                val adminDoc = result.documents.firstOrNull()

                val adminLat = adminDoc?.getDouble("latitude") ?: 0.0
                val adminLng = adminDoc?.getDouble("longitude") ?: 0.0

                Log.d(
                    TAG,
                    "adminDoc id=${adminDoc?.id}, adminLat=$adminLat, adminLng=$adminLng"
                )

                if (adminDoc == null || (adminLat == 0.0 && adminLng == 0.0)) {

                    Log.e(
                        TAG,
                        "Lokasi admin tidak valid -> adminDoc null? ${adminDoc == null}, " +
                                "adminLat=$adminLat, adminLng=$adminLng"
                    )

                    tvJarakWaktu.text =
                        "Estimasi Jarak : Lokasi toko belum diatur"

                    tvJarakWaktu.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                val jarakKm = hitungJarakKm(
                    adminLat, adminLng,
                    orderLat, orderLng
                )

                val menit = estimasiWaktuMenit(jarakKm)

                Log.d(
                    TAG,
                    "HASIL AKHIR -> jarakKm=$jarakKm, estimasiMenit=$menit"
                )

                tvJarakWaktu.text =
                    "Estimasi Jarak : %.1f km (± %d menit)".format(
                        jarakKm,
                        menit
                    )

                tvJarakWaktu.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Query users where role=admin GAGAL: ${e.message}", e)

                tvJarakWaktu.text =
                    "Estimasi Jarak : Gagal memuat data toko"

                tvJarakWaktu.visibility = View.VISIBLE
            }
    }
}