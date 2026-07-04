package com.example.baksomanagement.ui.checkout

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import com.bumptech.glide.Glide
import com.example.baksomanagement.data.remote.FirebaseClient
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Order
import com.example.baksomanagement.data.repository.OrderRepository
import com.example.baksomanagement.ui.PaymentDialogFragment
import com.example.baksomanagement.ui.cart.CartManager
import com.example.baksomanagement.ui.orderStatus.OrderSessionManager
import com.example.baksomanagement.utils.NotificationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.uikit.external.UiKitApi
import java.util.Locale

class CheckoutFragment : Fragment() {

    companion object {
        private const val TAG = "CheckoutDebug"
    }
    private var isCheckoutProcessing = false
    private lateinit var mapContainer: FragmentContainerView
    private lateinit var googleMap: GoogleMap
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var snapLauncher: ActivityResultLauncher<Intent>
    private lateinit var rvCheckout: RecyclerView
    private lateinit var tvGrandTotal: TextView
    private lateinit var btnTambahPesanan: Button
    private lateinit var btnCheckout: Button
    private lateinit var btnCurrentLocation: Button
    private lateinit var rgPickup: RadioGroup
    private lateinit var rbDineIn: RadioButton
    private lateinit var rbDelivery: RadioButton
    private lateinit var etAlamat: EditText
    private var selectedLatitude = 0.0
    private var selectedLongitude = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var pickupType: String = "dine_in"
    private var alamat: String = ""
    private val orderRepository = OrderRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                fetchAndFillCurrentLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Izin lokasi diperlukan untuk mengisi alamat otomatis",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        snapLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->

                Log.d(TAG, "================================")
                Log.d(TAG, "MIDTRANS SNAP CLOSED")
                Log.d(TAG, "Result Code = ${result.resultCode}")
                Log.d(TAG, "================================")

                if (result.data != null) {

                    Log.d(
                        TAG,
                        "Intent = ${result.data}"
                    )

                }
            }

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
        rgPickup = view.findViewById(R.id.rgPickup)
        rbDineIn = view.findViewById(R.id.rbDineIn)
        rbDelivery = view.findViewById(R.id.rbDelivery)
        etAlamat = view.findViewById(R.id.etAlamat)
        mapContainer = view.findViewById(R.id.mapContainer)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapContainer)
                    as SupportMapFragment

        mapFragment.getMapAsync { map ->

            googleMap = map

            val defaultLocation = LatLng(-6.2000, 106.8166)

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f)
            )

            googleMap.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .draggable(true)
            )

            selectedLatitude = defaultLocation.latitude
            selectedLongitude = defaultLocation.longitude

            if (etAlamat.text.isNullOrEmpty()) {
                getAddress(selectedLatitude, selectedLongitude)
            }

            googleMap.setOnMarkerDragListener(
                object : GoogleMap.OnMarkerDragListener {

                    override fun onMarkerDragEnd(marker: Marker) {
                        selectedLatitude = marker.position.latitude
                        selectedLongitude = marker.position.longitude
                        getAddress(selectedLatitude, selectedLongitude)
                    }

                    override fun onMarkerDrag(marker: Marker) {}
                    override fun onMarkerDragStart(marker: Marker) {}
                }
            )

            googleMap.setOnMarkerDragListener(
                object : GoogleMap.OnMarkerDragListener {

                    override fun onMarkerDragEnd(marker: Marker) {
                        selectedLatitude = marker.position.latitude
                        selectedLongitude = marker.position.longitude
                        getAddress(selectedLatitude, selectedLongitude)
                    }

                    override fun onMarkerDrag(marker: Marker) {}
                    override fun onMarkerDragStart(marker: Marker) {}
                }
            )
        }
        btnTambahPesanan = view.findViewById(R.id.btnTambahPesanan)
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        btnTambahPesanan.setOnClickListener {
            findNavController().navigate(
                R.id.action_checkoutFragment_to_menuFragment
            )
        }

        btnCheckout.setOnClickListener {

            Log.d(TAG, "Button Checkout ditekan")
            showConfirmationDialog()
        }

        rgPickup.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rbDelivery) {

                pickupType = "delivery"
                etAlamat.visibility = View.VISIBLE
                mapContainer.visibility = View.VISIBLE
                btnCurrentLocation.visibility = View.VISIBLE

                if (etAlamat.text.isNullOrEmpty()) {
                    loadSavedAddressForDelivery()
                }

            } else {

                pickupType = "dine_in"
                etAlamat.visibility = View.GONE
                mapContainer.visibility = View.GONE
                btnCurrentLocation.visibility = View.GONE
            }
        }

        alamat =
            if (pickupType == "delivery")
                etAlamat.text.toString()
            else
                ""

        btnCurrentLocation.setOnClickListener {
            loadCurrentLocation()
        }

        rvCheckout.layoutManager =
            LinearLayoutManager(requireContext())

        requestNotificationPermission()

        loadCheckoutItems()

        return view
    }

    private fun loadSavedAddressForDelivery() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseClient.firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->

                val savedAlamat = doc.getString("alamat")
                val savedLat = doc.getDouble("latitude")
                val savedLng = doc.getDouble("longitude")

                if (!savedAlamat.isNullOrEmpty() &&
                    savedLat != null && savedLng != null &&
                    (savedLat != 0.0 || savedLng != 0.0)
                ) {

                    etAlamat.setText(savedAlamat)
                    selectedLatitude = savedLat
                    selectedLongitude = savedLng

                    if (::googleMap.isInitialized) {
                        showMarker(savedLat, savedLng)
                    }
                }
            }
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

                Log.d(
                    "EDIT_DEBUG",
                    "selectedItem.menu_id = ${selectedItem.menu_id}"
                )

                Log.d(
                    "EDIT_DEBUG",
                    "position = $position"
                )

                findNavController().navigate(
                    R.id.action_checkoutFragment_to_detailMenuFragment,
                    bundle
                )
            },

            onDeleteClick = { position ->

                AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Pesanan")
                    .setMessage("Yakin ingin menghapus item ini?")
                    .setPositiveButton("Ya") { _, _ ->

                        CartManager.removeItem(position)

                        loadCheckoutItems()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
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
    private fun loadCurrentLocation() {

        val fineGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        fetchAndFillCurrentLocation()
    }

    private fun fetchAndFillCurrentLocation() {

        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    val lat = location.latitude
                    val lng = location.longitude

                    if (::googleMap.isInitialized) {
                        showMarker(lat, lng)
                    } else {
                        // Map belum siap, tetap simpan koordinat
                        selectedLatitude = lat
                        selectedLongitude = lng
                    }

                    getAddress(lat, lng)

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Lokasi tidak ditemukan, pastikan GPS aktif",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Gagal mengambil lokasi: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showMarker(lat: Double, lng: Double) {

        selectedLatitude = lat
        selectedLongitude = lng

        val lokasi = LatLng(lat, lng)

        googleMap.clear()

        googleMap.addMarker(
            MarkerOptions()
                .position(lokasi)
                .draggable(true)
                .title("Lokasi Anda")
        )

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                lokasi,
                17f
            )
        )
    }

    private fun getAddress(lat: Double, lng: Double) {

        try {
            val geocoder = Geocoder(requireContext(), Locale("id", "ID"))
            val list = geocoder.getFromLocation(lat, lng, 1)

            if (!list.isNullOrEmpty()) {
                etAlamat.setText(list[0].getAddressLine(0))
            } else {
                Toast.makeText(
                    requireContext(),
                    "Alamat tidak ditemukan untuk lokasi ini",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Geocoder error: ${e.message}")
            Toast.makeText(
                requireContext(),
                "Gagal mendapatkan alamat, coba lagi",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showConfirmationDialog() {

        if (pickupType == "delivery" && etAlamat.text.toString().isBlank()) {

            Toast.makeText(
                requireContext(),
                "Alamat delivery belum diisi, silakan isi atau pilih lokasi di peta",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Order")
            .setMessage("Apakah orderan sudah sesuai?")
            .setPositiveButton("Ya") { _, _ ->

                Log.d(TAG, "===== CHECKOUT DIMULAI =====")

                if (isCheckoutProcessing) {
                    Log.d(TAG, "Checkout masih diproses, dibatalkan")
                    return@setPositiveButton
                }

                isCheckoutProcessing = true
                btnCheckout.isEnabled = false

                val userId =
                    FirebaseAuth.getInstance()
                        .currentUser?.uid ?: "guest"

                Log.d(TAG, "User ID = $userId")

                val cartItems = CartManager.items

                Log.d(TAG, "Jumlah item = ${cartItems.size}")

                cartItems.forEachIndexed { index, item ->

                    Log.d(
                        TAG,
                        """
                    ITEM $index
                    menuId = ${item.menu_id}
                    nama = ${item.nama}
                    harga = ${item.harga}
                    qty = ${item.quantity}
                    addon = ${item.addons.size}
                    """.trimIndent()
                    )
                }

                val total =
                    cartItems.sumOf {
                        (
                                it.harga +
                                        it.addons.sumOf { addon ->
                                            addon.price
                                        }
                                ) * it.quantity
                    }

                Log.d(TAG, "TOTAL = $total")

                val order = Order(
                    userID = userId,
                    total = total,
                    status = "pending",
                    pickupType = pickupType,
                    deliveryAddress = etAlamat.text.toString(),
                    latitude = selectedLatitude,
                    longitude = selectedLongitude
                )

                Log.d(TAG, "Membuat Order Firebase...")

                orderRepository.createOrder(
                    order,
                    cartItems
                ) { orderId ->


                    Log.d(TAG, "===========================================")
                    Log.d(TAG, "ORDER BERHASIL DIBUAT")
                    Log.d(TAG, "Order ID = $orderId")
                    Log.d(TAG, "Total = $total")
                    Log.d(TAG, "User = $userId")
                    Log.d(TAG, "Pickup = $pickupType")
                    Log.d(TAG, "Alamat = $alamat")
                    Log.d(TAG, "===========================================")

                    /*
                    Log.d(TAG, "Meminta Snap Token ke Backend...")

                    orderRepository.createSnapPayment(

                        orderId,

                        total,

                        onSuccess = { snapToken ->

                            Log.d(TAG, "===========================================")
                            Log.d(TAG, "SNAP TOKEN BERHASIL DITERIMA")
                            Log.d(TAG, "Order ID = $orderId")
                            Log.d(TAG, "Snap Token = $snapToken")
                            Log.d(TAG, "===========================================")

                            try {

                                Log.d(TAG, "Membuka Midtrans Snap UI...")
                                UiKitApi.getDefaultInstance().startPaymentUiFlow(
                                    requireActivity(),
                                    snapLauncher,
                                    snapToken
                                )
                                Log.d(TAG, "Midtrans Snap UI berhasil dipanggil")

                            } catch (e: Exception) {

                                Log.e(TAG, "======================================")
                                Log.e(TAG, "GAGAL MEMBUKA SNAP UI")
                                Log.e(TAG, e.message ?: "Unknown Error")
                                Log.e(TAG, "======================================")
                                e.printStackTrace()
                            }

                            Log.d(TAG, "PaymentDialogFragment ditampilkan")

                            OrderSessionManager.lastOrderId = orderId

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

                            isCheckoutProcessing = false
                            btnCheckout.isEnabled = true
                        },

                        onFailed = { error ->

                            Log.e(TAG, "===================================")
                            Log.e(TAG, "GAGAL MEMBUAT QRIS")
                            Log.e(TAG, "Order ID = $orderId")
                            Log.e(TAG, "Error = $error")
                            Log.e(TAG, "===================================")

                            Toast.makeText(
                                requireContext(),
                                error,
                                Toast.LENGTH_LONG
                            ).show()

                            isCheckoutProcessing = false
                            btnCheckout.isEnabled = true
                        }

                    )
                    */
                    /*
                    orderRepository.createQrisPayment(

                        orderId,

                        total,

                        onSuccess = { bank, vaNumber ->
                            PaymentDialogFragment(
                                orderId,
                                total,
                                bank,
                                vaNumber
                            ).show(
                                parentFragmentManager,
                                "payment"
                            )

                            Log.d(TAG, "PaymentDialogFragment ditampilkan")

                            OrderSessionManager.lastOrderId = orderId

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

                            isCheckoutProcessing = false
                            btnCheckout.isEnabled = true
                        },

                        onFailed = { error ->

                            Log.e(TAG, "===================================")
                            Log.e(TAG, "GAGAL MEMBUAT QRIS")
                            Log.e(TAG, "Order ID = $orderId")
                            Log.e(TAG, "Error = $error")
                            Log.e(TAG, "===================================")

                            Toast.makeText(
                                requireContext(),
                                error,
                                Toast.LENGTH_LONG
                            ).show()

                            isCheckoutProcessing = false
                            btnCheckout.isEnabled = true
                        }

                    )
                    */
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
        loadCheckoutItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }
}