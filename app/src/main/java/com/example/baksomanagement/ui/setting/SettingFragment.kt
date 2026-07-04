package com.example.baksomanagement.ui.setting

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import com.google.android.gms.location.LocationServices
import android.content.Intent
import com.example.baksomanagement.MainActivity
import com.example.baksomanagement.utils.SavedAccountManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.baksomanagement.R
import com.example.baksomanagement.data.remote.FirebaseClient
import com.example.baksomanagement.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatDelegate
import com.example.baksomanagement.utils.ThemeManager
import com.google.android.gms.location.FusedLocationProviderClient
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import java.util.Locale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class SettingFragment : Fragment() {

    private val TAG = "SettingFragment"
    private var googleMapEdit: GoogleMap? = null
    private var imageUri: Uri? = null
    private var imgProfile: ImageView? = null
    private var cameraUri: Uri? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseClient.firestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var etAlamatEdit: EditText? = null
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0
    private lateinit var switchNotification: Switch

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                imgProfile?.setImageURI(it)

                Log.d(TAG, "Image dipilih dari galeri: $uri")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                fetchCurrentLocationForEdit()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Izin lokasi diperlukan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchCurrentLocationForEdit() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location == null) {
                    Toast.makeText(
                        requireContext(),
                        "Lokasi tidak ditemukan, aktifkan GPS",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                selectedLatitude = location.latitude
                selectedLongitude = location.longitude

                updateMarkerEdit(selectedLatitude, selectedLongitude)
                reverseGeocodeToEdit(selectedLatitude, selectedLongitude)
            }
    }

    private fun reverseGeocodeToEdit(lat: Double, lng: Double) {

        try {
            val geocoder = Geocoder(requireContext(), Locale("id", "ID"))
            val list = geocoder.getFromLocation(lat, lng, 1)

            if (!list.isNullOrEmpty()) {
                etAlamatEdit?.setText(list[0].getAddressLine(0))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Geocoder error: ${e.message}")
        }
    }

    private fun updateMarkerEdit(lat: Double, lng: Double) {

        val map = googleMapEdit ?: return

        val posisi = LatLng(lat, lng)

        map.clear()

        map.addMarker(
            MarkerOptions()
                .position(posisi)
                .draggable(true)
                .title("Lokasi Delivery")
        )

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(posisi, 17f)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_setting,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupNotificationSwitch(view)

        view.findViewById<View>(R.id.btnAccount).setOnClickListener {
            showEditProfileDialog()
        }

        view.findViewById<View>(R.id.btnLanguage).setOnClickListener {
            Toast.makeText(requireContext(), "Language Setting", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnBackground).setOnClickListener {
            showThemeDialog()
        }

        view.findViewById<View>(R.id.btnChangeAccount).setOnClickListener {
            showChangeAccountDialog()
        }

        view.findViewById<View>(R.id.btnDeleteAccount).setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun setupNotificationSwitch(view: View) {

        val switchNotification = view.findViewById<Switch>(R.id.switchNotification)

        val sharedPref = requireActivity()
            .getSharedPreferences(
                "app_settings",
                AppCompatActivity.MODE_PRIVATE
            )

        val isEnabled = sharedPref.getBoolean(
            "global_notification",
            true
        )

        switchNotification.isChecked = isEnabled

        switchNotification.setOnCheckedChangeListener { _, isChecked ->

            if (!isChecked) {

                AlertDialog.Builder(requireContext())
                    .setTitle("Matikan Notifikasi?")
                    .setMessage(
                        "Notifikasi order tidak akan muncul lagi."
                    )
                    .setPositiveButton("Ya") { _, _ ->

                        sharedPref.edit()
                            .putBoolean(
                                "global_notification",
                                false
                            )
                            .apply()

                        Toast.makeText(
                            requireContext(),
                            "Notifikasi dimatikan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Batal") { _, _ ->

                        switchNotification.isChecked = true
                    }
                    .show()

            } else {

                sharedPref.edit()
                    .putBoolean(
                        "global_notification",
                        true
                    )
                    .apply()

                Toast.makeText(
                    requireContext(),
                    "Notifikasi diaktifkan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun deleteAccount() {

        val user = auth.currentUser ?: return

        val uid = user.uid

        firestore.collection("users")
            .document(uid)
            .delete()
            .addOnSuccessListener {

                user.delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            requireContext(),
                            "Akun berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToFirstPage()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            requireContext(),
                            "Gagal menghapus akun",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }

    private fun goToFirstPage() {

        findNavController().navigate(
            R.id.firstPageFragment
        )
    }

    private fun showDeleteAccountDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Akun")
            .setMessage(
                "Jika akun dihapus maka semua data akan hilang. Apakah Anda yakin?"
            )
            .setPositiveButton("Ya") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showChangeAccountDialog() {

        val accounts =
            SavedAccountManager.getAccounts(requireContext())

        if (accounts.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Belum ada akun tersimpan",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Akun")
            .setItems(accounts.toTypedArray()) { _, which ->
                val selectedEmail = accounts[which]
                auth.signOut()
                SessionManager.clearSession(requireContext())

                Toast.makeText(
                    requireContext(),
                    "Silakan login ke akun: $selectedEmail",
                    Toast.LENGTH_LONG
                ).show()

                val intent =
                    Intent(requireContext(), MainActivity::class.java)

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                requireActivity().finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private val takePictureLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {

                imageUri?.let {

                    imgProfile?.setImageURI(it)

                    Log.d(
                        TAG,
                        "Foto berhasil diambil dari kamera: $it"
                    )
                }
            }
        }

    private fun openCamera() {

        val contentValues = ContentValues().apply {

            put(
                MediaStore.Images.Media.TITLE,
                "profile_picture"
            )

            put(
                MediaStore.Images.Media.DESCRIPTION,
                "From Camera"
            )
        }

        cameraUri =
            requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

        imageUri = cameraUri

        takePictureLauncher.launch(cameraUri)
    }

    private fun showEditProfileDialog() {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_edit_profile,
                null
            )

        imgProfile =
            dialogView.findViewById(R.id.imgProfileEdit)

        val etNama =
            dialogView.findViewById<EditText>(R.id.etNama)

        val etEmail =
            dialogView.findViewById<EditText>(R.id.etEmail)

        val etPhone =
            dialogView.findViewById<EditText>(R.id.etPhone)

        etAlamatEdit = dialogView.findViewById(R.id.etAlamatEdit)
        val btnGunakanLokasiEdit = dialogView.findViewById<Button>(R.id.btnGunakanLokasiEdit)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapContainerEdit)
                    as? SupportMapFragment

        mapFragment?.getMapAsync { map ->

            googleMapEdit = map

            val defaultLocation =
                if (selectedLatitude != 0.0 || selectedLongitude != 0.0)
                    LatLng(selectedLatitude, selectedLongitude)
                else
                    LatLng(-6.2000, 106.8166) // fallback Jakarta

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f)
            )

            map.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .draggable(true)
                    .title("Lokasi Delivery")
            )

            if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
                selectedLatitude = defaultLocation.latitude
                selectedLongitude = defaultLocation.longitude
            }

            map.setOnMarkerDragListener(
                object : GoogleMap.OnMarkerDragListener {

                    override fun onMarkerDragEnd(marker: Marker) {
                        selectedLatitude = marker.position.latitude
                        selectedLongitude = marker.position.longitude
                        reverseGeocodeToEdit(selectedLatitude, selectedLongitude)
                    }

                    override fun onMarkerDrag(marker: Marker) {}
                    override fun onMarkerDragStart(marker: Marker) {}
                }
            )
        }

        btnGunakanLokasiEdit.setOnClickListener {

            val fineGranted = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!fineGranted) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                fetchCurrentLocationForEdit()
            }
        }

        val btnSave =
            dialogView.findViewById<Button>(R.id.btnSave)

        val uid =
            auth.currentUser?.uid ?: return

        val dialog =
            AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

        dialog.show()
        dialog.setOnDismissListener {
            googleMapEdit = null
            selectedLatitude = 0.0
            selectedLongitude = 0.0
        }
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->

                etNama.setText(doc.getString("nama"))
                etEmail.setText(doc.getString("email"))
                etPhone.setText(doc.getString("noTelp"))
                etAlamatEdit?.setText(doc.getString("alamat"))
                selectedLatitude = doc.getDouble("latitude") ?: 0.0
                selectedLongitude = doc.getDouble("longitude") ?: 0.0
                if (selectedLatitude != 0.0 || selectedLongitude != 0.0) {
                    updateMarkerEdit(selectedLatitude, selectedLongitude)
                }
                val imageUrl =
                    doc.getString("profilePicture")

                if (!imageUrl.isNullOrEmpty()) {

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_account_)
                        .circleCrop()
                        .into(imgProfile!!)
                }
            }

        imgProfile!!.setOnClickListener {

            val options =
                arrayOf("Kamera", "Galeri")

            AlertDialog.Builder(requireContext())
                .setTitle("Pilih Foto")
                .setItems(options) { _, which ->

                    when (which) {

                        0 -> openCamera()

                        1 -> pickImageLauncher.launch("image/*")
                    }
                }
                .show()
        }

        btnSave.setOnClickListener {

            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val alamat = etAlamatEdit?.text.toString()

            if (imageUri != null) {

                uploadProfileImage(imageUri!!) { imageUrl ->

                    updateUser(
                        uid,
                        nama,
                        email,
                        phone,
                        alamat,
                        selectedLatitude,
                        selectedLongitude,
                        imageUrl
                    )

                    dialog.dismiss()
                }

            } else {

                updateUser(
                    uid,
                    nama,
                    email,
                    phone,
                    alamat,
                    selectedLatitude,
                    selectedLongitude,
                    null
                )

                dialog.dismiss()
            }
        }
    }

    private fun uploadProfileImage(
        uri: Uri,
        onComplete: (String) -> Unit
    ) {

        MediaManager.get()
            .upload(uri)
            .option("folder", "profile_images")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {}

                override fun onProgress(
                    requestId: String?,
                    bytes: Long,
                    totalBytes: Long
                ) {}

                override fun onSuccess(
                    requestId: String?,
                    resultData: Map<*, *>?
                ) {

                    val imageUrl =
                        resultData?.get("secure_url").toString()

                    onComplete(imageUrl)
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {

                    Toast.makeText(
                        requireContext(),
                        "Upload gagal",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {}
            })
            .dispatch()
    }

    private fun updateUser(
        uid: String,
        nama: String,
        email: String,
        phone: String,
        alamat: String,
        latitude: Double,
        longitude: Double,
        imageUrl: String?
    ) {

        val updateMap =
            mutableMapOf<String, Any>(
                "nama" to nama,
                "email" to email,
                "noTelp" to phone,
                "alamat" to alamat,
                "latitude" to latitude,
                "longitude" to longitude,
                "updatedAt" to System.currentTimeMillis()
            )

        imageUrl?.let {
            updateMap["profilePicture"] = it
        }

        firestore.collection("users")
            .document(uid)
            .update(updateMap)
            .addOnSuccessListener {

                auth.currentUser?.updateEmail(email)

                Toast.makeText(
                    requireContext(),
                    "Profile berhasil diupdate",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showThemeDialog() {

        val options = arrayOf(
            "Light Mode",
            "Dark Mode"
        )

        val currentTheme =
            ThemeManager.getTheme(requireContext())

        val checkedItem =
            when (currentTheme) {
                ThemeManager.DARK -> 1
                else -> 0
            }

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Background Mode")
            .setSingleChoiceItems(
                options,
                checkedItem
            ) { dialog, which ->

                val selectedTheme =
                    if (which == 0)
                        ThemeManager.LIGHT
                    else
                        ThemeManager.DARK

                ThemeManager.saveTheme(
                    requireContext(),
                    selectedTheme
                )

                ThemeManager.applyTheme(
                    selectedTheme
                )

                dialog.dismiss()

                Toast.makeText(
                    requireContext(),
                    "Theme berhasil diubah",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}